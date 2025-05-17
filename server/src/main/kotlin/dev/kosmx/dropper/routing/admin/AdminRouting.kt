package dev.kosmx.dropper.routing.admin

import dev.kosmx.dropper.data.Admin
import dev.kosmx.dropper.data.DataAccess
import dev.kosmx.dropper.data.ShareSession
import dev.kosmx.dropper.routing.admin.types.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.post
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.uuid.ExperimentalUuidApi

object AdminRouting: KoinComponent {
    val access: DataAccess by inject()
}

@OptIn(ExperimentalUuidApi::class)
fun Route.adminRouting() {
    route("admins") {
        get<AdminList> {
            call.respond(AdminRouting.access.getAdminClientList())
        }

        post<AdminAdd> { adminInfo ->
            val token = AdminRouting.access.addAdminClient(adminInfo.name)
            call.respond(Admin(adminInfo.name, token))
        }

        post<AdminDelete> { adminInfo ->
            val success = AdminRouting.access.deleteAdminClient(adminInfo.token)
            if (success) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }

    route("sessions") {
        get<GetSessions> { filter ->
            val sessions = AdminRouting.access.getSessions(
                count = filter.count,
                page = filter.page,
                nameContains = filter.nameContains,
            )
            call.respond(sessions)
        }

        get<SessionById> { param ->
            val session = AdminRouting.access.getSession(param.idBytes)
            if (session != null) {
                call.respond(session)
            } else call.respond(HttpStatusCode.NotFound)
        }

        get<SessionByCode> { param ->
            val session = AdminRouting.access.getSession(param.id)
            if (session != null) {
                call.respond(session)
            } else call.respond(HttpStatusCode.NotFound)
        }

        post<UpdateSession> {
            val newSession = call.receive<ShareSession>()
            val up = AdminRouting.access.updateSession(newSession)
            if (up) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post<CreateSession> {
            val newSession = AdminRouting.access.createSession(call.receive<ShareSession>())
            call.respond(newSession)
        }

        post<InsertSession> {
            val newSession = AdminRouting.access.insertSession(call.receive<ShareSession>())
            call.respond(HttpStatusCode.OK, newSession)
        }

        post<DeleteSession> { params ->
            val del = AdminRouting.access.deleteSession(params.id)
            if (del) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }


    }

    route("uploads") {
        get<GetUploads> { filter ->
            val uploads = AdminRouting.access.getUploads(
                count = filter.count,
                page = filter.page,
                sessionNameContains = filter.sessionNameContains,
                contentNameContains = filter.contentNameContains,
                session = filter.session,
            )
            call.respond(uploads)
        }

        get<GetUploadsBySession> { params ->
            val uploads = AdminRouting.access.getUploads(params.id)
            call.respond(uploads)
        }

        get<Session.Get> { params ->
            val r = AdminRouting.access.getUpload(params.parent.id)
            if (r != null) {
                call.respond(r)
            } else call.respond(HttpStatusCode.NotFound)
        }

        post<Session.Delete> {
            if (AdminRouting.access.deleteUpload(it.parent.id)) {
                call.respond(HttpStatusCode.OK)
            } else call.respond(HttpStatusCode.NotFound)
        }

        get<FindFile> { params ->
            val files = AdminRouting.access.findFile(
                filename = params.filename,
            )
            call.respond(files)

        }

        get<FileRoute.Info> {
            val info = AdminRouting.access.getFile(it.parent.id)
            if (info != null) {
                call.respond(info)
            } else call.respond(HttpStatusCode.NotFound)
        }

        get<FileRoute.Content> {
            TODO("large file serving")
        }

        post<FileRoute.Delete> {
            if (AdminRouting.access.deleteFile(it.parent.id)) {
                call.respond(HttpStatusCode.OK)
            } else call.respond(HttpStatusCode.NotFound)
        }
    }
}