package dev.kosmx.dropper.data

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class File @OptIn(ExperimentalUuidApi::class) constructor(

    /**
     * File name, must be a valid UNIX UTF8 name. (no / allowed)
     */
    val name: String,

    /**
     * File creation or last edit date. The uploader client must specify it.
     */
    val fileDate: Long, // seconds since Unix epoch,

    /**
     * Unique ID for this file, this is used on storage, and when downloading a single file.
     * also for storage name
     */
    val id: Uuid? = null,
) {
    init {
        require(name.none { it == '/' }) {}
    }
}

/**
 * Represents one upload
 * it may contain multiple files, it has an owner session (who uploaded it)
 */
data class Upload @OptIn(ExperimentalUuidApi::class) constructor(
    /**
     * List of files in this upload. Must be non-empty
     */
    val files: List<File>,

    /**
     * When did the upload happen (finished)
     */
    val uploadDate: Long
)
