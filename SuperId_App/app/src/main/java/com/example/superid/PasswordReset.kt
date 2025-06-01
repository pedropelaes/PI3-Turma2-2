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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.ui.theme.SuperIdTheme
import com.example.superid.ui.theme.ui.common.IsEmailValid
import com.example.superid.ui.theme.ui.common.LoginAndSignUpDesign
import com.example.superid.ui.theme.ui.common.SuperIdTitlePainterVerified
import com.example.superid.ui.theme.ui.common.TextFieldDesignForLoginAndSignUp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import java.net.URL

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
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth(),
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

        Text(
            text = "Atenção: Apenas contas com e-mail verificado poderão alterar a senha.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
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
                sendPasswordResetIfEmailVerified(cleanEmail, onResultado = {result ->
                    Toast.makeText(context,result, Toast.LENGTH_LONG).show()
                })},
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

fun sendPasswordResetIfEmailVerified(email: String, onResultado: (String) -> Unit) {                // envia o e-mail de redefinir senha SE o usuário tem
    val functions = Firebase.functions                                                              // seu e-mail verificado
    if(!IsEmailValid(email)){
        onResultado("Digite um email válido")
        return
    }
    Log.d("DEBUG_EMAIL", email)
    functions
        .getHttpsCallableFromUrl(URL("https://checkemailisverified-snp2owcvrq-rj.a.run.app"))  // chama firebase function que retorna se o email está
        .call(hashMapOf("email" to email))                                                          // verificado ou não
        .addOnSuccessListener { result ->
            val data = result.data as? Map<*,*> ?: run {
                onResultado("Erro: resposta inválida do servidor.")
                return@addOnSuccessListener
            }
            val isVerified = data["verified"] as? Boolean ?: false
            Log.d("DEBUG_EMAIL", "Função chamada")

            if (isVerified) {
                Firebase.auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onResultado("Um e-mail de recuperação foi enviado.")
                        } else {
                            onResultado("Erro ao enviar o e-mail: ${task.exception?.message}")
                        }
                    }
            } else {
                onResultado("Seu e-mail ainda não foi verificado.")
            }
        }
        .addOnFailureListener { exception ->
            if (exception is FirebaseFunctionsException) {
                when (exception.code) {
                    FirebaseFunctionsException.Code.NOT_FOUND -> {
                        onResultado("Nenhum usuário com esse e-mail.")
                    }
                    FirebaseFunctionsException.Code.INVALID_ARGUMENT -> {
                        onResultado("E-mail inválido.")
                    }
                    else -> {
                        onResultado("Erro inesperado: ${exception.message}")
                    }
                }
            } else {
                onResultado("Erro de conexão: ${exception.message}")
            }
        }
}