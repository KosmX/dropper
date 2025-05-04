package dev.kosmx.dropper

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform