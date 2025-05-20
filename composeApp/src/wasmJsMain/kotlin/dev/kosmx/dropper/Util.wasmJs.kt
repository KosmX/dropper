package dev.kosmx.dropper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * No IO here, this is always default
 */
actual fun getIODispatcher(): CoroutineDispatcher = Dispatchers.Default