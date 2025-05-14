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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.ui.theme.SuperIdTheme
import com.example.superid.ui.theme.ui.common.LoginAndSignUpDesign
import com.example.superid.ui.theme.ui.common.SuperIdTitle
import com.example.superid.ui.theme.ui.common.SuperIdTitlePainterVerified
import com.example.superid.ui.theme.ui.common.TextFieldDesignForLoginAndSignUp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore




class PasswordReset : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperIdTheme {
                LoginAndSignUpDesign(){
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

        // Campo de e-mail com o estilo padronizado
        TextFieldDesignForLoginAndSignUp(value = email, onValueChange = { email = it },
            label = stringResource(R.string.type_your_email)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botão com o mesmo estilo e dimensões das caixas
        Button(
            onClick = {
                val db = Firebase.firestore
                db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty) {
                            val doc = result.documents[0]
                            val emailVerified = doc.getBoolean("emailVerified") ?: false
                            if (emailVerified) {
                                sendPasswordResetEmail(email) { success, error ->
                                    if (success) {
                                        emailSent = true
                                        errorMessage = ""
                                    } else {
                                        emailSent = false
                                        errorMessage = error ?: "Erro desconhecido."
                                    }
                                }
                            } else {
                                errorMessage = "Você precisa verificar seu e-mail antes de recuperar a senha."
                                emailSent = false
                            }
                        } else {
                            errorMessage = "E-mail não encontrado no sistema."
                            emailSent = false
                        }
                    }
                    .addOnFailureListener {
                        errorMessage = "Erro ao verificar status do e-mail."
                        emailSent = false
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
                .height(56.dp) // mesmo tamanho da TextField
                .fillMaxWidth(0.5f) // largura igual às caixas
        ) {
            Text("Enviar E-mail")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (emailSent) {
            Text(
                "Um E-mail para redefinição foi enviado!\n" +
                        "Verifique sua caixa de entrada.",
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
                "Erro ao enviar E-mail.",
                color = Color.Red,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
        }
    }

    // Botão Voltar
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


fun sendPasswordResetEmail(email: String, onResult: (Boolean, String?) -> Unit) {
    val cleanEmail = email.trim().lowercase()
    FirebaseAuth.getInstance().fetchSignInMethodsForEmail(cleanEmail)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods
                Log.d("RESET", "Métodos de login: $signInMethods")
                if (!signInMethods.isNullOrEmpty()) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(cleanEmail)
                        .addOnCompleteListener { resetTask ->
                            if (resetTask.isSuccessful) {
                                onResult(true, null)
                            } else {
                                val exception = resetTask.exception
                                Log.e("RESET", "Erro ao enviar email:", exception)
                                onResult(false, exception?.localizedMessage ?: "Erro ao enviar e-mail de recuperação.")
                            }
                        }
                } else {
                    onResult(false, "Este e-mail não está registrado.")
                }
            } else {
                val exception = task.exception
                Log.e("RESET", "Erro ao verificar métodos de login:", exception)
                onResult(false, "Erro ao verificar o e-mail.")
            }
        }
}






