package dev.kosmx.dropper.data.net

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

actual fun platformClient(): HttpClient = HttpClient(CIO)