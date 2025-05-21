package dev.kosmx.dropper.data

import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


/**
 * Aggregate all views into one interface
 */
interface DataAccess: AdminAccess, SessionAccess, UploadAccess {
    suspend fun ping(): HttpResponse
}

@Serializable
data class Admin(
    val name: String,
    val token: String
)

expect class FileInput

/**
 * Operations with admin device tokens
 */
interface AdminAccess {

    /**
     * list of admin clients
     */
    suspend fun getAdminClientList(): List<Admin>

    /**
     * Adds a new admin client
     * @param name of client
     * @return the newly created Admin
     */
    suspend fun addAdminClient(name: String): Admin

    suspend fun deleteAdminClient(token: String): Boolean
}

/**
 * Operations with session thingies
 */
interface SessionAccess {
    /**
     * Sessions might blow up, we need proper paginating
     */
    suspend fun getSessions(count: Int = 32, page: Int = 0, nameContains: String? = null): List<ShareSession>

    /**
     * Get a session by public ID
     */
    suspend fun getSession(sessionId: ByteArray): ShareSession?

    /**
     * Get a session by internal ID
     */
    suspend fun getSession(sessionId: Long): ShareSession?

    /**
     * update an existing session
     * warning: it will not update the session ID
     * @return if something was updated
     */
    suspend fun updateSession(session: ShareSession): Boolean

    /**
     * Creates a new session, session UUID and key is ignored, the function will return a session with the actual key.
     * This session will contain the secret key
     */
    suspend fun createSession(newSession: ShareSession): ShareSession


    /**
     * Inserts a new session. This is done without storing its secret key.
     */
    suspend fun insertSession(newSession: ShareSession): Long

    /**
     * Delete the session with its internal Long ID, returns true if something was deleted
     */
    suspend fun deleteSession(sessionID: Long): Boolean

}

/**
 * Operations with uploads, files, etc...
 *
 * Because some of these methods may require synchronized filesystem operations, functions are suspending
 */
@OptIn(ExperimentalUuidApi::class)
interface UploadAccess {
    /**
     * Get a list of upload entries with file list and everything. Can be filtered with non-null parameters
     */
    suspend fun getUploads(count: Int = 32, page: Int = 0, sessionNameContains: String? = null, contentNameContains: String? = null, session: Long? = null): List<Upload>

    /**
     * All uploads made by this session
     */
    suspend fun getUploads(session: Long): List<Upload>

    /**
     * retrieves an upload entry
     */
    suspend fun getUpload(id: Long): Upload?

    /**
     * Adds a new upload with files. Session may be null if upload is originated from admin (i guess)
     * [content] must have the same length as the file count in [upload].
     * files will be re-hashed, hashes in [Upload.files] will be ignored.
     * @param content list of tmpfiles containing all the content. This function does **not** delete these
     * @return the fixed Upload instance (with correct ID and file sashes)
     */
    suspend fun addUpload(session: Long?, upload: Upload, content: List<FileInput>): Upload

    /**
     * Delete an upload by its internal ID
     * @return true if it affected anything
     */
    suspend fun deleteUpload(id: Long): Boolean

    // files

    /**
     * Finds a file, returns with the corresponding upload
     */
    suspend fun findFile(filename: String? = null, extension: String? = null): List<Upload>

    /**
     * Deletes a single file from an upload. (idk why would anyone do this tbh)
     * @return true if deleted
     */
    suspend fun deleteFile(id: Uuid): Boolean

    /**
     * @return a single file (if exists
     */
    suspend fun getFile(id: Uuid): FileEntry?

    /**
     * @return content of file with [id], null if not found
     */
    suspend fun getFileContent(id: Uuid): FileInput?
}
