package dev.kosmx.dropper

import dev.kosmx.dropper.auth.AdminAuthenticationPlugin
import dev.kosmx.dropper.data.DataAccess
import dev.kosmx.dropper.routing.admin.adminRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.Resources
import io.ktor.server.routing.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


private object KoinHelper: KoinComponent {
    val dbView: DataAccess
        get() = get()
}

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

private fun Application.module() {

    routing {

        install(ContentNegotiation) {

        }

        route("/admin") {
            install(Resources)

            install(AdminAuthenticationPlugin) {
                withTokenVerify { call, token ->
                    KoinHelper.dbView.checkAdminToken(token)
                }
            }

            adminRouting()
        }

        route("/auth") {
            // no need to do this for admin
        }
    }
}
