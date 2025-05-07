package dev.kosmx.dropper.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class ShareSession @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class) constructor(

    /**
     * Session public ID. This is displayed on QR code, and needed to do authentication
     */
    val id: Uuid,

    /**
     * Private session key, only shared if link share is used. This keeps QR based sessions secure. The server and upload knows it, but the QR does not include this.
     */
    val sessionKey: String?,

    /**
     * Optional name of share.
     */
    val name: String?,

    /**
     * Session can be used until this instant
     */
    @SerialName("expirationDate")
    val expirationDateString: String,

    /**
     * Allow multi-file or folder upload
     */
    val allowMultipleFiles: Boolean = true,

    /**
     * Should be expired after one upload, maybe more, or only expire after expiration date
     */
    val uploadLimit: Int = 1, // set to -1 to allow infinity,

    /**
     * Uploading again should overwrite old files (true), or create a new upload entry
     */
    val overrideReupload: Boolean = false
) {
    @OptIn(ExperimentalTime::class)
    val expirationDate by lazy { Instant.parse(expirationDateString) }


    /**
     * Special copy method that can copy with expiration date instant
     */
    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    fun copy(
        id: Uuid = this.id,
        sessionKey: String? = this.sessionKey,
        name: String? = this.name,
        expirationDate: Instant, // no default, if this entry is not modified, the auto-generated copy should be used
        allowMultipleFiles: Boolean = this.allowMultipleFiles,
        uploadLimit: Int = this.uploadLimit,
        overrideReupload: Boolean = this.overrideReupload,
    ) = this.copy(id, sessionKey, name, expirationDate.toString(), allowMultipleFiles, uploadLimit, overrideReupload)
}