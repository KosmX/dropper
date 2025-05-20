package dev.kosmx.dropper.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private fun internalGetProperty(property: String): String? = js("localStorage.getItem(\"property\")")
private fun internalSetProperty(property: String, value: String?) {
    js("localStorage.setItem(property, value)")
}

actual class LocalDataStore {

    actual fun setToken(token: String?) {
        mutableToken.value = token
        internalSetProperty("token", token)
    }

    actual val authToken: StateFlow<String?>
        get() = mutableToken

    private val mutableToken: MutableStateFlow<String?> by lazy { MutableStateFlow(getToken()) }

    private val mutableServer = MutableStateFlow<String?>(getServer())

    actual val serverAddress: StateFlow<String?>
        get() = mutableServer

    actual fun setServerAddress(address: String?) {
        mutableServer.value = address
        internalSetProperty("server", address)
    }

    private fun getToken(): String? = internalGetProperty("token")

    private fun getServer(): String? = internalGetProperty("server")

}