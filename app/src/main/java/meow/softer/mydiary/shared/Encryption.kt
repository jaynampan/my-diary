package meow.softer.mydiary.shared

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Encryption {
    fun Encrypt(str: String, encryptionMethod: String): String? {
        var encoded: String?
        try {
            val md = MessageDigest.getInstance(encryptionMethod)
            md.update(str.toByteArray())
            val byteData = md.digest()
            val sb = StringBuilder()
            for (aByteData in byteData) {
                sb.append(((aByteData.toInt() and 0xff) + 0x100).toString(16).substring(1))
            }
            encoded = sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            encoded = null
        }
        return encoded
    }

    fun SHA256(str: String): String? {
        return Encrypt(str, "SHA-256")
    }
}
