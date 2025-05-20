package dev.kosmx.dropper.data.net

import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect fun platformClient(): HttpClient

fun provideHttpBaseClient(server: String, token: String) = platformClient().config {

    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = false
            }
        )
    }
    install(Auth) {
        bearer {
            loadTokens {
                BearerTokens(
                    token,
                    null
                )
            }
        }
    }
    install(Resources) {
    }
    defaultRequest {
        url(server)
    }
}
