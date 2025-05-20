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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class QrCodeAuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            SuperIdTheme(darkTheme = isSystemInDarkTheme()) {
                SearchLoginDocument("zfxxikumxnwa0elpknm5ahrjudouczpae0zisfeegd2z818upbejxkjsri2dplyzz2eftatg64o18v3cwyxx57bjmawgicl1mj7hb7rcq7d2dxewe6o0iz7ntve0z88i2qgootrsb9mwzvvo5thd55yidul1e3y6sinfjjyaevwm5krlhxmyb8ldhhozpglfs36hn11euh1a5s9pg71446kd3xrfb5sn7w1pkbj0cgjjgz0jgz51gifvfffilg8o")
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
                document.reference.update("uid", currentUser?.uid)
                    .addOnSuccessListener {
                        Log.d("LOGINSEMSENHA", "UID adicionado com sucesso ao documento ${document.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("LOGINSEMSENHA", "Falha ao atualizar UID no documento", e)
                    }
                Log.d("LOGINSEMSENHA", "Documento encontrado ${document.id}")
            }else{
                Log.e("LOGINSEMSENHA", "Nenhum documento de login encontrado")
            }
        }
        .addOnFailureListener { exception->
            Log.e("LOGINSEMSENHA", "Erro ao encontrar documento de login no banco", exception)
        }

}