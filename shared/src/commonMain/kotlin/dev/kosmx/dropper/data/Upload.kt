package dev.kosmx.dropper.data

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class FileEntry @OptIn(ExperimentalUuidApi::class) constructor(

    /**
     * File name, must be a valid UNIX UTF8 name. (no `/` is allowed)
     */
    val name: String,

    /**
     * File creation or last edit date. The uploader client must specify it.
     */
    val fileDate: Long, // seconds since Unix epoch,

    /**
     * exact byte length of this file
     */
    val size: Long,

    /**
     * Unique ID for this file, this is used on storage, and when downloading a single file.
     * also for storage name
     */
    val id: Uuid,

    /**
     * File SHA-256 hash
     */
    val hash: ByteArray,

) {
    init {
        require(name.none { it == '/' }) {}
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FileEntry

        if (fileDate != other.fileDate) return false
        if (size != other.size) return false
        if (name != other.name) return false
        if (id != other.id) return false
        if (!hash.contentEquals(other.hash)) return false

        return true
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun hashCode(): Int {
        var result = fileDate.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + hash.contentHashCode()
        return result
    }

    fun sizeAsText(): String = when {
        size < 4096 -> "$size B"
        size < 1024 * 4096 -> "${size / 1024} K"
        size < 1024 * 4096 * 1024 -> "${size / 1024 / 1024} M"
        size < 1024 * 1024 * 4096 * 1024 -> "${size / 1024 / 1024 / 1024} G"
        else -> "${size / 1024 / 1024 / 1024 / 1024} T"
    }
}

/**
 * Represents one upload
 * it may contain multiple files, it has an owner session (who uploaded it)
 */
data class Upload @OptIn(ExperimentalUuidApi::class) constructor(

    /**
     * Unique internal ID
     */
    val id: Long,

    /**
     * List of files in this upload. Must be non-empty
     */
    val files: List<FileEntry>,

    /**
     * When did the upload happen (finished)
     */
    val uploadDate: Long,

    /**
     * Session ID of uploader
     */
    val uploadSession: Long? = null
)
