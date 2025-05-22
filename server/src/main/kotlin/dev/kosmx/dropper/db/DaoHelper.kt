package dev.kosmx.dropper.db

import dev.kosmx.dropper.data.FileEntry
import dev.kosmx.dropper.data.ShareSession
import dev.kosmx.dropper.data.Upload
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateStatement
import java.util.Base64
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toKotlinUuid

fun ResultRow.toShareSession(): ShareSession {
    val row = this

    return ShareSession(
        id = row[SessionTable.id].value,
        publicID = row[SessionTable.publicID].encodeBase64(),
        privateID = row[SessionTable.secretString]?.encodeBase64(),
        name = row[SessionTable.name],
        expirationDateString = row[SessionTable.expirationDate].toString(),
        allowMultipleFiles = row[SessionTable.allowMultipleFiles],
        uploadLimit = row[SessionTable.uploadLimit],
        overrideReupload = row[SessionTable.overrideUpload]
    )
}

fun Iterable<ResultRow>.toShareSession(): Iterable<ShareSession> = map { it.toShareSession() }

@OptIn(ExperimentalTime::class)
fun SessionTable.writeSession(it: UpdateStatement, session: ShareSession) {
    if (session.privateID != null) {
        it[secretString] = session.privateID!!.decodeBase64Bytes()
    }
    it[name] = session.name
    it[expirationDate] = session.expirationDate.toJavaInstant().toKotlinInstant()
    it[allowMultipleFiles] = session.allowMultipleFiles
    it[uploadLimit] = session.uploadLimit
    it[overrideUpload] = session.overrideReupload
}




@OptIn(ExperimentalUuidApi::class)
fun Iterable<ResultRow>.toUpload(): Iterable<Upload> = map { row ->

    val upload = Upload(
        id = row[UploadTable.id].value,
        files = emptyArray(),
        uploadDate = row[UploadTable.uploadDate]
    )

    val file = FileEntry(
        name = row[FileTable.filename],
        fileDate = row[FileTable.fileDate],
        size = row[FileTable.fileSize],
        id = row[FileTable.id].value.toKotlinUuid(),
        hash = row[FileTable.hash],
    )

    upload to file
}.groupBy(keySelector = { it.first }, valueTransform = { it.second }).map { (upload, files) ->
    upload.copy(
        files = files.toTypedArray()
    )
}