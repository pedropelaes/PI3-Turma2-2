package com.example.superid

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.superid.ui.theme.SuperIdTheme
import com.example.superid.ui.theme.ui.common.SuperIdTitle
import com.example.superid.ui.theme.ui.common.TextFieldDesignForMainScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class PasswordsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            val categoria = intent.getStringExtra("categoria")
            val icone = intent.getIntExtra("icone", R.drawable.logo_without_text)
            SuperIdTheme(darkTheme = isSystemInDarkTheme()) {
                PasswordsScreen(categoria, icone)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordsScreenDesign(
    statusBarColor: Color = Color.Transparent,
    navigationBarColor: Color = Color.Transparent,
    categoria: String?,
    iconPainter: Int,
    content: @Composable () -> Unit
){
    val systemUiController = rememberSystemUiController()
    val darkIcons = isSystemInDarkTheme()
    SideEffect { //aplicando as cores da barra de status e navegação
        systemUiController.setStatusBarColor(statusBarColor, darkIcons = darkIcons)
        systemUiController.setNavigationBarColor(navigationBarColor, darkIcons = darkIcons)
    }

    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        Icon(
                            painter = painterResource(iconPainter),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = "Icone da categoria",
                            modifier = Modifier.wrapContentSize()
                                .padding(6.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        if (categoria != null) {
                            Text("${categoria.capitalize()}:", color = MaterialTheme.colorScheme.onPrimary)
                        }else{
                            Text("Senhas:")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        //SuperIdTitle()
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                }
            )
        },
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(context, QrCodeAuthActivity::class.java)
                        context.startActivity(intent)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.qr_code),
                        contentDescription = "Escanear QR-Code",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                ExtendedFloatingActionButton(
                    onClick = {
                        showDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                ){
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Criar Categoria",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text("Adicionar senha")
                }
            }
        }

    ){ innerPadding->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            content()
        }

        if (showDialog){
            DialogCriarCategoria(
                onDismiss = {showDialog = false},
                onConfirm = {showDialog = false}
            )
        }
    }
}

@Composable
fun AddPasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){
    var login by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Criando categoria:")
        },
        text = {
            TextFieldDesignForMainScreen(value = login, onValueChange = {login = it}, label = "Login(opcional)")
            TextFieldDesignForMainScreen(value = login, onValueChange = {login = it}, label = "Senha", isPassword = true)
            TextFieldDesignForMainScreen(value = login, onValueChange = {login = it}, label = "Descrição(opcional)")

        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Confirmar", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        },
        containerColor = MaterialTheme.colorScheme.primary, // Cor de fundo do dialog
        titleContentColor = MaterialTheme.colorScheme.onPrimary, // Cor do título
        textContentColor = MaterialTheme.colorScheme.onPrimary, // Cor do texto
    )
}

@Composable
fun PasswordsScreen(categoria: String?, icone: Int){
    PasswordsScreenDesign(categoria = categoria, iconPainter = icone) {
        if (categoria != null) {
            Text(categoria)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordsScreenPreview(){
    PasswordsScreen(
        categoria = "mock_categoria",
        icone = R.drawable.logo_without_text
    )
}