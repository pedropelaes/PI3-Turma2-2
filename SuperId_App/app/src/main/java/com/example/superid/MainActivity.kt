package com.example.superid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.superid.ui.theme.SuperIdTheme
import com.example.superid.ui.theme.ui.common.DialogVerificarConta
import com.example.superid.ui.theme.ui.common.SendEmailVerification
import com.example.superid.ui.theme.ui.common.StatusAndNavigationBarColors
import com.example.superid.ui.theme.ui.common.SuperIdTitle
import com.example.superid.ui.theme.ui.common.TextFieldDesignForMainScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            SuperIdTheme {
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
    var categoriasCriadas by remember { mutableStateOf(listOf<String>()) }                          // variável que guarda as categorias criadas pelo usuario
    var categoriaParaExcluir by remember { mutableStateOf<String?>(null) }
    var showDialogExcluir by remember { mutableStateOf(false) }                               // variável de estado de exibição do dialog de Exclusão

    val visibleMap = remember { mutableStateMapOf<String, MutableTransitionState<Boolean>>() }      // variável usadas para as animações
    val scope = rememberCoroutineScope()                                                            // coroutineScope das animações

    LaunchedEffect(userId) {                                                                        // busca as categorias do usuario
        userId?.let { uid ->
            db.collection("users")
                .document(uid)
                .collection("categorias")
                .addSnapshotListener { snapshot, _ ->
                    snapshot?.let {
                        categoriasCriadas = it.documents.mapNotNull { doc ->
                            doc.getString("nome")
                        }//.filterNot { nome-> //filtra as categorias pré criadas para evitar repetição
                           // nome in categoriasFixas
                       // }
                    }
                }
        }
    }

    MainScreenDesign(
        visibleMap = visibleMap,
        scope = scope,
        categoriasCriadas = categoriasCriadas,
        onAdicionarCategoria = { novaCategoria ->                                                                               // adicionando categorias
            val nomeNormalizado = novaCategoria.trim().lowercase()
            val nomesExistentes = categoriasCriadas.map { it.trim().lowercase() }

            if (nomeNormalizado in nomesExistentes) {
                Toast.makeText(context, "Já existe uma categoria com esse nome.", Toast.LENGTH_SHORT).show()
            }else if(nomeNormalizado == "sites"){
                Toast.makeText(context, "Nome proibido.", Toast.LENGTH_SHORT).show()
            }else if(nomeNormalizado.length > 18){
                Toast.makeText(context, "Categorias podem ter no máximo 18 caracteres", Toast.LENGTH_SHORT).show()
            }
            else{
                categoriasCriadas = categoriasCriadas + novaCategoria
                userId?.let { uid ->
                    db.collection("users")
                        .document(uid)
                        .collection("categorias")
                        .add(mapOf("nome" to novaCategoria))
                        .addOnSuccessListener {
                            Toast.makeText(context, "Categoria criada!", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        },
        categoriaParaExcluir = categoriaParaExcluir,
        onExcluirCategoria = { nome ->
            categoriaParaExcluir = nome
            showDialogExcluir = true
        },
        showDialogExcluir = showDialogExcluir,
        onConfirmarExclusao = {                                                                     // excluindo categoria
            val nomeExcluir = categoriaParaExcluir
            if (nomeExcluir != null && userId != null && nomeExcluir != "sites") {
                val transitionState = visibleMap[nomeExcluir]
                //atualizando a UI de maneira otimista, removendo a categoria a ser excluida da UI antes de ela ser excluida no banco
                showDialogExcluir = false
                categoriaParaExcluir = null

                transitionState?.targetState = false

                scope.launch {
                    delay(300L)  //delay para a animação

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
                                        Toast.makeText(
                                            context,
                                            "Categoria deletada!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        visibleMap.remove(nomeExcluir)
                                    }.addOnFailureListener { error->
                                        Toast.makeText(
                                            context,
                                            "Houve um erro ao deletar a categoria. Erro:$error",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        transitionState?.targetState = true
                                    }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Erro ao deletar", Toast.LENGTH_SHORT).show()
                            transitionState?.targetState = true
                        }
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
    visibleMap: SnapshotStateMap<String, MutableTransitionState<Boolean>>,
    scope: CoroutineScope
) {
    val topBarColor = if(isSystemInDarkTheme()) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant
    StatusAndNavigationBarColors()
    var isSearching by remember { mutableStateOf(false) }                                                   // variável de estado da busca
    var searchQuery by remember { mutableStateOf("") }                                                      // variável para busca
    var showVerifyAccountDialog by remember { mutableStateOf(false) }                                       // variável de estado
    var showDialog by remember { mutableStateOf(false) }                                                    // variável de estado
    var showEditDialog by remember { mutableStateOf(false) }                                                // variável de estado
    var categoriaParaEditar by remember { mutableStateOf("") }
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(user){                                                                           // verifica se o usuário tem e-mail verificado ao
        user?.reload()                                                                              // abrir a tela inicial, se não tem, exibe o dialog
        if(!user?.isEmailVerified!!){                                                               // de verificação
            showVerifyAccountDialog = true
        }
    }

    Scaffold(
        topBar = {
            Column{
                TopAppBar(
                    title = {
                        if(isSearching){                                                                        // troca o titulo pela barra de busca se
                            TextFieldDesignForMainScreen(searchQuery, {searchQuery = it}, "Buscar")       // estiver pesquisando
                        } else {
                        SuperIdTitle(modifier = Modifier.size(10.dp))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = topBarColor
                    ),
                    actions = {
                        if (isSearching) {
                            TextButton(onClick = {
                                searchQuery = ""
                                isSearching = false
                            }) {
                                Text("Cancelar", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        } else {
                            IconButton(onClick = { isSearching = true }) {
                                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                )
                HorizontalDivider(
                    thickness = 4.dp,
                    color = Color.White
                )
            }
        },
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = {
                        if (user != null) {                                                         // permite abrir a tela de login por QR-Code apenas se
                            user.reload()                                                           // o usuario tiver e-mail verificado
                            if(user.isEmailVerified){
                                val intent = Intent(context, QrCodeAuthActivity::class.java)
                                context.startActivity(intent)
                            }else{
                                showVerifyAccountDialog = true
                            }
                        }
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
                    onClick = { showDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Criar Categoria",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text("Criar Categoria", color = MaterialTheme.colorScheme.onPrimary)
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
            val categoriasPredefinidas = listOf(                                                    // lista para exibição das categorias default
                Triple("Sites", R.drawable.world_wide_web, "Categoria Sites"),
                Triple("Aplicativos", R.drawable.smartphone, "Categoria Aplicativos"),
                Triple("E-mails", R.drawable.email, "Categoria Emails"),
                Triple("Teclados de acesso", R.drawable.numeric_keypad, "Categoria Teclados de acesso físicos")
            )
            val nomesPredefinidos = categoriasPredefinidas.map{it.first}                            // lista com os nomes das categorias default


            LaunchedEffect(categoriasCriadas.size) {                                                          // lança as animações das categorias
                (categoriasPredefinidas.map { it.first } + categoriasCriadas).forEachIndexed { index, nome ->
                    scope.launch {
                        delay(100L * index)
                        val state = visibleMap.getOrPut(nome) {
                            MutableTransitionState(false)
                        }
                        state.targetState = true // animação de entrada controlada
                    }
                }
            }

            LazyColumn(                                                                             // lazy column que exibe as categorias
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 80.dp)
            ) {
                items(categoriasPredefinidas.filter { it.first.contains(searchQuery, ignoreCase = true)             // categorias são filtradas com a busca
                        && it.first in categoriasCriadas }, key = { it.first }) { (nome, icone, descricao) ->
                    val transitionState = visibleMap.getOrPut(nome) {
                        MutableTransitionState(false)
                    }

                    AnimatedVisibility(
                        visibleState = transitionState,
                        enter = slideInVertically(initialOffsetY = { it / 4 }) + fadeIn(tween(300)),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        CategoryRow(
                            painter = icone,
                            contentDescripiton = descricao,
                            text = nome,
                            onClick = { OpenPasswordsActivity(nome, icone, context) },
                            onExcluirCategoria = {onExcluirCategoria(nome)},
                            onEditarCategoria = {
                                showEditDialog = true
                                categoriaParaEditar = nome
                            }
                        )
                    }
                }


                items(categoriasCriadas.filter { it.contains(searchQuery, ignoreCase = true) }.filterNot {it in nomesPredefinidos }, key = { it }) { nome ->
                    val transitionState = visibleMap.getOrPut(nome) {
                        MutableTransitionState(false)
                    }

                    AnimatedVisibility(
                        visibleState = transitionState,
                        enter = slideInVertically(initialOffsetY = { it / 4 }) + fadeIn(tween(300)),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        Box(modifier = Modifier.animateItem()) {
                            CategoryRow(
                                contentDescripiton = "Categoria $nome",
                                text = nome,
                                onClick = {
                                    OpenPasswordsActivity(
                                        nome,
                                        R.drawable.logo_without_text,
                                        context
                                    )
                                },
                                onExcluirCategoria = {
                                    onExcluirCategoria(nome)
                                },
                                onEditarCategoria = {
                                    showEditDialog = true
                                    categoriaParaEditar = nome
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {                                                                           // exibição dos dialogs
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
                onConfirm = { novoNome ->                                                           // editando categorias
                    val nomeNormalizado = novoNome.trim().lowercase()
                    val nomesExistentes = categoriasCriadas
                        .filter { it != categoriaParaEditar }
                        .map { it.trim().lowercase() }

                    if (nomeNormalizado in nomesExistentes) {
                        Toast.makeText(context, "Já existe uma categoria com esse nome!", Toast.LENGTH_SHORT).show()
                    }else if(nomeNormalizado == "sites"){
                        Toast.makeText(context, "Nome proibido", Toast.LENGTH_SHORT).show()
                    }else if(nomeNormalizado.length > 18){
                        Toast.makeText(context, "Categorias podem ter no máximo 18 caracteres", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        if (novoNome.isNotBlank() && userId != null && novoNome != categoriaParaEditar) {
                            val transitionState = visibleMap[categoriaParaEditar]

                            showEditDialog = false

                            scope.launch {
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
                                                    Toast.makeText(
                                                        context,
                                                        "Categoria renomeada!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    visibleMap[novoNome]?.targetState = true
                                                    scope.launch{
                                                        delay(300L)
                                                        visibleMap.remove(categoriaParaEditar)
                                                    }
                                                }.addOnFailureListener {error->
                                                    Toast.makeText(
                                                        context,
                                                        "Houve um erro ao editar a categoria. Erro:$error",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    transitionState?.targetState = true
                                                }
                                        }
                                    }
                            }
                        }
                    }
                    showEditDialog = false
                }
            )
        }
        if(showVerifyAccountDialog){
            DialogVerificarConta(
                onVerificar = {
                    SendEmailVerification(user, context)
                    showVerifyAccountDialog = false
                },
                onDismiss = { showVerifyAccountDialog = false }
            )
        }


        if (showDialogExcluir && categoriaParaExcluir != null) {
            AlertDialog(
                onDismissRequest = onCancelarExclusao,
                title = { Text("Apagar categoria \"${categoriaParaExcluir}\"?", color = MaterialTheme.colorScheme.onBackground) },
                text = { Text("Deseja mesmo apagar essa categoria?(não há reversão para esta ação, e todas as senhas dentro dela serão perdidas)", color = MaterialTheme.colorScheme.onBackground)  },
                confirmButton = {
                    TextButton(onClick = onConfirmarExclusao) {
                        Text("Confirmar", color = MaterialTheme.colorScheme.onBackground)
                    }
                },
                dismissButton = {
                    TextButton(onClick = onCancelarExclusao) {
                        Text("Cancelar", color = MaterialTheme.colorScheme.onBackground)
                    }
                },
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                textContentColor = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
fun DialogCriarCategoria(                                                                           // Composable do dialog de criar categoria
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var nomeDaCategoria by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Criando categoria:", color = MaterialTheme.colorScheme.onBackground) },
        text = {
            TextFieldDesignForMainScreen(
                value = nomeDaCategoria,
                onValueChange = { nomeDaCategoria = it },
                label = "Nome da categoria"
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(nomeDaCategoria) }) {
                Text("Confirmar", color = MaterialTheme.colorScheme.onBackground)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onBackground)
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        textContentColor = MaterialTheme.colorScheme.onPrimary,
    )
}

@Composable
fun DialogEditarCategoria(                                                                          // Composabçe do dialog de editar categoria
    nomeAtual: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var novoNome by remember { mutableStateOf(nomeAtual) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar categoria", color = MaterialTheme.colorScheme.onBackground) },
        text = {
            TextFieldDesignForMainScreen(
                value = novoNome,
                onValueChange = { novoNome = it },
                label = "Novo nome"
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(novoNome) }) {
                Text("Confirmar", color = MaterialTheme.colorScheme.onBackground)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onBackground)
            }
        },

        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        textContentColor = MaterialTheme.colorScheme.onPrimary,
    )
}

@Composable
fun CategoryRow(                                                                                    // Composable das categorias
    painter: Int = R.drawable.folder_icon,
    contentDescripiton: String,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onExcluirCategoria: (String) -> Unit,
    onEditarCategoria: (String) -> Unit,
) {
    Row(
        modifier = modifier
            .padding(12.dp)
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(15.dp),
                clip = false // clip precisa ser false para a sombra aparecer fora do shape
            )
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(15.dp)
            )
            .height(64.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = painter),
            contentDescription = contentDescripiton,
            modifier = Modifier
                .size(56.dp)
                .padding(start = 12.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier.weight(1f))
        if (text != "Sites"){                                                                       // exibe botões de apagar e editar somente se a categoria
            IconButton(                                                                             // não for Sites
                onClick = {
                    onEditarCategoria(text)
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
                    onExcluirCategoria(text)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Deletar categoria",
                    tint = Color.Red
                )
            }
        }
        Icon(
            painter = painterResource(R.drawable.right_arrow),
            contentDescription = contentDescripiton,
            modifier = Modifier
                .size(32.dp)
                .padding(end = 12.dp)
        )
    }
}

fun OpenPasswordsActivity(categoria: String, icone: Int = R.drawable.logo_without_text, context: Context) {  // Abre a tela de senhas da categoria clicada
    val intent = Intent(context, PasswordsActivity::class.java).apply {
        putExtra("categoria", categoria)
        putExtra("icone", icone)
    }
    context.startActivity(intent)
}
