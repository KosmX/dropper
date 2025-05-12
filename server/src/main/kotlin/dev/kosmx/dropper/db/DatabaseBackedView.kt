package dev.kosmx.dropper.db

import dev.kosmx.dropper.crypto.CryptoTool
import dev.kosmx.dropper.data.DataAccess
import dev.kosmx.dropper.data.FileEntry
import dev.kosmx.dropper.data.ShareSession
import dev.kosmx.dropper.data.Upload
import dev.kosmx.dropper.weird.NopOutputStream
import io.ktor.util.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.security.DigestOutputStream
import java.security.MessageDigest
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.io.path.createDirectories
import kotlin.io.path.isDirectory
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

@OptIn(ExperimentalContracts::class)
inline fun <T, P: Any> T.ifNotNull(p: P?, block: T.(P) -> T): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return if (p != null) {
        block(p)
    } else this
}


@OptIn(ExperimentalUuidApi::class)
class DatabaseBackedView(
    val cryptoTool: CryptoTool,
    val dataRoot: Path,
): DataAccess {

    private val fsMutex = Mutex()

    override fun checkAdminToken(token: String): Boolean {
        return transaction {
            AdminTable.select(AdminTable.id).where { AdminTable.id eq token }.any() // i don't care about what it is, only whether it is
        }
    }

    override fun getAdminClientList(): List<Pair<String, String>> {
        return transaction {
            AdminTable.selectAll().map { row ->
                row[AdminTable.id] to row[AdminTable.name]
            }.toList()
        }
    }

    override fun addAdminClient(name: String): String {
        return transaction {
            AdminTable.insertReturning {
                it[AdminTable.name] = name
            }.first()[AdminTable.id]
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun getSessions(
        count: Int,
        page: Int,
        nameContains: String?
    ): List<ShareSession> = transaction {
        SessionTable.selectAll().run {
            if (nameContains != null) {
                where { SessionTable.name like nameContains }
            } else {
                this
            }
        }.take(count * (page + 1)).drop(count * page).toShareSession().toList()
    }

    override fun getSession(sessionId: ByteArray): ShareSession? = transaction {
        SessionTable.selectAll().where { SessionTable.publicID eq sessionId }.firstOrNull()?.toShareSession()
    }

    override fun getSession(sessionId: Long): ShareSession? = transaction {
        SessionTable.selectAll().where { SessionTable.id eq sessionId }.firstOrNull()?.toShareSession()
    }

    @OptIn(ExperimentalTime::class)
    override fun updateSession(session: ShareSession): Boolean = transaction {
        val updated = SessionTable.update({
            (SessionTable.id eq session.id).let {
                if (session.publicID.isNotEmpty()) {
                    it and (SessionTable.publicID eq session.publicID.decodeBase64Bytes())
                } else it
            }

        }) {
            writeSession(it, session)
        }

        return@transaction updated > 0
    }

    @OptIn(ExperimentalTime::class)
    override fun createSession(newSession: ShareSession): ShareSession = transaction {
        val (private, public) = cryptoTool.createTokens()
        val session = newSession.copy(
            privateID = private.encodeBase64(),
            publicID = public.encodeBase64()
        )
        val id = SessionTable.insert {
            it[SessionTable.publicID] = public
            it[secretString] = session.privateID!!.decodeBase64Bytes()
            it[name] = session.name
            it[expirationDate] = session.expirationDate.toJavaInstant().toKotlinInstant()
            it[allowMultipleFiles] = session.allowMultipleFiles
            it[uploadLimit] = session.uploadLimit
            it[overrideUpload] = session.overrideReupload
        }
        return@transaction session.copy(id = id[SessionTable.id].value)
    }

    @OptIn(ExperimentalTime::class)
    override fun insertSession(newSession: ShareSession): Long = transaction {
        val id = SessionTable.insert {
            it[SessionTable.publicID] = newSession.publicID.decodeBase64Bytes()
            it[secretString] = newSession.privateID!!.decodeBase64Bytes()
            it[name] = newSession.name
            it[expirationDate] = newSession.expirationDate.toJavaInstant().toKotlinInstant()
            it[allowMultipleFiles] = newSession.allowMultipleFiles
            it[uploadLimit] = newSession.uploadLimit
            it[overrideUpload] = newSession.overrideReupload
        }
        return@transaction id[SessionTable.id].value
    }

    override fun deleteSession(sessionID: Long): Boolean {
        val deleted = SessionTable.deleteWhere {
            SessionTable.id eq sessionID
        }
        return deleted > 0
    }

    override fun verifySession(
        sessionSecret: ByteArray,
        sessionID: ByteArray?
    ): ShareSession? {
        if (sessionID != null && !cryptoTool.verify(sessionSecret, sessionID)) {
            return null
        }

        val pub = sessionID ?: cryptoTool.genPublic(sessionSecret)
        return transaction {
            SessionTable.select(SessionTable.id).where {
                SessionTable.publicID eq pub
            }.firstOrNull()?.toShareSession()
        }

    }

    override suspend fun getUploads(
        count: Int,
        page: Int,
        sessionNameContains: String?,
        contentNameContains: String?,
        session: Long?
    ): List<Upload> = transaction {
        UploadTable.join(FileTable, joinType = JoinType.LEFT)
            .run { if (sessionNameContains != null) join(SessionTable, joinType = JoinType.INNER) else this }
            .selectAll().ifNotNull(sessionNameContains) {
                andWhere {
                    SessionTable.name like it
                }
            }
            .ifNotNull(contentNameContains) {
                andWhere {
                    FileTable.filename like it
                }
            }
            .ifNotNull(session) {
                andWhere {
                    UploadTable.uploaderSession eq it
                }
            }
            .take(page * (count + 1)).drop(page).toUpload().toList()
    }

    override suspend fun getUploads(session: Long): List<Upload> = transaction {
        UploadTable.join(FileTable, joinType = JoinType.LEFT).selectAll()
            .where {
                UploadTable.uploaderSession eq session
            }.toUpload().toList()
    }

    override suspend fun getUpload(id: Long): Upload? = transaction {
        UploadTable.join(FileTable, joinType = JoinType.LEFT).selectAll()
            .where {
                UploadTable.id eq id
            }.toUpload().firstOrNull()
    }

    override suspend fun addUpload(
        session: Long?,
        upload: Upload,
        content: List<File>
    ): Upload = coroutineScope {
        // filecache
        val hash = MessageDigest.getInstance("SHA-256")
        val hashes = content.map { tmpfile ->
            hash.reset()
            tmpfile.inputStream().buffered().use { input ->
                val hasher = DigestOutputStream(NopOutputStream, hash)
                input.transferTo(hasher)
            }
            hash.digest()!!
        }
        val strHashes = hashes.map { it.encodeBase64() }

        // synchronize
        return@coroutineScope fsMutex.withLock {
            val createdFiles = mutableListOf<File>()
            try {
                // store non-existing files
                for (i in hashes.indices) {
                    val fileHash = strHashes[i]
                    val dir = dataRoot.resolve(fileHash.substring(0, endIndex = 4))
                    if (!dir.isDirectory()) {
                        dir.createDirectories()
                    }

                    val file = dir.resolve(fileHash).toFile()
                    if (!file.isFile) {
                        file.outputStream().buffered().use { output -> content[i].inputStream().use { input -> input.transferTo(output) } }
                        createdFiles += file
                    }
                }

                transaction {
                    // create upload entry
                    val id = UploadTable.insert {
                        it[UploadTable.uploaderSession] = upload.uploadSession
                        it[UploadTable.uploadDate] = upload.uploadDate
                    }[UploadTable.id]

                    // file entries
                    val files= FileTable.batchInsert(upload.files.asSequence().mapIndexed { i, f ->
                        f.copy(
                            hash = hashes[i],
                            size = Files.size(content[i].toPath()),
                        )
                    } ) { entry ->
                        this[FileTable.filename] = entry.name
                        this[FileTable.upload] = id
                        this[FileTable.fileDate] = entry.fileDate
                        this[FileTable.fileSize] = entry.size
                        this[FileTable.hash] = entry.hash
                    }.map { result ->
                        FileEntry(
                            name = result[FileTable.filename],
                            fileDate = result[FileTable.fileDate],
                            size = result[FileTable.fileSize],
                            id = result[FileTable.id].value.toKotlinUuid(),
                            hash = result[FileTable.hash],
                        )
                    }


                    return@transaction upload.copy(id = id.value, files = files)
                }

            } catch (e: Throwable) {
                createdFiles.forEach { file -> file.delete() }
                throw e
            }
        }
    }

    override suspend fun deleteUpload(id: Long): Boolean = fsMutex.withLock {
        transaction {
            val list = UploadTable.leftJoin(FileTable).selectAll().where { UploadTable.id eq id }.toList()
            for (e in list) {
                deleteFileTransaction(e[FileTable.id].value)
            }

            if (list.isNotEmpty()) {
                UploadTable.deleteWhere { UploadTable.id eq id }
                true
            } else false
        }
    }


    /**
     *
     */
    private fun deleteFileTransaction(id: UUID): Boolean {
        val deleted = FileTable.deleteReturning { FileTable.id eq id }.firstOrNull()?.get(FileTable.hash) ?: return false

        val none = FileTable.select(FileTable.id).where { FileTable.hash eq deleted }.none()
        if(none) {
            val delHash = deleted.encodeBase64()
            dataRoot.resolve(delHash.substring(0, endIndex = 4), delHash).toFile().delete()
        }
        return true
    }

    override suspend fun findFile(
        filename: String?,
        extension: String?
    ): List<Upload> = transaction {
        TODO("c:")
    }

    override suspend fun deleteFile(id: Uuid): Boolean = fsMutex.withLock { transaction { deleteFileTransaction(id.toJavaUuid()) } }

    override suspend fun getFileContent(id: Uuid): InputStream? {
        TODO("Not yet implemented")
    }
}