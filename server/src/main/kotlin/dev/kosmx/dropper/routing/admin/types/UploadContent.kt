@file:OptIn(ExperimentalUuidApi::class)
package dev.kosmx.dropper.routing.admin.types

import io.ktor.resources.Resource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Resource("list")
class GetUploads(
    val count: Int = 32,
    val page: Int = 0,
    val sessionNameContains: String? = null,
    val contentNameContains: String? = null,
    val session: Long? = null
)

@Resource("ofSession/{id}")
class GetUploadsBySession(val id: Long)


@Resource("session/{id}")
class Session(val id: Long) {
    @Resource("get")
    class Get(val parent: Session)

    @Resource("delete")
    class Delete(val parent: Session)
}

// TODO @Resource("addUpload") this is going to be websocket stuff here, or idk

@Resource("findFile")
class FindFile(val filename: String? = null)

@Resource("file/{id}")
class FileRoute(val id: Uuid) {
    @Resource("info")
    class Info(val parent: FileRoute)

    @Resource("content")
    class Content(val parent: FileRoute)

    @Resource("delete")
    class Delete(val parent: FileRoute)
}
