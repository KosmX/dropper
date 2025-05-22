package dev.kosmx.dropper.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {

    @Serializable
    class Create: Screen

    @Serializable
    class Authorize(val id: Long): Screen

    @Serializable
    data class Session(val session: Long? = null): Screen

    @Serializable
    data class Upload(val upload: Long? = null): Screen

    @Serializable
    class Settings: Screen
}