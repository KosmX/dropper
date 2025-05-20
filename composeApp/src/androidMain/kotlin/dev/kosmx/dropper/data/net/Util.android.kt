package dev.kosmx.dropper.data.net

import io.ktor.client.*
import io.ktor.client.engine.cio.*

actual fun platformClient(): HttpClient = HttpClient(CIO)