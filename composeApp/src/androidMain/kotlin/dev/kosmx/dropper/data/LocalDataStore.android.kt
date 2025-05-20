package dev.kosmx.dropper.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.FileNotFoundException

actual class LocalDataStore: KoinComponent {
    private val context: Context by inject()


    actual val authToken: StateFlow<String?>
        get() = mutableToken

    private val mutableToken by lazy { MutableStateFlow( getToken() ) }

    actual fun setToken(token: String?) {
        mutableToken.value = token
        if (token != null) {
            context.openFileOutput("token", Context.MODE_PRIVATE).bufferedWriter().use { it.write(token) }
        } else {
            context.deleteFile("token")
        }
    }

    private fun getToken(): String? {
        return try {
            context.openFileInput("token").bufferedReader().use { it.readText() }
        } catch (_: FileNotFoundException) {
            null
        }
    }

    private val address = MutableStateFlow(getAddress())

    actual val serverAddress: StateFlow<String?>
        get() = address

    private fun getAddress(): String? {
        return try {
            context.openFileInput("server").bufferedReader().use { it.readText() }
        } catch (_: FileNotFoundException) {
            null
        }
    }

    actual fun setServerAddress(address: String?) {
        this.address.value = address
        if (address != null) {
            context.openFileOutput("server", Context.MODE_PRIVATE).bufferedWriter().use { it.write(address) }
        } else {
            context.deleteFile("server")
        }
    }

}