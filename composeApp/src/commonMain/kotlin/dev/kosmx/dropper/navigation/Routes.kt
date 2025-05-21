package dev.kosmx.dropper.navigation

sealed interface Screen {
    class Create: Screen

    data class Session(val session: Long? = null): Screen

    data class Upload(val upload: Long? = null): Screen
}