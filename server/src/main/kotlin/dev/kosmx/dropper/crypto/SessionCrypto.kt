package dev.kosmx.dropper.crypto

import dev.kosmx.dropper.getValue
import dev.kosmx.dropper.threadLocal
import java.security.Key
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.spec.KeySpec

interface CryptoTool {
    /**
     * @return `(private, public)`
     */
    fun createTokens(keyLength: Int = 36): Pair<ByteArray, ByteArray>

    fun verify(secret: ByteArray, public: ByteArray): Boolean

    fun genPublic(secret: ByteArray): ByteArray
}

private const val ML_KEM = "ML-KEM"

private inline fun <reified T: KeySpec> KeyFactory.getKeySpec(key: Key): T =
    getKeySpec(key, T::class.java)

class Sha256CryptoTool(
    val random: SecureRandom = SecureRandom.getInstanceStrong()
): CryptoTool {

    private val digest by threadLocal { MessageDigest.getInstance("SHA-256") }

    private fun createRandomArray(length: Int): ByteArray {
        return ByteArray(length).also { random.nextBytes(it) }
    }


    override fun createTokens(keyLength: Int): Pair<ByteArray, ByteArray> {
        val private = createRandomArray(36)
        return private to genPublic(private)
    }

    override fun verify(secret: ByteArray, public: ByteArray): Boolean {
        val digest = digest
        digest.reset()
        return digest.digest(secret).contentEquals(public)
    }

    override fun genPublic(secret: ByteArray): ByteArray {
        val digest = digest
        digest.reset()
        return digest.digest(secret)
    }


}
