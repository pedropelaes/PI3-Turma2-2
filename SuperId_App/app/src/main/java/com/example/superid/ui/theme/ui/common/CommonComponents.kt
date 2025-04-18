package com.example.superid.ui.theme.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController

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
            unfocusedTextColor = Color.White,
            unfocusedLabelColor = Color.Gray,
            unfocusedContainerColor = colorResource(R.color.field_text_background),
            focusedContainerColor = colorResource(R.color.field_text_focused_background),
            focusedTextColor = Color.White,
            focusedLabelColor = Color.Black,
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
fun SuperIdTitle(modifier: Modifier = Modifier){
    val title_font = FontFamily(Font(R.font.fonte_titulo))
    Text(
        buildAnnotatedString { //junta strings com estilos diferentes
            withStyle(
                style = SpanStyle(fontFamily = title_font, fontSize = 40.sp, color = Color.Black, background = Color.White)
            ){
                append("Super")
            }
            withStyle(
                style = SpanStyle(fontFamily = title_font, fontSize = 40.sp, color = Color(0xFF152034), background = Color.White)
            ){
                append(" ID")
            }
        },
        textAlign = TextAlign.Center,
        modifier = Modifier
            .wrapContentWidth()
            .padding(16.dp)
    )
}

@Composable
fun LoginAndSignUpDesign(
    imageResId: Int,
    statusBarColor: Color = Color(0xFF152034),
    navigationBarColor: Color = Color(0xFF152034),
    content: @Composable () -> Unit,
) {
    val systemUiController = rememberSystemUiController()
    SideEffect { //aplicando as cores da barra de status e navegação
        systemUiController.setStatusBarColor(statusBarColor, darkIcons = false)
        systemUiController.setNavigationBarColor(navigationBarColor, darkIcons = false)
    }

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
