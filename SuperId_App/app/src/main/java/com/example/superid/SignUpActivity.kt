package com.example.superid

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.superid.ui.theme.ui.common.LoginAndSignUpDesign
import com.example.superid.ui.theme.ui.common.SuperIdTitle
import com.example.superid.ui.theme.ui.common.TextFieldDesignForLoginAndSignUp

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignUp()
        }
    }
}
@Preview
@Composable
fun SignUp(){
    LoginAndSignUpDesign(R.drawable.lockers_background) {
        SignUpScreen()
    }
}

@Composable
fun SignUpScreen() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var masterPassword by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
        SuperIdTitle()

        Spacer(modifier = Modifier.height(24.dp))

        Text("Cadastro:",fontFamily = FontFamily.SansSerif ,fontSize = 30.sp, color = Color.White,
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
        
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {},
            border = BorderStroke(2.dp, Color.White),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF152034).copy(alpha = 0.5f)
            ),
            modifier = Modifier.height(45.dp).width(160.dp)
        ){
            Text("Fazer Cadastro")
        }
    }
}
