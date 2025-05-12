package utils

// Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

// Segurança e codificação
import java.security.SecureRandom
import android.util.Base64
//log
import android.util.Log
import androidx.compose.runtime.Composable
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object ChaveAesUtils {


    fun gerarChaveAesBase64(): String {               // Gera uma chave AES de 256 bits (32 bytes) e retorna em formato Base64.
        val chave = ByteArray(32)               // Essa chave será usada para criptografar/descriptografar as senhas do usuário.
        SecureRandom().nextBytes(chave)
        return Base64.encodeToString(chave, Base64.NO_WRAP)
    }

    fun recuperarChaveDoUsuario(               // Recupera a chave AES do usuário autenticado no Firestore
        uid: String,
        db:FirebaseFirestore=Firebase.firestore,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val firestore = Firebase.firestore

        firestore.collection("users")           //  Uma função assíncrona, um callback de sucesso e erro.
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val chave = doc.getString("AESkey")
                if (chave != null) {
                    onSuccess(chave)
                } else {
                    onFailure(Exception("Chave AES não encontrada para o usuário $uid"))
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}
