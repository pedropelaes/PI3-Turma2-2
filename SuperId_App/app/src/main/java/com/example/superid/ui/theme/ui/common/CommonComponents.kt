package com.example.superid.ui.theme.ui.common

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.PasswordInfo
import com.example.superid.PasswordsActivity
import com.example.superid.QrCodeAuthActivity
import com.example.superid.R
import com.example.superid.SignUpActivity
import com.example.superid.SuperID
import com.example.superid.ui.theme.SuperIdTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.auth.User


@Composable
//necessário passar a função de atualização por conta do estado do campo de texto ser gerenciado pela activity
//isPassoword é passado quando true quando a função for chamada para senha,
//label recebe um valor de strings.xml
fun TextFieldDesignForLoginAndSignUp(value: String, onValueChange: (String) -> Unit, label: String, isPassword: Boolean = false){
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        shape = CircleShape,
        visualTransformation = if(isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
        ),
        modifier = Modifier.wrapContentSize()
            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            .padding(4.dp)
    )
}

@Composable
fun TextFieldDesignForMainScreen(value: String, onValueChange: (String) -> Unit, label: String, isPassword: Boolean = false){
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        shape = CircleShape,
        visualTransformation = if(isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            focusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
        ),
        modifier = Modifier.wrapContentSize()
            .border(2.dp, colorResource(R.color.field_text_border), CircleShape)
            .padding(4.dp)
    )
}

@Composable
fun SuperIdTitlePainter(painter: Int = R.drawable.super_id_title_light ){
    Image(
        painter = painterResource(painter),
        contentDescription = "SuperIdTitle",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    )
}
@Composable
fun SuperIdTitlePainterVerified(){
    if(isSystemInDarkTheme()){
        SuperIdTitlePainter()
    }else{
        SuperIdTitlePainter(R.drawable.super_id_title_dark)
    }
}

@Preview
@Composable
fun SuperIdTitle(modifier: Modifier = Modifier){
    val title_font = FontFamily(Font(R.font.fonte_titulo))
    Text(
        buildAnnotatedString { //junta strings com estilos diferentes
            withStyle(
                style = SpanStyle(fontFamily = title_font, fontSize = 28.sp, color = Color.White,
                    shadow = Shadow(Color.DarkGray, offset = Offset(1f, 1f),blurRadius = 4f)
                )
            ){
                append("Super")
            }
            withStyle(
                style = SpanStyle(fontFamily = title_font, fontSize = 28.sp, color = MaterialTheme.colorScheme.primary,
                    shadow = Shadow(Color.DarkGray, offset = Offset(1f, 1f),blurRadius = 4f)
                )
            ){
                append(" ID")
            }
        },
        textAlign = TextAlign.Center,
        modifier = Modifier
            .wrapContentWidth()
            .padding(horizontal = 16.dp)
    )

}

@Composable
fun LoginAndSignUpDesign(
    imageResId: Int = themedBackgroundImage(),
    content: @Composable () -> Unit,
) {
    StatusAndNavigationBarColors(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.background)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                content()
            }
        }
    }

}
@Composable
fun themedBackgroundImage(): Int {
    return if (isSystemInDarkTheme()){
        R.drawable.lockers_background_dark
    } else{
        R.drawable.lockers_backgroud_light
    }
}

@Composable
fun CategoryRow(painter: Int = R.drawable.logo_without_text, contentDescripiton: String, text: String, onClick: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .size(80.dp)
            .padding(vertical = 2.dp)
            .background(color = MaterialTheme.colorScheme.secondary)
            .clickable { onClick() }
    ){
        Icon(
            painter = painterResource(painter),
            tint = MaterialTheme.colorScheme.inverseOnSurface,
            contentDescription = contentDescripiton,
            modifier = Modifier.size(48.dp)
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = text,
            color = MaterialTheme.colorScheme.inverseOnSurface,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Abrir categoria",
            tint = MaterialTheme.colorScheme.inverseOnSurface,
            modifier = Modifier.align(Alignment.CenterVertically)

        )
    }
}

@Composable
fun PasswordRow(contentDescripiton: String, text: String, onClick: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .padding(8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(15.dp),
                clip = false
            )
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(15.dp)
            )
            .clickable { onClick() }
    ){
        Icon(
            painter = painterResource(R.drawable.logo_without_text,),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = contentDescripiton,
            modifier = Modifier.size(48.dp)
                .align(Alignment.CenterVertically)
                .padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
                .wrapContentSize()
                .padding(6.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Abrir senha",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun StatusAndNavigationBarColors(
    statusBarColor: Color = Color.Transparent,
    navigationBarColor: Color = Color.Transparent,
){
    val systemUiController = rememberSystemUiController()
    val darkIcons = isSystemInDarkTheme()
    SideEffect { //aplicando as cores da barra de status e navegação
        systemUiController.setStatusBarColor(statusBarColor, darkIcons = darkIcons)
        systemUiController.setNavigationBarColor(navigationBarColor, darkIcons = darkIcons)
    }
}

@Composable
fun DialogVerificarConta(
    onVerificar: () -> Unit,
    onDismiss: () -> Unit,
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Atenção! Verifique seu e-mail")
        },
        modifier = Modifier.wrapContentSize(),
        text = { Text("Para utilizar a função de login por QR-code, é necessário que o email da sua conta esteja verificado.") },
        confirmButton = {
            TextButton(
                onClick = onVerificar
            ) {
                Text("Enviar e-mail", color = MaterialTheme.colorScheme.onBackground)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onBackground)
            }
        },
        containerColor = MaterialTheme.colorScheme.background, // Cor de fundo do dialog
        titleContentColor = MaterialTheme.colorScheme.onBackground, // Cor do título
        textContentColor = MaterialTheme.colorScheme.onBackground, // Cor do texto
    )
}

fun SendEmailVerification(user: FirebaseUser?, context: Context) {
    if (user != null) {
        user.sendEmailVerification()
            .addOnCompleteListener { verification ->
                if (verification.isSuccessful) {
                    Log.d("VERIFICATION", "Email de verificação enviado.")
                    Toast.makeText(
                        context,
                        "Um email de verificação foi enviado para confirmar sua conta.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e("VERIFICATION", "Erro ao mandar email de verificação.")
                    Toast.makeText(
                        context,
                        "Erro ao enviar email de verificação, verifique o seu email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
