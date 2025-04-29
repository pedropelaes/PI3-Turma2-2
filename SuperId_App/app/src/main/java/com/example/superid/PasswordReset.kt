package com.example.superid

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.ui.theme.ui.common.LoginAndSignUpDesign
import com.example.superid.ui.theme.ui.common.SuperIdTitle
import com.example.superid.ui.theme.ui.common.TextFieldDesignForLoginAndSignUp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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
            Text("Enviar e-mail de recuperação")
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
