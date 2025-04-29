package com.example.superid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.ui.theme.ui.common.LoginAndSignUpDesign
import com.example.superid.ui.theme.ui.common.SuperIdTitle
import com.example.superid.ui.theme.ui.common.TextFieldDesignForLoginAndSignUp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class PasswordReset : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginAndSignUpDesign(imageResId = R.drawable.lockers_background_dark) {
                PasswordResetScreen()
            }
        }
    }
}

@Composable
fun PasswordResetScreen() {
    var email by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SuperIdTitle()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Recuperar Senha",
            fontFamily = FontFamily.SansSerif,
            fontSize = 30.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Por favor, insira o email associado à sua conta e nós enviaremos um link de recuperação de senha",

            fontFamily = FontFamily.SansSerif,
            fontSize = 15.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextFieldDesignForLoginAndSignUp(
            value = email,
            onValueChange = { email = it },
            label = "Digite seu e-mail"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                sendPasswordResetEmail(email) { success, error ->
                    if (success) {
                        emailSent = true
                        errorMessage = ""
                    } else {
                        emailSent = false
                        errorMessage = error ?: "Erro desconhecido."
                    }
                }
            },
            enabled = email.isNotEmpty(),
            border = BorderStroke(2.dp, Color.White),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF152034).copy(alpha = 0.5f),
                disabledContentColor = Color.DarkGray
            ),
            modifier = Modifier
                .height(45.dp)
                .width(220.dp)
        ) {
            Text("Enviar e-mail")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (emailSent) {
            Text(
                "E-mail enviado! Verifique sua caixa de entrada.",
                color = Color.Green,
                fontWeight = FontWeight.Bold
            )
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                errorMessage,
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        TextButton(
            onClick = {
                val intent = Intent(context, LogInActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.BottomStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = Color.White
            )
            Text("Voltar", fontSize = 15.sp, color = Color.White, textDecoration = TextDecoration.Underline)
        }
    }


}

fun sendPasswordResetEmail(email: String, onResult: (Boolean, String?) -> Unit) {
    val auth = Firebase.auth
    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("PasswordReset", "Email de recuperação enviado.")
                onResult(true, null)
            } else {
                Log.e("PasswordReset", "Erro ao enviar email: ${task.exception?.message}")
                onResult(false, task.exception?.message)
            }
        }
}
