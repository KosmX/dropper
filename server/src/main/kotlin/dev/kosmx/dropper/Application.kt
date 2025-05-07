package dev.kosmx.dropper

import dev.kosmx.dropper.auth.AdminAuthenticationPlugin
import dev.kosmx.dropper.routing.admin.adminRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

private fun Application.module() {

    routing {

        install(ContentNegotiation) {

        }

        route("/admin") {
            install(AdminAuthenticationPlugin) {
                withTokenVerify { call, token ->
                    TODO("Use DB to verify client")
                }
            }
            adminRouting()
        }
    }
}