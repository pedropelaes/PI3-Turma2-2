package com.example.superid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.ui.theme.SuperIdTheme
import com.example.superid.ui.theme.ui.common.LoginAndSignUpDesign
import com.example.superid.ui.theme.ui.common.SuperIdTitle
import com.example.superid.ui.theme.ui.common.TextFieldDesignForLoginAndSignUp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperIdTheme(darkTheme = isSystemInDarkTheme()) {
                SignUp()
            }
        }
    }
}
@Preview
@Composable
fun SignUp(){
    LoginAndSignUpDesign() {
        SignUpScreen()
    }
}

fun PerformSignUp(name: String,email: String, password: String, onResult: (String) -> Unit){
    val auth = Firebase.auth

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful){
                val user = auth.currentUser
                user?.let {
                    SaveNewAccount(name, email, it.uid)
                }
                user?.sendEmailVerification()
                    ?.addOnCompleteListener{ verification ->
                        if(verification.isSuccessful){
                            Log.d("SIGNUP", "Email de verificação enviado.")
                            onResult("Um email de verificação foi enviado para confirmar sua conta.")
                        }else{
                            Log.d("SIGNUP", "Erro ao mandar email de veriricação.")
                            onResult("Erro ao enviar email de verificação, verifique o seu email.")
                        }
                    }
            }else{
                Log.d("SIGNUP", "Erro ao criar usuário ${task.exception?.message}.")
                onResult("Erro ao criar sua conta, verifique seus dados.")
            }
        }
}

fun SaveNewAccount(name: String, email: String, uid: String, tries: Int = 0){
    val db = Firebase.firestore

    val taskDoc = hashMapOf(
        "name" to name,
        "email" to email,
    )
    db.collection("users").document(uid).set(taskDoc)
        .addOnCompleteListener{task->
            if(task.isSuccessful){
                Log.d("SIGNUP", "Documento do usuário salvo no banco de dados.")
            }
        }
        .addOnFailureListener{retry->
            if (tries < 5){
                SaveNewAccount(name, email, uid, tries + 1)
            }else{
                Log.d("SIGNUP", "Falha ao salvar usuário no banco de dados.")
            }
        }
}

@Composable
fun SignUpScreen() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var masterPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
        SuperIdTitle()

        Spacer(modifier = Modifier.height(24.dp))

        Text("Cadastro:",fontFamily = FontFamily.SansSerif ,fontSize = 30.sp, color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextFieldDesignForLoginAndSignUp(value = name, onValueChange = { name = it },
            label = stringResource(R.string.type_your_name)
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextFieldDesignForLoginAndSignUp(value = email, onValueChange = { email = it },
            label = stringResource(R.string.type_your_email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextFieldDesignForLoginAndSignUp(value = masterPassword, onValueChange = { masterPassword = it },
            label = stringResource(R.string.type_your_password,), isPassword = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextFieldDesignForLoginAndSignUp(value = confirmPassword, onValueChange = { confirmPassword = it },
            label = stringResource(R.string.confirm_your_password,), isPassword = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        if(masterPassword != confirmPassword) {
            Text(stringResource(R.string.passwords_must_match), color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                PerformSignUp(name, email, masterPassword){msg ->
                result = msg
                }
            },
            enabled = if(masterPassword == confirmPassword && name.isNotEmpty() && email.isNotEmpty() && masterPassword.isNotEmpty()) true else false,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier
                .height(45.dp)
                .width(160.dp)
        ){
            Text("Fazer Cadastro")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(result, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(12.dp))

        Text("Já possui conta?", color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(0.dp))
        TextButton(
            onClick = {
                val intent = Intent(context, LogInActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .height(45.dp)
                .width(160.dp)
        ) {
            Text("Login", textDecoration = TextDecoration.Underline, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}
