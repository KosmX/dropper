package dev.kosmx.dropper.db

import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.*
import kotlin.random.asKotlinRandom
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

private val charset = byteArrayOf(
    65,  66,  67,  68,  69,  70,  71,  72,  73,  74,  75,  76,  77,  78,  79,  80,  /* 0 - 15 */
    81,  82,  83,  84,  85,  86,  87,  88,  89,  90,  97,  98,  99,  100, 101, 102, /* 16 - 31 */
    103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, /* 32 - 47 */
    119, 120, 121, 122, 48,  49,  50,  51,  52,  53,  54,  55,  56,  57,  45,  95,  /* 48 - 63 */
)

/**
 * A string with exactly 192 bits of entropy (if length is 32)
 * basically unbreakable
 */
private fun randomString(length: Int): String {
    val random = SecureRandom.getInstanceStrong().asKotlinRandom()
    val result = ByteBuffer.allocate(length)
    for (i in 0 ..< length) {
        result.put(charset.random(random))
    }
    return result.array().toString(Charsets.US_ASCII).also { assert(it.length == length) { "oops" } }
}

/**
 * Table storing allowed admins
 */
object AdminTable: Table("admin") {
    val id: Column<String> = varchar("id", length = 32).clientDefault {
        randomString(32)
    }
    // this is just an ID store

    /**
     * Admin client name, just to make my life easier
     */
    val name: Column<String> = text("name")

    override val primaryKey = PrimaryKey(id)
}


object SessionTable: IdTable<UUID>("session") {
    @OptIn(ExperimentalUuidApi::class)
    override val id: Column<EntityID<UUID>> = uuid("key").entityId()

    /**
     * 36 bytes long random secret.The key is its bcrypt "hash"
     */
    val secureString: Column<ByteArray> = binary("secure", length = 36)

    val name: Column<String> = text("name")

    @OptIn(ExperimentalTime::class)
    val expirationDate: Column<Instant> = timestamp("expiration")

    val allowMultipleFiles: Column<Boolean> = bool("multiple_files")
    val uploadLimit: Column<Int> = integer("upload_limit")

    val overrideUpload: Column<Boolean> = bool("override_upload")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

object UploadTable: LongIdTable("upload") {
    val uploadDate: Column<Instant> = timestamp("instant")

    val uploaderSession:Column<EntityID<UUID>?> = optReference("session", SessionTable)
}

object FileTable: IdTable<UUID>("file") {
    /**
     * File UUID. generated randomly.
     */
    override val id: Column<EntityID<UUID>> = uuid("id").clientDefault {
        UUID.randomUUID()
    }.entityId()

    val filename: Column<String> = text("filename")
    val fileDate: Column<Instant> = timestamp("timestamp")

    /**
     * The uploader block. If that entry is deleted, all files must be also deleted.
     * However, files must be deleted before deleting the upload entry to avoid leaving files behind.
     */
    val upload: Column<EntityID<Long>> = reference("upload", UploadTable, onDelete = ReferenceOption.RESTRICT)

    override val primaryKey: PrimaryKey = PrimaryKey(id)


}