package dev.kosmx.dropper.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories

actual class LocalDataStore {

    private val file = Path(System.getProperty("user.home")).resolve(".dropper", "token").toFile()
    private val serverFile = Path(System.getProperty("user.home")).resolve(".dropper", "server").toFile()

    init {
        file.toPath().createParentDirectories()
    }

    actual val authToken: StateFlow<String?>
        get() = token

    private val token: MutableStateFlow<String?> by lazy { MutableStateFlow(getToken()) }

    private fun getToken(): String? {
        return if (file.isFile) {
            file.readText()
        } else {
            null
        }
    }

    actual fun setToken(token: String?) {
        this.token.value = token
        if (token != null) {
            file.writeText(token)
        } else {
            file.delete()
        }
    }

    private val serverAddr = MutableStateFlow(getAddress())

    actual val serverAddress: StateFlow<String?>
        get() = serverAddr

    actual fun setServerAddress(address: String?) {
        serverAddr.value = address
        if (address != null) {
            serverFile.writeText(address)
        } else {
            serverFile.delete()
        }
    }

    private fun getAddress(): String? {
        return if (serverFile.isFile) {
            serverFile.readText()
        } else {
            null
        }
    }
}