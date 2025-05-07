package dev.kosmx.dropper.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*


class AdminAuthConfig(
    var tokenHeader: String = "Authorization",
    var prefix: String = "Bearer",
) {

    internal lateinit var authFunction: suspend (PipelineCall, String) -> Boolean

    fun withTokenVerify(verifyFun: suspend (PipelineCall, String) -> Boolean) {
        authFunction = verifyFun
    }
}

internal class NotAuthorizedException(error: String): RuntimeException(error)

val AdminAuthenticationPlugin = createRouteScopedPlugin(name = "admin auth", { AdminAuthConfig() }) {
    val authFunction = pluginConfig.authFunction
    val tokenHeader = pluginConfig.tokenHeader
    val tokenPrefix = pluginConfig.prefix
    val pattern = Regex("${Regex.escape(tokenPrefix)} (?<token>.+)")

    onCall { call ->
        try {
            val header = call.request.header(tokenHeader) ?: throw NotAuthorizedException("Missing token header")
            val match = pattern.matchEntire(header) ?: throw NotAuthorizedException("Malformed authorization token")

            if (!authFunction(call, match.groups["token"]!!.value)) {
                throw NotAuthorizedException(HttpStatusCode.Unauthorized.description)
            }

        } catch (e: NotAuthorizedException) {
            call.respond(HttpStatusCode.Unauthorized, "${e.message}")
        }
    }
}