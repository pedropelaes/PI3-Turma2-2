package com.example.superid

import android.content.Context
import android.os.Bundle
import android.text.Layout
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
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class QrCodeAuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            SuperIdTheme(darkTheme = isSystemInDarkTheme()) {
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
