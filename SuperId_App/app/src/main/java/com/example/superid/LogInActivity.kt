package com.example.superid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.ui.theme.SuperIdTheme
import com.example.superid.ui.theme.ui.common.LoginAndSignUpDesign
import com.example.superid.ui.theme.ui.common.SuperIdTitle
import com.example.superid.ui.theme.ui.common.SuperIdTitlePainter
import com.example.superid.ui.theme.ui.common.SuperIdTitlePainterVerified
import com.example.superid.ui.theme.ui.common.TextFieldDesignForLoginAndSignUp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            SuperIdTheme(darkTheme = isSystemInDarkTheme()) {
                Login()
            }
        }
    }
}

fun PerformLogin(email: String, password: String, context: Context, onResult: (Boolean) -> Unit){
    val auth = Firebase.auth

    val currentUser = auth.currentUser
    if (currentUser != null){
        auth.signOut()
    }
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener{task ->
            if(task.isSuccessful){
                Log.d("LOGIN", "Login efetuado.")
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }else{
                Log.d("LOGIN", "Login efetuado.")
                onResult(false)
            }
        }
}

@Preview
@Composable
fun Login(){
    LoginAndSignUpDesign(content = {
        LoginScreen()
    })
}

@Composable
fun LoginScreen(){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var success by remember { mutableStateOf(true) }
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
        Spacer(modifier = Modifier.height(64.dp))
        SuperIdTitlePainterVerified()

        Spacer(modifier = Modifier.height(24.dp))

        Text("Entrar:",fontFamily = FontFamily.SansSerif ,fontSize = 30.sp, color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextFieldDesignForLoginAndSignUp(value = email, onValueChange = { email = it },
            label = stringResource(R.string.type_your_email,)
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextFieldDesignForLoginAndSignUp(value = password, onValueChange = { password = it },
            label = stringResource(R.string.type_your_password), isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                PerformLogin(email, password, context) { result ->
                    success = result
                }
            },
            enabled = if(email.isNotEmpty() && password.isNotEmpty()) true else false,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(0.5f),
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth(0.85f) // Igual às caixas de texto
                .height(50.dp)
        ){
            Text("Entrar")
        }
        Spacer(modifier = Modifier.height(2.dp))

        TextButton(
            onClick = {
                val intent = Intent(context, PasswordReset::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .height(45.dp)
                .width(190.dp)
                .wrapContentSize()
        ) {
            Text("Esqueceu sua senha?", textDecoration = TextDecoration.Underline, color = MaterialTheme.colorScheme.onBackground)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if(!success){
            Text(stringResource(R.string.login_error), color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text("Ainda não possui conta?", color = MaterialTheme.colorScheme.onBackground)
        TextButton(
            onClick = {
                val intent = Intent(context, SignUpActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .height(45.dp)
                .width(160.dp)
        ) {
            Text("Fazer Cadastro", textDecoration = TextDecoration.Underline, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}