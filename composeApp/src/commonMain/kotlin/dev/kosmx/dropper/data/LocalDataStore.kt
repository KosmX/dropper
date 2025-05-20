package dev.kosmx.dropper.data

import kotlinx.coroutines.flow.StateFlow


/**
 * Strictly locally stored data
 */
expect class LocalDataStore {
    val authToken: StateFlow<String?>

    fun setToken(token: String?)

    val serverAddress: StateFlow<String?>
    fun setServerAddress(address: String?)
}