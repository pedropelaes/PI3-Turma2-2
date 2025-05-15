package com.example.superid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.ui.theme.SuperIdTheme
import com.example.superid.ui.theme.ui.common.LoginAndSignUpDesign
import com.example.superid.ui.theme.ui.common.SuperIdTitlePainterVerified
import com.example.superid.ui.theme.ui.common.TextFieldDesignForLoginAndSignUp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PasswordReset : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperIdTheme {
                LoginAndSignUpDesign {
                    PasswordResetScreen()
                }
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
        SuperIdTitlePainterVerified()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Recuperar Senha",
            fontFamily = FontFamily.SansSerif,
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Por favor, insira o e-mail associado à sua conta. Enviaremos um link para recuperação de senha.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextFieldDesignForLoginAndSignUp(
            value = email,
            onValueChange = { email = it },
            label = stringResource(R.string.type_your_email)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val cleanEmail = email.trim().lowercase()

                sendPasswordResetIfEmailVerified("usuario@email.com", "senhaDoUsuario") { success, message ->
                    if (success) {
                        Toast.makeText(context, "Email de redefinição enviado!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Erro: $message", Toast.LENGTH_LONG).show()
                    }
                }
            },
            enabled = email.isNotEmpty(),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.surface),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(0.5f),
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth(0.5f)
        ) {
            Text("Enviar E-mail")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (emailSent) {
            Text(
                "Um E-mail para redefinição foi enviado!\nVerifique sua caixa de entrada.",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                errorMessage,
                color = Color.Red,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                tint = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Voltar",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

fun sendPasswordResetIfEmailVerified(
    email: String,
    password: String,
    onResult: (Boolean, String?) -> Unit
) {
    val auth = Firebase.auth

    // Tenta login com email e senha
    auth.signInWithEmailAndPassword(email, password)
        .addOnSuccessListener { result ->
            val user = result.user

            if (user != null && user.isEmailVerified) {
                // Email foi verificado — envia email de redefinição de senha
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("PasswordReset", "Email de redefinição enviado com sucesso.")
                            onResult(true, null)
                        } else {
                            Log.e("PasswordReset", "Erro ao enviar email: ${task.exception?.message}")
                            onResult(false, task.exception?.message)
                        }

                        // Faz logout por segurança
                        auth.signOut()
                    }
            } else {
                Log.d("PasswordReset", "Email não verificado.")
                auth.signOut()
                onResult(false, "O email ainda não foi verificado.")
            }
        }
        .addOnFailureListener { exception ->
            Log.e("PasswordReset", "Erro ao autenticar: ${exception.message}")
            onResult(false, "Erro ao autenticar: ${exception.message}")
        }
}
