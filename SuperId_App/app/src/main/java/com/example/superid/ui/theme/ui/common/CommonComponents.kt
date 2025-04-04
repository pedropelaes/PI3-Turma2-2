package com.example.superid.ui.theme.ui.common

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.superid.R

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
    )
}