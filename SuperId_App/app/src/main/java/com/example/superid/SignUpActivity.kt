package com.example.superid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
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
import utils.ChaveAesUtils

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperIdTheme() {
                SignUp()
            }
        }
    }
}

@Preview
@Composable
fun SignUp() {
    LoginAndSignUpDesign {
        SignUpScreen()
    }
}

fun PerformSignUp(
    context: android.content.Context,
    name: String,
    email: String,
    password: String,
    onResult: (String) -> Unit
) {
    val auth = Firebase.auth

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    SaveNewAccount(name, email, it.uid)
                    SaveUserDefaultCategories(it.uid)
                }

                user?.sendEmailVerification()
                    ?.addOnCompleteListener { verification ->
                        if (verification.isSuccessful) {
                            Log.d("SIGNUP", "Email de verificação enviado.")
                            onResult("Conta criada com sucesso! Verifique seu e-mail.")

                            val intent = Intent(context, LogInActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            Log.e("SIGNUP", "Erro ao enviar email de verificação.")
                            onResult("Erro ao enviar email de verificação. Verifique o seu e-mail.")
                        }
                    }
            } else {
                Log.e("SIGNUP", "Erro ao criar usuário ${task.exception?.message}.")
                onResult("Erro ao criar sua conta. Verifique seus dados ou tente novamente.")
            }
        }
}

fun SaveNewAccount(name: String, email: String, uid: String, tries: Int = 0) {
    val db = Firebase.firestore
    val chave = ChaveAesUtils.gerarChaveAesBase64()
    val taskDoc = hashMapOf(
        "name" to name,
        "email" to email,
        "AESkey" to chave,
    )
    db.collection("users").document(uid).set(taskDoc)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("SIGNUP", "Documento do usuário salvo no banco de dados. ${task.result}")
            }
        }
        .addOnFailureListener { retry ->
            if (tries < 5) {
                SaveNewAccount(name, email, uid, tries + 1)
            } else {
                Log.e("SIGNUP", "Falha ao salvar usuário no banco de dados.")
            }
        }
}

fun SaveUserDefaultCategories(uid: String) {
    val db = Firebase.firestore
    val batch = db.batch()
    val categorias = listOf("aplicativos", "emails", "sites", "teclados")
    val categoriasRef = db.collection("users").document(uid).collection("categorias")

    for (categoria in categorias) {
        val docRef = categoriasRef.document(categoria)

        val nameCategoria = mapOf("nome" to categoria)
        batch.set(docRef, nameCategoria)

        val passwordPlaceHolder = docRef.collection("senhas").document("placeholder")
        val placeholder = mapOf("placeholder" to true)
        batch.set(passwordPlaceHolder, placeholder)
    }

    batch.commit()
        .addOnSuccessListener { Log.d("SIGNUP", "Estrutura das categorias criada.") }
        .addOnFailureListener { Log.e("SIGNUP", "Erro", it) }
}

@Composable
fun SignUpScreen() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var masterPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        SuperIdTitlePainterVerified()
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Crie sua conta:",
            fontFamily = FontFamily.SansSerif,
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextFieldDesignForLoginAndSignUp(
            value = name,
            onValueChange = { name = it },
            label = stringResource(R.string.type_your_name)
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextFieldDesignForLoginAndSignUp(
            value = email,
            onValueChange = { email = it },
            label = stringResource(R.string.type_your_email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextFieldDesignForLoginAndSignUp(
            value = masterPassword,
            onValueChange = { masterPassword = it },
            label = stringResource(R.string.type_your_password),
            isPassword = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextFieldDesignForLoginAndSignUp(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = stringResource(R.string.confirm_your_password),
            isPassword = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (masterPassword != confirmPassword) {
            Text(
                stringResource(R.string.passwords_must_match),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (!isValidEmail(email)) {
                    Toast.makeText(context, "Por favor, insira um e-mail válido.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (masterPassword.length < 6) {
                    Toast.makeText(context, "A senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true

                checkIfEmailExists(email) { exists ->
                    if (exists) {
                        Toast.makeText(context, "Já existe uma conta com este e-mail.", Toast.LENGTH_SHORT).show()
                        isLoading = false
                    } else {
                        PerformSignUp(context, name, email, masterPassword) { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            isLoading = false
                        }
                    }
                }
            },
            enabled = !isLoading && masterPassword == confirmPassword &&
                    name.isNotEmpty() && email.isNotEmpty() && masterPassword.isNotEmpty(),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(24.dp).width(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Fazer Cadastro")
            }
        }

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

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun checkIfEmailExists(email: String, onResult: (Boolean) -> Unit) {
    FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods
                onResult(!signInMethods.isNullOrEmpty())
            } else {
                onResult(false)
            }
        }
}
