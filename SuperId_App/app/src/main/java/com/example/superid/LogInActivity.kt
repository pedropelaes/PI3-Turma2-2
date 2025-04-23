package com.example.superid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.superid.ui.theme.ui.common.LoginAndSignUpDesign
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.example.superid.ui.theme.ui.common.SuperIdTitle
import com.example.superid.ui.theme.ui.common.TextFieldDesignForLoginAndSignUp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            Login()
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
    LoginAndSignUpDesign(imageResId = R.drawable.lockers_background, content = {
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
        SuperIdTitle()

        Spacer(modifier = Modifier.height(24.dp))

        Text("Login:",fontFamily = FontFamily.SansSerif ,fontSize = 30.sp, color = Color.White,
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
            border = BorderStroke(2.dp, Color.White),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF152034).copy(alpha = 0.5f),
                disabledContentColor = Color.DarkGray
            ),
            modifier = Modifier.height(45.dp).width(160.dp)
        ){
            Text("Fazer Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if(!success){
            Text(stringResource(R.string.login_error), color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}