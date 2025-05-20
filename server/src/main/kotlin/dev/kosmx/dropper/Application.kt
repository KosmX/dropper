package dev.kosmx.dropper

import dev.kosmx.dropper.auth.AdminAuthenticationPlugin
import dev.kosmx.dropper.config.Config
import dev.kosmx.dropper.crypto.CryptoTool
import dev.kosmx.dropper.crypto.Sha256CryptoTool
import dev.kosmx.dropper.data.DataAccess
import dev.kosmx.dropper.db.AdminTable
import dev.kosmx.dropper.db.DatabaseBackedView
import dev.kosmx.dropper.db.DatabaseLoader
import dev.kosmx.dropper.routing.admin.adminRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.Resources
import io.ktor.server.routing.*
import kotlinx.io.files.FileNotFoundException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.dsl.module
import kotlin.io.path.Path


private object KoinHelper: KoinComponent {
    val dbView: DataAccess
        get() = get()
}

val dataModule = module {
    single<CryptoTool> { Sha256CryptoTool() }
    single<DataAccess> { DatabaseBackedView(get(), kotlin.io.path.Path("data")) }
}


val modules = module {
    includes(dataModule)
}

@OptIn(ExperimentalSerializationApi::class)
fun main() {
    startKoin {
        modules(modules)
    }

    val configFile = Path("dropper.config.json").toFile()!!
    val configJson = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }
    // open
    val config: Config = try {
        configFile.inputStream().buffered().use { stream -> configJson.decodeFromStream(stream) }
    } catch (e: FileNotFoundException) {
        val c = Config()
        configFile.outputStream().buffered().use { stream -> configJson.encodeToStream(c, stream) }
        c
    }

    DatabaseLoader.loadDB(config.db)

    // sanity thingy
    transaction {
        if (AdminTable.selectAll().none()) {
            println("The server did not have any admin, generating one now...")
            val admin = KoinHelper.dbView.addAdminClient("Auto-generated admin")
            println("Token for auto-generated admin account: $admin")
        }
    }

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
