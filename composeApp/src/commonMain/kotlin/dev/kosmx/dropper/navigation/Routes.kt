package dev.kosmx.dropper.navigation

import dev.kosmx.dropper.data.ShareSession
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen() {

    @Serializable
    object Create: Screen()

    @Serializable
    class Authorize(val id: String?): Screen()

    @Serializable
    data class Session(val session: Long? = null): Screen()

    @Serializable
    data class Upload(val upload: Long? = null): Screen()

    @Serializable
    object Settings: Screen()

    @Serializable
    data class DisplayLink(val publicID: String, val privateID: String): Screen() {
        constructor(session: ShareSession): this(session.publicID, session.privateID!!)
    }

    @Serializable
    data class Update(val id: Long): Screen()
}