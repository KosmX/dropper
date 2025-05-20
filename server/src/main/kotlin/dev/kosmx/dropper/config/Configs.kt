package dev.kosmx.dropper.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val db: DatabaseAccess = DatabaseAccess()
)

@Serializable
data class DatabaseAccess(
    val address: String = "jdbc:sqlite:data.db",
    val driver: String = "org.sqlite.JDBC",
    val username: String = "",
    val password: String = "",
) {
    override fun toString(): String {
        return "DatabaseAccess(address='$address', driver='$driver', username='$username')"
    }
}