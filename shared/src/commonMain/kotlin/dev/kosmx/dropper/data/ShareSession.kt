package dev.kosmx.dropper.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

@Serializable
data class ShareSession @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class) constructor(

    /**
     * Internal session identifier. Only for admin-device and server communication
     */
    val id: Long,

    /**
     * public session key, derived from [privateID], exact way of creating this is TBD, maybe RSA or module-lattice, or a simple SHA-256
     */
    val publicID: String,

    /**
     * Private session key, only shared if link share is used. This keeps QR based sessions secure. The uploader knows it, but the QR does not include this.
     * Nullable, maybe even the server won't know it.
     */
    val privateID: String? = null,

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
        id: Long = this.id,
        publicID: String = this.publicID,
        privateID: String? = this.privateID,
        name: String? = this.name,
        expirationDate: Instant, // no default, if this entry is not modified, the auto-generated copy should be used
        allowMultipleFiles: Boolean = this.allowMultipleFiles,
        uploadLimit: Int = this.uploadLimit,
        overrideReupload: Boolean = this.overrideReupload,
    ) = this.copy(
        id,
        publicID,
        privateID,
        name,
        expirationDate.toString(),
        allowMultipleFiles,
        uploadLimit,
        overrideReupload
    )
}