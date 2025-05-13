package com.example.superid.ui.theme.ui.common

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.PasswordsActivity
import com.example.superid.QrCodeAuthActivity
import com.example.superid.R
import com.example.superid.SignUpActivity
import com.example.superid.SuperID
import com.example.superid.ui.theme.SuperIdTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun TextFieldDesignForLoginAndSignUp(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false

) {
    val leadingIcon = when {
        label.contains("nome", ignoreCase = true) -> Icons.Default.Person
        label.contains("email", ignoreCase = true) -> Icons.Default.Email
        isPassword -> Icons.Default.Lock
        else -> null
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        shape = CircleShape,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        leadingIcon = {
            leadingIcon?.let {
                Icon(imageVector = it, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
            }
        },
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
            .height(52.dp)
            .padding(horizontal = 25.dp) // Espaço entre as bordas e os campos
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = CircleShape
            )
    )
}

@Composable
fun TextFieldDesignForMainScreen(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        leadingIcon = leadingIcon,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = CircleShape
            ),
        shape = CircleShape,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
            imeAction = ImeAction.Next
        )
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
                style = SpanStyle(fontFamily = title_font, fontSize = 28.sp, color = MaterialTheme.colorScheme.onPrimary,
                    shadow = Shadow(Color.DarkGray, offset = Offset(1f, 1f),blurRadius = 4f)
                )
            ){
                append("Super")
            }
            withStyle(
                style = SpanStyle(fontFamily = title_font, fontSize = 28.sp, color = MaterialTheme.colorScheme.surfaceVariant,
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
    content: @Composable () -> Unit,
) {
    StatusAndNavigationBarColors(
        MaterialTheme.colorScheme.background,
        MaterialTheme.colorScheme.background
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // <- cor de fundo aplicada aqui
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                ),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
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
            .size(80.dp)
            .padding(vertical = 2.dp)
            .background(color = MaterialTheme.colorScheme.secondary)
            .clickable { onClick() }
    ){
        Icon(
            painter = painterResource(R.drawable.logo_without_text,),
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
                .wrapContentSize()
                .padding(6.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Abrir senha",
            tint = MaterialTheme.colorScheme.inverseOnSurface,
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