package dev.kosmx.dropper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual fun getIODispatcher(): CoroutineDispatcher = Dispatchers.IO