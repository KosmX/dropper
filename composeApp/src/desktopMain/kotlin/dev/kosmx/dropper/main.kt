package dev.kosmx.dropper

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.kosmx.dropper.di.initKoin
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "dropper",
    ) {
        startKoin {
            initKoin()
        }
        App()
    }
}