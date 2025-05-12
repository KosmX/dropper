package dev.kosmx.dropper.weird

import java.io.OutputStream

object NopOutputStream: OutputStream() {
    override fun write(b: Int) {}

    override fun write(b: ByteArray) {}

    override fun write(b: ByteArray, off: Int, len: Int) {}

    override fun toString() = "NopOutputStream"
}