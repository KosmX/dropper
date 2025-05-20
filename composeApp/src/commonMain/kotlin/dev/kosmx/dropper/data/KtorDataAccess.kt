package dev.kosmx.dropper.data

import dev.kosmx.dropper.resources.AdminList
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class KtorDataAccess(
    private val client: HttpClient
): DataAccess {
    override suspend fun ping(): HttpResponse {
        return client.get(urlString = "ping")
    }

    override suspend fun getAdminClientList(): List<Admin> = client.get(AdminList()).body<List<Admin>>()

    override suspend fun addAdminClient(name: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAdminClient(token: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getSessions(
        count: Int,
        page: Int,
        nameContains: String?
    ): List<ShareSession> {
        TODO("Not yet implemented")
    }

    override suspend fun getSession(sessionId: ByteArray): ShareSession? {
        TODO("Not yet implemented")
    }

    override suspend fun getSession(sessionId: Long): ShareSession? {
        TODO("Not yet implemented")
    }

    override suspend fun updateSession(session: ShareSession): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun createSession(newSession: ShareSession): ShareSession {
        TODO("Not yet implemented")
    }

    override suspend fun insertSession(newSession: ShareSession): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSession(sessionID: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun verifySession(
        sessionSecret: ByteArray,
        sessionID: ByteArray?
    ): ShareSession? {
        TODO("Not yet implemented")
    }

    override suspend fun getUploads(
        count: Int,
        page: Int,
        sessionNameContains: String?,
        contentNameContains: String?,
        session: Long?
    ): List<Upload> {
        TODO("Not yet implemented")
    }

    override suspend fun getUploads(session: Long): List<Upload> {
        TODO("Not yet implemented")
    }

    override suspend fun getUpload(id: Long): Upload? {
        TODO("Not yet implemented")
    }

    override suspend fun addUpload(
        session: Long?,
        upload: Upload,
        content: List<FileInput>
    ): Upload {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUpload(id: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun findFile(
        filename: String?,
        extension: String?
    ): List<Upload> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFile(id: Uuid): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getFile(id: Uuid): FileEntry? {
        TODO("Not yet implemented")
    }

    override suspend fun getFileContent(id: Uuid): FileInput? {
        TODO("Not yet implemented")
    }
}