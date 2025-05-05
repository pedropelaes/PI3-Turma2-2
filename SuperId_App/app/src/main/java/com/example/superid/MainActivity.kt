package com.example.superid

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.superid.ui.theme.SuperIdTheme
import com.example.superid.ui.theme.ui.common.CategoryRow
import com.example.superid.ui.theme.ui.common.SuperIdTitle
import com.example.superid.ui.theme.ui.common.TextFieldDesignForLoginAndSignUp
import com.example.superid.ui.theme.ui.common.TextFieldDesignForMainScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            SuperIdTheme(darkTheme = isSystemInDarkTheme()) {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenDesign(
    statusBarColor: Color = Color.Transparent,
    navigationBarColor: Color = Color.Transparent,
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
            TopAppBar(
                title = {
                    SuperIdTitle(Modifier.fillMaxSize())
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
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
                    Text("Criar Categoria")
                }
            }
        },
    ) { innerPadding ->
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
fun DialogCriarCategoria(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){
    var nomeDaCategoria by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Criando categoria:")
        },
        text = {
            TextFieldDesignForMainScreen(value = nomeDaCategoria, onValueChange = {nomeDaCategoria = it}, label = "Nome da categoria")
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

fun OpenPasswordsActivity(categoria: String, painter: Int ,context: Context) {
    val intent = Intent(context, PasswordsActivity::class.java).apply {
        putExtra("categoria", categoria)
        putExtra("icone", painter)
    }
    context.startActivity(intent)
}

@Preview
@Composable
fun MainScreen(){
    val context = LocalContext.current
    var categoriasCriadas by remember { mutableStateOf(emptyList<Int>()) }
    MainScreenDesign {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ){
            CategoryRow(
                painter = R.drawable.smartphone,
                contentDescripiton = "Categoria Aplicativos",
                text = "Aplicativos",
                onClick = { OpenPasswordsActivity("aplicativos", R.drawable.smartphone,context) },
            )
            CategoryRow(
                painter = R.drawable.email,
                contentDescripiton = "Categoria Emails",
                text = "Emails",
                onClick = { OpenPasswordsActivity("emails", R.drawable.email,context) },
            )
            CategoryRow(
                painter = R.drawable.world_wide_web,
                contentDescripiton = "Categoria Sites",
                text = "Sites",
                onClick = { OpenPasswordsActivity("sites", R.drawable.world_wide_web,context) },
            )
            CategoryRow(
                painter = R.drawable.keyboard,
                contentDescripiton = "Categoria Teclados de acesso físicos",
                text = "Teclados de acesso físicos",
                onClick = { OpenPasswordsActivity("teclados", R.drawable.keyboard, context) },
            )

            if(categoriasCriadas.isNotEmpty()) {
                categoriasCriadas.forEach { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(color = MaterialTheme.colorScheme.onBackground),
                    ) {
                        Text(text = "Item ${index + 1}")
                    }
                }
            }
        }
    }
}