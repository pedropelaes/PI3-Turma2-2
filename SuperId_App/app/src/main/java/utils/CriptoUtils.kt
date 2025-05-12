package utils

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CriptoUtils {

    fun generateIV(): ByteArray {               // gera um IV (vetor de inicialização) aleatório de 16 bytes
        val iv = ByteArray(16)             // vamos usar o IV pra conseguir decodificar (AES/CBC)
        SecureRandom().nextBytes(iv)            // por isso ele precisa ser salvo junto com a senha criptografada
        return iv
    }

    private fun generateAccessToken(): String {
        val randomBytes = ByteArray(192)   // Gera um token aleatório de 256 caracteres (usado como accessToken da senha).
        SecureRandom().nextBytes(randomBytes)
        return Base64.encodeToString(randomBytes, Base64.NO_WRAP)
    }

    fun encrypt(text: String, secretKey: SecretKey): Triple<String, String, String> {
        val iv = generateIV()
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")     // Criptografa a senha com AES/CBC/PKCS5Padding usando uma chave (criada junto com a conta) e IV
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

        val encryptedBytes = cipher.doFinal(text.toByteArray(Charsets.UTF_8))

        // Gera um token aleatório (256 caracteres em Base64)
        val accessToken = generateAccessToken()

        return Triple(                                                          // Retorna um Triple com: senha criptografada, IV usado em Base64, token aleatório.
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP),
            Base64.encodeToString(iv, Base64.NO_WRAP),
            accessToken
        )
    }

    // Descriptografa a senha usando a chave, o IV e a string criptografada.
    fun decrypt(encryptedText: String, ivBase64: String, secretKey: SecretKey): String {
        val iv = Base64.decode(ivBase64, Base64.NO_WRAP)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))

        val decryptedBytes = cipher.doFinal(Base64.decode(encryptedText, Base64.NO_WRAP))
        return String(decryptedBytes, Charsets.UTF_8)
    }

    // Converte uma string Base64 de chave (vinda do Firestore) para uma SecretKey AES (AESkey).
    fun base64ToSecretKey(base64Key: String): SecretKey {
        val decodedKey = Base64.decode(base64Key, Base64.NO_WRAP)
        return SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
    }
}

