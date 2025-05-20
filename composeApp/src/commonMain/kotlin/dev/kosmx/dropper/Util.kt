package dev.kosmx.dropper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

expect fun getIODispatcher(): CoroutineDispatcher

fun IOScope(): CoroutineScope = CoroutineScope(Util.IO)

object Util {
    val IO = getIODispatcher()
}
