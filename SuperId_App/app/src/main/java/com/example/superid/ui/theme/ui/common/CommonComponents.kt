package com.example.superid.ui.theme.ui.common

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseUser

// nesse arquivo, se encontra funções que são utilizadas em mais de 1 activity, ou usadas várias vezes

fun IsEmailValid(email: String): Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()  //retorna se um email é valido ou não


@Composable
fun TextFieldDesignForLoginAndSignUp(                                                               // Composable de design de textfield usado nas
    value: String,                                                                                  // telas de login, cadastro e recuperar senha
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false                                                                     // se for senha, esconde os caracteres e dispõe um botão
) {                                                                                                 // para alternar sua visibilidade
    var passwordVisible by remember { mutableStateOf(false) }

    val leadingIcon = when {                                                                        // definição dos icones de acordo com a label
        label.contains("nome", ignoreCase = true) -> Icons.Default.Person
        label.contains("email", ignoreCase = true) -> Icons.Default.Email
        label.contains("login", ignoreCase = true) -> Icons.Default.Person
        label.contains("descri", ignoreCase = true) -> Icons.Default.Info                     // cobre "descrição" e "descricao"
        label.contains("url", ignoreCase = true) -> Icons.Default.Public
        isPassword -> Icons.Default.Lock
        else -> null
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        singleLine = true,
        shape = CircleShape,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        leadingIcon = {
            leadingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        trailingIcon = if (isPassword) {
            {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                val description = if (passwordVisible) "Esconder senha" else "Mostrar senha"
                Icon(
                    imageVector = image,
                    contentDescription = description,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                        .offset(y = 1.dp)
                        .clickable { passwordVisible = !passwordVisible }
                        .padding(0.dp)
                )

            }
        } else null,
        colors = TextFieldDefaults.colors(
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedContainerColor = MaterialTheme.colorScheme.background,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(52.dp)
            .padding(horizontal = 25.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = CircleShape
            )
    )
}


@Composable
fun TextFieldDesignForMainScreen(                                                                   // Composable de design de textFIeld usado
    value: String,                                                                                  // nas telas de categorias e senhas
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        leadingIcon = leadingIcon,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(56.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = CircleShape
            ),
        shape = CircleShape,
        colors = TextFieldDefaults.colors(
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedContainerColor = MaterialTheme.colorScheme.background,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
            imeAction = ImeAction.Next
        )
    )
}

@Composable
fun SuperIdTitlePainter(painter: Int = R.drawable.logo_clara_transparente ){                        // Composable da logo do SuperId
    Image(
        painter = painterResource(painter),
        contentDescription = "SuperIdTitle",
        contentScale = ContentScale.Fit,
        modifier = Modifier
                .size(164.dp)
    )
}

@Composable
fun SuperIdTitlePainterVerified(){                                                                  // Composable de logo dinâmica do SuperId, que muda
    if(isSystemInDarkTheme()){                                                                      // a logo de acordo com o tema do sistema(escuro ou claroa)
        SuperIdTitlePainter()
    }else{
        SuperIdTitlePainter(R.drawable.logo_transparente)
    }
}

@Preview
@Composable
fun SuperIdTitle(modifier: Modifier = Modifier, isOnMainScreen: Boolean = true, fontSize: TextUnit = 28.sp){                        // Composable do titulo
    val title_font = FontFamily(Font(R.font.fonte_titulo))                                                                          // da logo do SuperId
    val superIdColor = if (isOnMainScreen) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onBackground   // que também varia
    Text(                                                                                                                           // de acordo com o tema
        buildAnnotatedString {                                                                                                      // usa string anotada
            withStyle(                                                                                                              // para combinar diferentes
                style = SpanStyle(fontFamily = title_font, fontSize = fontSize, color = superIdColor,                               // estilos
                    shadow = Shadow(Color.DarkGray, offset = Offset(1f, 1f),blurRadius = 4f)
                )
            ){
                append("Super")
            }
            withStyle(
                style = SpanStyle(fontFamily = title_font, fontSize = fontSize, color = Color(0xFF014E92),
                    shadow = Shadow(Color.DarkGray, offset = Offset(1f, 1f),blurRadius = 4f)
                )
            ){
                append(" ID")
            }
        },
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(horizontal = 16.dp)
    )
}

@Composable
fun LoginAndSignUpDesign(                                                                           // Composable de design de tela usado nas telas de logi,
    content: @Composable () -> Unit,                                                                // qr code, cadastro e redefinir senha
) {
    StatusAndNavigationBarColors(                                                                   // define as cores das barras do android
        MaterialTheme.colorScheme.background,
        MaterialTheme.colorScheme.background
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = WindowInsets.navigationBars                                            // define o padding da barra de navegação do android
                        .asPaddingValues()
                        .calculateBottomPadding()
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.wrapContentHeight(),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}

@Composable
fun PasswordRow(contentDescripiton: String, text: String, onClick: () -> Unit){                     // Composable de design das senhas
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
fun StatusAndNavigationBarColors(                                                                   // Composable que define as cores das barras do android
    statusBarColor: Color = Color.Transparent,                                                      // de acordo com o tema
    navigationBarColor: Color = Color.Transparent,
){
    val systemUiController = rememberSystemUiController()
    val darkIcons = !isSystemInDarkTheme()
    SideEffect {
        systemUiController.setStatusBarColor(statusBarColor, darkIcons = darkIcons)
        systemUiController.setNavigationBarColor(navigationBarColor, darkIcons = darkIcons)
    }
}

@Composable
fun DialogVerificarConta(                                                                           // Composable de um dialog que exibe mensagem sobre verificar
    onVerificar: () -> Unit,                                                                        // e-mail e envia e-mail de verificação
    onDismiss: () -> Unit,
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Atenção! Verifique seu e-mail")
        },
        modifier = Modifier.wrapContentSize(),
        text = { Text("Para utilizar a função de login por QR-code ou recuperar sua senha caso à esqueça, é necessário que o email da sua conta esteja verificado.") },
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
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
        textContentColor = MaterialTheme.colorScheme.onBackground,
    )
}

fun SendEmailVerification(user: FirebaseUser?, context: Context) {                                  // função que envia um e-mail de verificação para o usuario
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
