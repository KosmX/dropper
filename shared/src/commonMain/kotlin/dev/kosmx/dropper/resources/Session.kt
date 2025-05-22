package dev.kosmx.dropper.resources

import io.ktor.resources.Resource
import io.ktor.util.decodeBase64Bytes

@Resource("list")
class GetSessions(val count: Int = 32, val page: Int = 0, val nameContains: String? = null)

@Resource("byId")
class SessionById(val id: String) {
    val idBytes = id.decodeBase64Bytes()
}

@Resource("byCode")
class SessionByCode(val id: Long)

@Resource("update")
object UpdateSession // parameter in content

@Resource("create")
object CreateSession // parameter in content

@Resource("insert")
object InsertSession

@Resource("delete")
class DeleteSession(val id: Long)

