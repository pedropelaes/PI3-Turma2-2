package com.example.superid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.superid.ui.theme.SuperIdTheme
import com.example.superid.ui.theme.ui.common.StatusAndNavigationBarColors
import com.example.superid.ui.theme.ui.common.SuperIdTitle
import com.example.superid.ui.theme.ui.common.TextFieldDesignForMainScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            SuperIdTheme(darkTheme = isSystemInDarkTheme()) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var categoriasCriadas by remember { mutableStateOf(listOf<String>()) }
    var categoriaParaExcluir by remember { mutableStateOf<String?>(null) }
    var showDialogExcluir by remember { mutableStateOf(false) }
    val categoriasFixas = listOf("aplicativos", "emails", "sites", "teclados")

    LaunchedEffect(userId) {
        userId?.let { uid ->
            db.collection("users")
                .document(uid)
                .collection("categorias")
                .addSnapshotListener { snapshot, _ ->
                    snapshot?.let {
                        categoriasCriadas = it.documents.mapNotNull { doc ->
                            doc.getString("nome")
                        }.filterNot { nome-> //filtra as categorias pré criadas para evitar repetição
                            nome in categoriasFixas
                        }
                    }
                }
        }
    }

    MainScreenDesign(
        categoriasCriadas = categoriasCriadas,
        onAdicionarCategoria = { novaCategoria ->
            categoriasCriadas = categoriasCriadas + novaCategoria
            userId?.let { uid ->
                db.collection("users")
                    .document(uid)
                    .collection("categorias")
                    .document(novaCategoria)
                    .set(hashMapOf("nome" to novaCategoria))
                    .addOnSuccessListener {
                        Toast.makeText(context, "Categoria criada!", Toast.LENGTH_SHORT).show()
                    }
            }
        },
        categoriaParaExcluir = categoriaParaExcluir,
        onExcluirCategoria = { nome ->
            categoriaParaExcluir = nome
            showDialogExcluir = true
        },
        showDialogExcluir = showDialogExcluir,
        onConfirmarExclusao = {
            val nomeExcluir = categoriaParaExcluir
            if (nomeExcluir != null && userId != null) {
                db.collection("users")
                    .document(userId)
                    .collection("categorias")
                    .whereEqualTo("nome", nomeExcluir)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            db.collection("users")
                                .document(userId)
                                .collection("categorias")
                                .document(document.id)
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Categoria deletada!", Toast.LENGTH_SHORT).show()
                                }
                        }
                        showDialogExcluir = false
                        categoriaParaExcluir = null
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Erro ao deletar", Toast.LENGTH_SHORT).show()
                        showDialogExcluir = false
                        categoriaParaExcluir = null
                    }
            }
        },
        onCancelarExclusao = {
            showDialogExcluir = false
            categoriaParaExcluir = null
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenDesign(
    categoriasCriadas: List<String>,
    onAdicionarCategoria: (String) -> Unit,
    categoriaParaExcluir: String?,
    onExcluirCategoria: (String) -> Unit,
    showDialogExcluir: Boolean,
    onConfirmarExclusao: () -> Unit,
    onCancelarExclusao: () -> Unit,
    content: @Composable () -> Unit = {}
) {
    StatusAndNavigationBarColors()

    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var categoriaParaEditar by remember { mutableStateOf("") }
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    SuperIdTitle(modifier = Modifier.size(10.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = {}) {
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
            ) {
                ExtendedFloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Criar Categoria",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text("Criar Categoria")
                }

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
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                item {
                    CategoryRow(
                        painter = R.drawable.smartphone,
                        contentDescripiton = "Categoria Aplicativos",
                        text = "Aplicativos",
                        onClick = { OpenPasswordsActivity("aplicativos", context) },
                    )
                    CategoryRow(
                        painter = R.drawable.email,
                        contentDescripiton = "Categoria Emails",
                        text = "Emails",
                        onClick = { OpenPasswordsActivity("emails", context) },
                    )
                    CategoryRow(
                        painter = R.drawable.world_wide_web,
                        contentDescripiton = "Categoria Sites",
                        text = "Sites",
                        onClick = { OpenPasswordsActivity("sites", context) },
                    )
                    CategoryRow(
                        painter = R.drawable.keyboard,
                        contentDescripiton = "Categoria Teclados de acesso físicos",
                        text = "Teclados de acesso físicos",
                        onClick = { OpenPasswordsActivity("teclados", context) },
                    )
                }
                items(categoriasCriadas) { nome ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryRow(
                            painter = R.drawable.smartphone,
                            contentDescripiton = "Categoria $nome",
                            text = nome,
                            onClick = { OpenPasswordsActivity(nome, context) },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                showEditDialog = true
                                categoriaParaEditar = nome
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar categoria",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = {
                                onExcluirCategoria(nome)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar categoria",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {
            DialogCriarCategoria(
                onDismiss = { showDialog = false },
                onConfirm = { nome ->
                    if (nome.isNotBlank()) {
                        onAdicionarCategoria(nome)
                    }
                    showDialog = false
                }
            )
        }

        if (showEditDialog) {
            DialogEditarCategoria(
                nomeAtual = categoriaParaEditar,
                onDismiss = { showEditDialog = false },
                onConfirm = { novoNome ->
                    if (novoNome.isNotBlank() && userId != null) {
                        db.collection("users")
                            .document(userId)
                            .collection("categorias")
                            .whereEqualTo("nome", categoriaParaEditar)
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    db.collection("users")
                                        .document(userId)
                                        .collection("categorias")
                                        .document(document.id)
                                        .update("nome", novoNome)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Categoria renomeada!", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                    }
                    showEditDialog = false
                }
            )
        }

        if (showDialogExcluir && categoriaParaExcluir != null) {
            AlertDialog(
                onDismissRequest = onCancelarExclusao,
                title = { Text("Confirmar exclusão") },
                text = { Text("Tem certeza que deseja excluir a categoria \"${categoriaParaExcluir}\"?") },
                confirmButton = {
                    TextButton(onClick = onConfirmarExclusao) {
                        Text("Confirmar", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                },
                dismissButton = {
                    TextButton(onClick = onCancelarExclusao) {
                        Text("Cancelar", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                textContentColor = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
fun DialogCriarCategoria(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var nomeDaCategoria by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Criando categoria:") },
        text = {
            TextFieldDesignForMainScreen(
                value = nomeDaCategoria,
                onValueChange = { nomeDaCategoria = it },
                label = "Nome da categoria"
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(nomeDaCategoria) }) {
                Text("Confirmar", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        },
        containerColor = MaterialTheme.colorScheme.primary,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        textContentColor = MaterialTheme.colorScheme.onPrimary,
    )
}

@Composable
fun DialogEditarCategoria(
    nomeAtual: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var novoNome by remember { mutableStateOf(nomeAtual) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar categoria") },
        text = {
            TextFieldDesignForMainScreen(
                value = novoNome,
                onValueChange = { novoNome = it },
                label = "Novo nome"
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(novoNome) }) {
                Text("Confirmar", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        },
        containerColor = MaterialTheme.colorScheme.primary,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        textContentColor = MaterialTheme.colorScheme.onPrimary,
    )
}

@Composable
fun CategoryRow(
    painter: Int,
    contentDescripiton: String,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = painter),
            contentDescription = contentDescripiton,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

fun OpenPasswordsActivity(categoria: String, context: Context) {
    val intent = Intent(context, PasswordsActivity::class.java).apply {
        putExtra("categoria", categoria)
    }
    context.startActivity(intent)
}
