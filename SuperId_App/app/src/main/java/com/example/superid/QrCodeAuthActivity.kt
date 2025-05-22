package com.example.superid

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.ui.theme.SuperIdTheme
import com.example.superid.ui.theme.ui.common.LoginAndSignUpDesign
import com.example.superid.ui.theme.ui.common.SuperIdTitlePainterVerified
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class QrCodeAuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
    var shouldRequestPermission by remember { mutableStateOf(true) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        shouldRequestPermission = false
        if (isGranted) {
            iniciarLeituraQr(context)
        } else {
            Toast.makeText(context, "Permissão da câmera negada", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (shouldRequestPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LoginAndSignUpDesign {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SuperIdTitlePainterVerified()

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(50.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Escanear QR Code", fontSize = 16.sp)
            }
        }

        // Botão de voltar (canto inferior esquerdo)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Row(
                modifier = Modifier.clickable {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Voltar",
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp
                )
            }
        }
    }
}

fun iniciarLeituraQr(context: Context) {
    val options = GmsBarcodeScannerOptions.Builder()
        .enableAutoZoom()
        .allowManualInput()
        .build()

    val scanner = GmsBarcodeScanning.getClient(context, options)

    scanner.startScan()
        .addOnSuccessListener { barcode ->
            val valor = barcode.displayValue ?: barcode.rawValue ?: ""

            // Chama a função que busca no Firestore e trata o status
            SearchLoginDocument(valor, context)
        }
        .addOnFailureListener { e ->
            val mensagem = when (e) {
                is MlKitException -> when (e.errorCode) {
                    MlKitException.CODE_SCANNER_CAMERA_PERMISSION_NOT_GRANTED -> "Permissão da câmera não concedida"
                    MlKitException.CODE_SCANNER_APP_NAME_UNAVAILABLE -> "Nome do app não disponível"
                    else -> "Erro desconhecido: ${e.message}"
                }
                else -> e.message ?: "Erro desconhecido"
            }
            Toast.makeText(context, mensagem, Toast.LENGTH_LONG).show()
        }
        .addOnCanceledListener {
            Toast.makeText(context, "Escaneamento cancelado", Toast.LENGTH_SHORT).show()
        }
}

fun SearchLoginDocument(loginToken: String, context: Context) {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    db.collection("login")
        .whereEqualTo("loginToken", loginToken)
        .get()
        .addOnSuccessListener { result ->
            if (!result.isEmpty) {
                val document = result.documents.first()
                Log.d("LOGINSEMSENHA", "Documento encontrado ${document.id}")

                // Atualiza o UID (opcional, pode ser antes ou depois)
                document.reference.update("uid", currentUser?.uid)
                    .addOnSuccessListener {
                        Log.d("LOGINSEMSENHA", "UID adicionado com sucesso ao documento ${document.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("LOGINSEMSENHA", "Falha ao atualizar UID no documento", e)
                    }

                // Escuta mudanças no documento em tempo real
                document.reference.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("LOGINSEMSENHA", "Erro no listener de status", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val status = snapshot.getString("status")
                        if (status.equals("aprovado", ignoreCase = true)) {
                            Toast.makeText(context, "Login aprovado", Toast.LENGTH_LONG).show()
                        }
                    }
                }

            } else {
                Log.e("LOGINSEMSENHA", "Nenhum documento de login encontrado")
                Toast.makeText(context, "Login inválido", Toast.LENGTH_LONG).show()
            }
        }
        .addOnFailureListener { exception ->
            Log.e("LOGINSEMSENHA", "Erro ao encontrar documento de login no banco", exception)
            Toast.makeText(context, "Erro ao acessar o banco de dados", Toast.LENGTH_LONG).show()
        }
}
