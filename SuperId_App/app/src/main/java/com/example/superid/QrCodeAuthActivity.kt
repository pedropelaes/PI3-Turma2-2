package com.example.superid

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.provider.Settings
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.example.superid.ui.theme.SuperIdTheme
import com.example.superid.ui.theme.ui.common.LoginAndSignUpDesign
import com.example.superid.ui.theme.ui.common.SuperIdTitlePainterVerified
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import utils.CriptoUtils

class QrCodeAuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperIdTheme() {
                QrCodeScannerScreen()
            }
        }
    }
}
@Composable
fun QrCodeScannerScreen(
    context: Context = LocalContext.current
) {
    var cameraPermissionDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraPermissionDenied = false
            iniciarLeituraQr(context)
        } else {
            cameraPermissionDenied = true
            Toast.makeText(context, "Permissão da câmera negada", Toast.LENGTH_SHORT).show()
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Escanear QR Code", fontSize = 16.sp)
            }

            if (cameraPermissionDenied) {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(45.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Permitir câmera nas configurações", fontSize = 14.sp)
                }
            }
        }

        // Botão de voltar
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
            SearchLoginDocument(valor)
            monitorarStatusAprovado(valor, context)
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

fun monitorarStatusAprovado(loginToken: String, context: Context) {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    db.collection("login")
        .whereEqualTo("loginToken", loginToken)
        .get()
        .addOnSuccessListener { result ->
            if (!result.isEmpty) {
                val document = result.documents.first()
                val docRef = document.reference

                docRef.update("uid", currentUser?.uid)

                docRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("STATUS", "Erro ao monitorar status", error)
                        return@addSnapshotListener
                    }

                    val status = snapshot?.getString("status")
                    if (status.equals("aprovado", ignoreCase = true)) {
                        Toast.makeText(context, "Login aprovado", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Log.e("LOGINSEMSENHA", "Nenhum documento de login encontrado")
            }
        }
        .addOnFailureListener { exception ->
            Log.e("LOGINSEMSENHA", "Erro ao encontrar documento de login", exception)
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
        .document("Sites")
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