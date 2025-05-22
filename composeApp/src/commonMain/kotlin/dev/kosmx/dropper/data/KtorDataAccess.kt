package dev.kosmx.dropper.data

import dev.kosmx.dropper.resources.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class KtorDataAccess(
    client: HttpClient
): DataAccess {

    val adminClient = client.config {
        defaultRequest {
            url("admin/")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
    }
    val admins = adminClient.config {
        defaultRequest {
            url("admins/")
        }
    }
    val sessions = adminClient.config {
        defaultRequest {
            url("sessions/")
        }
    }
    val uploads = adminClient.config {
        defaultRequest {
            url("uploads/")
        }
    }

    /**
     * 2xx status = OK
     */
    private fun HttpResponse.ok(): Boolean = status.value/100 == 2

    private suspend inline fun <reified T: Any> HttpResponse.bodyOrNull(): T? {
        return if (status == HttpStatusCode.OK) {
            body<T>()
        } else {
            null
        }
    }

    override suspend fun ping(): HttpResponse {
        return adminClient.get(urlString = "ping")
    }

    override suspend fun getAdminClientList(): Array<Admin> = admins.get(AdminList()).body<Array<Admin>>()

    override suspend fun addAdminClient(name: String): Admin = admins.post(AdminAdd(name)).body()

    override suspend fun deleteAdminClient(token: String): Boolean = admins.post(AdminDelete(token)).ok()

    override suspend fun getSessions(
        count: Int,
        page: Int,
        nameContains: String?
    ): Array<ShareSession> = sessions.get(GetSessions(count, page, nameContains)).body()

    override suspend fun getSession(sessionId: ByteArray): ShareSession? = sessions.get(SessionById(sessionId.encodeBase64())).bodyOrNull()

    override suspend fun getSession(sessionId: Long): ShareSession? = sessions.get(SessionByCode(sessionId)).bodyOrNull()

    override suspend fun updateSession(session: ShareSession): Boolean = sessions.post(UpdateSession) {
        this.setBody(session)
    }.ok()

    override suspend fun createSession(newSession: ShareSession): ShareSession = sessions.post(CreateSession) {
        setBody(newSession)
    }.body()

    override suspend fun insertSession(newSession: ShareSession): Long = sessions.post(InsertSession) {
        setBody(newSession)
    }.body()

    override suspend fun deleteSession(sessionID: Long): Boolean = sessions.post(DeleteSession(sessionID)).ok()


    override suspend fun getUploads(
        count: Int,
        page: Int,
        sessionNameContains: String?,
        contentNameContains: String?,
        session: Long?
    ): Array<Upload> = uploads.get(GetUploads(count, page, sessionNameContains, contentNameContains, session)).body()

    override suspend fun getUploads(session: Long): Array<Upload> = uploads.get(GetUploadsBySession(session)).body()

    override suspend fun getUpload(id: Long): Upload? = uploads.get(Session.Get(Session(id))).body()

    override suspend fun addUpload(
        session: Long?,
        upload: Upload,
        content: Array<FileInput>
    ): Upload {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUpload(id: Long): Boolean = uploads.post(Session.Delete(Session(id))).ok()

    override suspend fun findFile(
        filename: String?,
        extension: String?
    ): Array<Upload> = uploads.get(FindFile(filename)).body()

    override suspend fun deleteFile(id: Uuid): Boolean = uploads.post(FileRoute.Delete(FileRoute(id))).ok()

    override suspend fun getFile(id: Uuid): FileEntry? = uploads.get(FileRoute.Info(FileRoute(id))).bodyOrNull()

    override suspend fun getFileContent(id: Uuid): FileInput? {
        TODO("Not yet implemented")
    }
}