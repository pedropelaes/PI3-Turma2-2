package com.example.superid

import android.content.Context
import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.superid.ui.theme.SuperIdTheme
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import utils.CriptoUtils

class QrCodeAuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            SuperIdTheme(darkTheme = isSystemInDarkTheme()) {
                SearchLoginDocument("9gsx8ei1mwutdwd6r6pghco4u960zmko5f9wbzcc2zlh2rqzppxy6h2l7vjy7qw18bn25bix4v2ijydf3nimdlpr3eupwvflo7nri23wmz0s0fztt3god183hvzi3zlwfvzwwo8zi1dlwy2d7ckhqd8aqakn1lsew369lsyzdkb208mcrghum9b2rzhliw677sn6kiej350lpy57hzjj8mqdnxhubrl5u28jtiaeqj12kid4n6c3vucj50x88z29")
                QrCodeScannerScreen()
            }
        }
    }
}
@Composable
fun QrCodeScannerScreen(
    context: Context = LocalContext.current
) {
    var scanResult by remember { mutableStateOf("Resultado do QR Code aparecerá aqui") }

    // Inicia a câmera automaticamente
    LaunchedEffect(Unit) {
        val optionsBuilder = GmsBarcodeScannerOptions.Builder()
            .enableAutoZoom()
            .allowManualInput()

        val scanner = GmsBarcodeScanning.getClient(context, optionsBuilder.build())
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                scanResult = """
                    Display Value: ${barcode.displayValue}
                    Raw Value: ${barcode.rawValue}
                    Format: ${barcode.format}
                    Value Type: ${barcode.valueType}
                """.trimIndent()
            }
            .addOnFailureListener { e ->
                scanResult = when (e) {
                    is MlKitException -> when (e.errorCode) {
                        MlKitException.CODE_SCANNER_CAMERA_PERMISSION_NOT_GRANTED ->
                            "Permissão da câmera não concedida"
                        MlKitException.CODE_SCANNER_APP_NAME_UNAVAILABLE ->
                            "Nome do app não disponível"
                        else -> "Erro desconhecido: ${e.message}"
                    }
                    else -> e.message ?: "Erro desconhecido"
                }
            }
            .addOnCanceledListener {
                scanResult = "Escaneamento cancelado"
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = scanResult,
            modifier = Modifier.padding(16.dp),
            fontSize = 18.sp
        )

        Button(onClick = {
            // Botão extra para escanear novamente
            val optionsBuilder = GmsBarcodeScannerOptions.Builder()
                .enableAutoZoom()
                .allowManualInput()

            val scanner = GmsBarcodeScanning.getClient(context, optionsBuilder.build())
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    scanResult = """
                        Display Value: ${barcode.displayValue}
                        Raw Value: ${barcode.rawValue}
                        Format: ${barcode.format}
                        Value Type: ${barcode.valueType}
                    """.trimIndent()
                    barcode.rawValue?.let { SearchLoginDocument(it) }
                }
                .addOnFailureListener { e ->
                    scanResult = when (e) {
                        is MlKitException -> when (e.errorCode) {
                            MlKitException.CODE_SCANNER_CAMERA_PERMISSION_NOT_GRANTED ->
                                "Permissão da câmera não concedida"
                            MlKitException.CODE_SCANNER_APP_NAME_UNAVAILABLE ->
                                "Nome do app não disponível"
                            else -> "Erro desconhecido: ${e.message}"
                        }
                        else -> e.message ?: "Erro desconhecido"
                    }
                }
                .addOnCanceledListener {
                    scanResult = "Escaneamento cancelado"
                }
        }) {
            Text("Escanear novamente")
        }
    }
}

fun SearchLoginDocument(loginToken: String){
    val db = Firebase.firestore
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    db.collection("login")
        .whereEqualTo("loginToken", loginToken)
        .get()
        .addOnSuccessListener {result->
            if(!result.isEmpty){
                val document = result.documents.first()
                Log.d("LOGINSEMSENHA", "Documento encontrado ${document.id}")
                val url = document.get("site")
                if(currentUser?.uid != null) {
                    GetAccessToken(
                        url.toString(),
                        currentUser.uid,
                        db,
                        loginDocumentRef = document.reference
                    )
                }
            }else{
                Log.e("LOGINSEMSENHA", "Nenhum documento de login encontrado")
            }
        }
        .addOnFailureListener { exception->
            Log.e("LOGINSEMSENHA", "Erro ao encontrar documento de login no banco", exception)
        }

}

fun GetAccessToken(
    url: String,
    uid: String,
    db: FirebaseFirestore,
    loginDocumentRef: DocumentReference
){
    db.collection("users")
        .document(uid)
        .collection("categorias")
        .document("sites")
        .collection("senhas")
        .whereEqualTo("url", url)
        .get()
        .addOnSuccessListener { result ->
            if(!result.isEmpty) {
                Log.d("LOGINSEMSENHA", "Senha localizada")
                val document = result.documents.first()
                val accessToken = document.get("accessToken")
                loginDocumentRef.update(
                    hashMapOf(
                        "uid" to uid,
                        "accessToken" to accessToken
                    )
                ).addOnSuccessListener {
                    Log.d("LOGINSEMSENHA", "UID e accessToken adicionados com sucesso ao documento login")
                    val newAccessToken = CriptoUtils.generateAccessToken()
                    document.reference.update("accessToken", newAccessToken)
                        .addOnSuccessListener {
                            Log.d("LOGINSEMSENHA", "accessToken atualizado no documento da senha")
                        }.addOnFailureListener { e->
                            Log.e("LOGINSEMSENHA", "Falha ao atualizar accessToken no documento da senha", e)
                        }
                }.addOnFailureListener { e ->
                    Log.e("LOGINSEMSENHA", "Falha ao atualizar documento login", e)
                }
            }else{
                Log.e("LOGINSEMSENHA", "Nenhuma senha encontrada para essa url")
            }
        }.addOnFailureListener { error ->
            Log.e("LOGINSEMSENHA", "Erro ao localizar senha", error)
        }
}