package dev.kosmx.dropper

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun getLogger(): ReadOnlyProperty<Any, Logger> {
    return object : ReadOnlyProperty<Any, Logger> {
        var logger: Logger? = null
        override fun getValue(thisRef: Any, property: KProperty<*>): Logger {
            if (logger == null) {
                logger = LoggerFactory.getLogger(thisRef::class.java)
            }
            return logger!!
        }
    }
}


inline fun Logger.debug(lazyMessage: () -> String) {
    if (isDebugEnabled) {
        debug(lazyMessage())
    }
}

inline fun Logger.info(lazyMessage: () -> String) {
    if (isInfoEnabled) {
        info(lazyMessage())
    }
}

inline fun Logger.warn(lazyMessage: () -> String) {
    if (isWarnEnabled) {
        warn(lazyMessage())
    }
}

inline fun Logger.error(lazyMessage: () -> String) {
    if (isErrorEnabled) {
        error(lazyMessage())
    }
}

inline fun Logger.debug(t: Throwable, lazyMessage: () -> String) {
    if (isDebugEnabled) {
        debug(lazyMessage(), t)
    }
}

inline fun Logger.info(t: Throwable, lazyMessage: () -> String) {
    if (isInfoEnabled) {
        info(lazyMessage(), t)
    }
}

inline fun Logger.warn(t: Throwable, lazyMessage: () -> String) {
    if (isWarnEnabled) {
        warn(lazyMessage(), t)
    }
}

inline fun Logger.error(t: Throwable, lazyMessage: () -> String) {
    if (isErrorEnabled) {
        error(lazyMessage(), t)
    }
}


fun <T> threadLocal(ctor: () -> T): ThreadLocal<T> = ThreadLocal.withInitial(ctor)

operator fun <T> ThreadLocal<T>.getValue(thisRef: Any?, property: KProperty<*>): T = get()
