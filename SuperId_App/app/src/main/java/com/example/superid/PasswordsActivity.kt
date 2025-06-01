package com.example.superid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.superid.ui.theme.SuperIdTheme
import com.example.superid.ui.theme.ui.common.DialogVerificarConta
import com.example.superid.ui.theme.ui.common.PasswordRow
import com.example.superid.ui.theme.ui.common.SendEmailVerification
import com.example.superid.ui.theme.ui.common.StatusAndNavigationBarColors
import com.example.superid.ui.theme.ui.common.TextFieldDesignForLoginAndSignUp
import com.example.superid.ui.theme.ui.common.TextFieldDesignForMainScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import utils.ChaveAesUtils
import utils.CriptoUtils
import utils.getSenhasRef
import utils.getSenhasRefDireto

data class Senha(                                                                                   // classe de dados Senha
    var login: String = "",
    var senha: String = "",
    var descricao: String = "",
    var id: String = "",
    var iv:  String = "",                                                                           // utilizado para criptografia
    var url: String = ""
)

class PasswordsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            val categoria = intent.getStringExtra("categoria")                                // nome da categoria que foi aberta na tela principal
            val icone = intent.getIntExtra("icone", R.drawable.logo_without_text)             // icone da categoria que foi aberta
            SuperIdTheme() {
                PasswordsScreen(categoria, icone, SenhasViewModel())
            }
        }
    }
}

class SenhasViewModel : ViewModel() {                                                               //view model para buscar as senhas que estão no banco de dados
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    var listaSenhas by mutableStateOf<List<Senha>>(emptyList())                                     //lista que guarda as senhas vindas do banco
        private set


    fun buscarSenhas(categoria: String?) {
        val uid = auth.currentUser?.uid
        if (uid != null && categoria != null) {                                                     //verifica se há um usuario logado e se a categoria existe
            val categoriasRef = db.collection("users")
                .document(uid)
                .collection("categorias")

            categoriasRef
                .whereEqualTo("nome", categoria)
                .get()
                .addOnSuccessListener { querySnapshot->
                    if(!querySnapshot.isEmpty){
                        val docCategoria = querySnapshot.documents.first()
                        val categoriaId = docCategoria.id
                        val senhasRef = getSenhasRefDireto(db, uid, categoriaId)

                        senhasRef
                            .whereNotEqualTo(
                                FieldPath.documentId(),
                                "placeholder"
                            ) //não exibe o placeholder caso ele ainda esteja no banco
                            .get()
                            .addOnSuccessListener { result ->
                                listaSenhas = result.toObjects(Senha::class.java)
                                Log.d("GETPASSWORDS", "Senhas buscadas no banco")
                            }
                            .addOnFailureListener { exception ->
                                Log.w("GETPASSWORDS", "Erro ao buscar senhas no banco", exception)
                            }
                    }else{
                        Log.e("ADDPASSWORD", "Categoria: '$categoria' não encontrada no banco")
                    }
                }.addOnFailureListener {
                    Log.e("GETPASSWORDS", "Erro ao buscar categoria no banco", it)
                }

        } else {
            Log.e("GETPASSWORDS", "UID ou categoria nulo")
        }
    }
}

@Composable
fun PasswordsScreen(categoria: String?, icone: Int, viewModel: SenhasViewModel){
    val context = LocalContext.current
    val db = Firebase.firestore
    val auth = Firebase.auth
    PasswordsScreenDesign(
        categoria = categoria,                                                                      //nome da categoria
        auth = auth,
        iconPainter = icone,                                                                        //icone da categoria
        onAddPassword = { novaSenha ->
            val uid = auth.currentUser?.uid
            if (uid == null || categoria == null) {
                Toast.makeText(context, "É necessário estar logado para adicionar uma senha", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LogInActivity::class.java))
                return@PasswordsScreenDesign
            }
            getSenhasRef(                                                                           // caminho para as senhas da categoria
                db = db,
                uid = uid,
                categoria = categoria,
                onSuccess = { senhasRef ->
                    ChaveAesUtils.recuperarChaveDoUsuario(                                          // utiliza a chave AES do usuário para criptografar
                        uid,
                        db,
                        onSuccess = { chaveBase64 ->
                            val secretKey = CriptoUtils.base64ToSecretKey(chaveBase64)                           // converte a chave AES do firestore
                            val (senhaCripto, iv, accessToken) = CriptoUtils.encrypt(novaSenha.senha, secretKey) // gera a senha criptografada, iv e accesstoken
                            val novoId = senhasRef.document().id


                            // Preenche os campos criptografados na instância
                            novaSenha.senha = senhaCripto
                            novaSenha.iv = iv
                            novaSenha.id = novoId

                            val doc = mutableMapOf(
                                "senha" to senhaCripto,
                                "iv" to iv,
                                "accessToken" to accessToken,
                                "login" to novaSenha.login,
                                "descricao" to novaSenha.descricao,
                                "id" to novaSenha.id,
                            )
                            if(categoria == "Sites") {                                              // se a categoria for sites, salva o campo url
                                senhasRef.whereEqualTo("url", novaSenha.url).get()
                                    .addOnSuccessListener { result ->
                                        if (result.isEmpty) {
                                            doc["url"] = novaSenha.url
                                            // Salva no Firestore
                                            senhasRef.document(novoId).set(doc)
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "Senha criada!", Toast.LENGTH_SHORT).show()
                                                    viewModel.buscarSenhas(categoria)
                                                }

                                            // Remove placeholder, se ainda existir
                                            senhasRef.document("placeholder").delete()
                                                .addOnSuccessListener {
                                                    Log.d("ADDPASSWORD", "Placeholder deletado")
                                                }
                                                .addOnFailureListener {
                                                    Log.w("ADDPASSWORD", "Erro ao deletar placeholder", it)
                                                }
                                        } else {
                                            Log.e("ADDPASSWORD", "URL já cadastrada!")
                                            Toast.makeText(context, "Só é possivel usar um URL apenas para uma senha.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }else{
                                senhasRef.document(novoId).set(doc)
                                    .addOnSuccessListener {
                                        viewModel.buscarSenhas(categoria)
                                        Toast.makeText(context, "Senha criada!", Toast.LENGTH_SHORT).show()
                                    }
                                // Remove placeholder, se ainda existir
                                senhasRef.document("placeholder").delete()
                                    .addOnSuccessListener {
                                        Log.d("ADDPASSWORD", "Placeholder deletado")
                                    }
                                    .addOnFailureListener {
                                        Log.w("ADDPASSWORD", "Erro ao deletar placeholder", it)
                                    }
                            }
                        },
                        onFailure = { e ->
                            Log.e("CRYPTO", "Erro ao buscar chave AES", e)
                        }
                    )
                },
                onFailure = { e ->
                    Log.e("ADDPASSWORD", e.message ?: "Erro desconhecido")
                }
            )
        }
    ) { searchQuery ->
        LaunchedEffect(categoria) {                                                                 //inicia o view model e busca as senhas
            viewModel.buscarSenhas(categoria)
        }
        val senhas by remember { derivedStateOf { viewModel.listaSenhas } }
        ColumnSenhas(senhasCriadas = senhas, categoria, viewModel, auth, db, searchQuery)
    }
}

fun EditPasswordOnFirestore(categoria: String?, senha: Senha, novaSenha: Senha, viewModel: SenhasViewModel, db:FirebaseFirestore, auth: FirebaseAuth, context: Context){
    val uid = auth.currentUser?.uid
    if (uid != null && categoria != null) {
        ChaveAesUtils.recuperarChaveDoUsuario(
            uid,
            db,
            onSuccess = { chaveBase64 ->
                val secretKey = CriptoUtils.base64ToSecretKey(chaveBase64)

                // Criptografa a senha que o usuário digitou
                val (senhaCripto, iv, accessToken) = CriptoUtils.encrypt(novaSenha.senha, secretKey)  // criptografa a nova senha e gera um novo iv e accessToken

                val doc = mutableMapOf(
                    "senha" to senhaCripto,
                    "login" to novaSenha.login,
                    "iv" to iv,
                    "accessToken" to accessToken,
                    "descricao" to novaSenha.descricao,
                )
                if(categoria == "Sites"){                                                           // se a categoria for sites, adiciona o campo url
                    doc["url"] = novaSenha.url
                }

                getSenhasRef(db, uid, categoria,
                    onSuccess = { senhasRef ->
                        senhasRef.document(senha.id)
                            .update(doc as Map<String, Any>)
                            .addOnSuccessListener {
                                Log.d("UPDATEPASSWORD", "Senha atualizada")
                                Toast.makeText(
                                    context,
                                    "Senha atualizada!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                viewModel.buscarSenhas(categoria)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Houve um erro ao editar a senha!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("UPDATEPASSWORD", "Erro ao atualizar senha", e)
                            }
                    },
                    onFailure = { e -> Log.e("UPDATEPASSWORD", e.message ?: "Erro desconhecido") }
                )
            },
            onFailure = { e -> Log.e("CRYPTO", "Erro ao buscar chave AES", e) }
        )
    } else {
        Log.e("UPDATEPASSWORD", "UID ou categoria nulo")
    }
}

fun DeletePasswordOnFirestore(categoria: String?, senha: Senha, db:FirebaseFirestore, auth: FirebaseAuth, context: Context){
    val uid = auth.currentUser?.uid

    if (uid != null && categoria != null) {
        getSenhasRef(db, uid, categoria,
            onSuccess = { senhasRef ->
                senhasRef.document(senha.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            context,
                            "Senha deletada!",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("DELETEPASSWORD", "Senha deletada")
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            "Houve um erro ao deletar a senha!",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("DELETEPASSWORD", "Erro ao apagar senha", e)
                    }
            },
            onFailure = { e -> Log.e("DELETEPASSWORD", e.message ?: "Erro desconhecido") }
        )
    } else {
        Log.e("DELETEPASSWORD", "UID ou categoria nulo")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordsScreenDesign(
    categoria: String?,
    auth: FirebaseAuth,
    onAddPassword: (Senha) -> Unit,
    iconPainter: Int,
    content: @Composable (String) -> Unit,
){
    val topBarColor = if(isSystemInDarkTheme()) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant
    StatusAndNavigationBarColors()
    var isSearching by remember { mutableStateOf(false) }                                       // variável de estado para a busca
    var searchQuery by remember { mutableStateOf("") }                                          // variável que guarda a busca
    var showAddPasswordDialog by remember { mutableStateOf(false) }                             // variável de estado para dialog
    var showVerifyAccountDialog by remember { mutableStateOf(false) }                           // variável de estado para dialog
    val context = LocalContext.current
    val user = auth.currentUser
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if(isSearching){
                        TextFieldDesignForMainScreen(searchQuery, {searchQuery = it}, "Buscar")
                    }else {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Icon(
                                painter = painterResource(iconPainter),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                contentDescription = "Icone da categoria",
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .padding(6.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            if (categoria != null) {
                                Text("${categoria.replaceFirstChar { it.uppercase() }}:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }else{
                                Text("Senhas:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topBarColor
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
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
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
        },
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                FloatingActionButton(                                                               // só permite o usuario abrir a tela de login por qr code
                    onClick = {                                                                     // se ele tiver o e-mail verificado
                        if (user != null) {
                            user.reload()
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
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }

                ExtendedFloatingActionButton(
                    onClick = {
                        showAddPasswordDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                ){
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Criar Categoria",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                    Text("Adicionar senha", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            content(searchQuery)                                                                    // conteudo da busca é passado para a tela de senhas
        }

        if (showAddPasswordDialog){
            AddPasswordDialog(
                onDismiss = {showAddPasswordDialog = false},
                onConfirm = { senhaCriada->
                    onAddPassword(senhaCriada)
                    showAddPasswordDialog = false
                },
                categoria = categoria
            )
        }
        if(showVerifyAccountDialog){
            DialogVerificarConta(
                onVerificar = {
                    SendEmailVerification(user, context)
                },
                onDismiss = { showVerifyAccountDialog = false }
            )
        }
    }
}

@Composable
fun AddPasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (Senha) -> Unit,
    categoria: String?
){
    var senha by remember { mutableStateOf(Senha()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Adicionar senha:")
        },
        modifier = Modifier.wrapContentSize(),
        text = {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if(categoria == "Sites"){
                    TextFieldDesignForLoginAndSignUp(value = senha.url, onValueChange = {senha = senha.copy(url = it)}, label = "Url(*obrigatório)")
                    Spacer(modifier = Modifier.size(12.dp))
                }
                TextFieldDesignForLoginAndSignUp(value = senha.login, onValueChange = {senha = senha.copy(login = it)}, label = "Login(opcional)")
                Spacer(modifier = Modifier.size(12.dp))
                TextFieldDesignForLoginAndSignUp(value = senha.senha, onValueChange = {senha = senha.copy(senha = it)}, label = "Senha(*obrigatório)", isPassword = true)
                Spacer(modifier = Modifier.size(12.dp))
                TextFieldDesignForLoginAndSignUp(value = senha.descricao, onValueChange = {senha = senha.copy(descricao = it)}, label = "Descrição(opcional)")
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(senha) },
                enabled = if (categoria == "sites") senha.url.isNotEmpty() && senha.senha.isNotEmpty() else senha.senha.isNotEmpty()
            ) {
                Text("Confirmar", color = MaterialTheme.colorScheme.onBackground)
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


@Composable
fun PasswordInfo(                                                                                   // composable para a informação da senha clicada
    senha: Senha,
    categoria: String?
){
    Column(
        horizontalAlignment = Alignment.Start,
    ) {
        Column(
            Modifier.padding(bottom = 5.dp)
        ) {
            if (categoria == "Sites") {
                Text(
                    "Url:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                )
                Text(senha.url, fontSize = 19.sp)
            }
        }
        Column(
            Modifier.padding(bottom = 5.dp)
        ) {
            Text(
                "Login:",
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp
            )
            Text(senha.login, fontSize = 19.sp)
        }
        Column(
            Modifier.padding(bottom = 5.dp)
        ) {
            Text(
                "Senha:",
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp
            )
            Text(senha.senha, fontSize = 19.sp)
        }
        Column {
            Text(
                "Descrição:",
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp
            )
            Text(senha.descricao, fontSize = 19.sp)
        }
    }
}

@Composable
fun ViewPasswordInfoDialog(
    senha: Senha,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onConfirm: () -> Unit,
    categoria: String?
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = "Informações da senha")
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        painter = painterResource(R.drawable.delete),
                        contentDescription = "Apagar senha",
                        modifier = Modifier.size(24.dp, 34.dp),
                        tint = Color.Red
                    )
                }
            }
        },
        modifier = Modifier.wrapContentSize(),
        text = {
            PasswordInfo(senha, categoria)
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Editar", color = MaterialTheme.colorScheme.onBackground)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Voltar", color = MaterialTheme.colorScheme.onBackground)
            }
        },
        containerColor = MaterialTheme.colorScheme.background, // Cor de fundo do dialog
        titleContentColor = MaterialTheme.colorScheme.onBackground, // Cor do título
        textContentColor = MaterialTheme.colorScheme.onBackground, // Cor do texto
    )
}

@Composable
fun ConfirmDeletePasswordDialog(
    senha: Senha,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
    categoria: String?
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Deseja apagar esta senha?")
        },
        modifier = Modifier.wrapContentSize(),
        text = {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                PasswordInfo(senha, categoria)
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDelete
            ) {
                Text("Sim", color = MaterialTheme.colorScheme.tertiary)
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

@Composable
fun EditPasswordDialog(
    senha: Senha,
    onDismiss: () -> Unit,
    onConfirm: (Senha) -> Unit,
    categoria: String?
){
    var senhaState by remember { mutableStateOf(senha) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Editar senha:")
        },
        modifier = Modifier.wrapContentSize(),
        text = {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if(categoria == "Sites"){
                    TextFieldDesignForLoginAndSignUp(value = senhaState.url, onValueChange = {senhaState = senhaState.copy(url = it)}, label = "Url(*obrigatório)")
                }
                TextFieldDesignForLoginAndSignUp(value = senhaState.login, onValueChange = {senhaState = senhaState.copy(login = it)}, label = "Login(opcional)")
                Spacer(modifier = Modifier.size(4.dp))
                TextFieldDesignForLoginAndSignUp(value = senhaState.senha, onValueChange = {senhaState = senhaState.copy(senha = it)}, label = "Senha(*obrigatório)", isPassword = true)
                Spacer(modifier = Modifier.size(4.dp))
                TextFieldDesignForLoginAndSignUp(value = senhaState.descricao, onValueChange = {senhaState = senhaState.copy(descricao = it)}, label = "Descrição(opcional)")
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(senhaState) },
                enabled = senhaState.senha.isNotEmpty()
            ) {
                Text("Confirmar", color = MaterialTheme.colorScheme.onBackground)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Voltar", color = MaterialTheme.colorScheme.onBackground)
            }
        },
        containerColor = MaterialTheme.colorScheme.background, // Cor de fundo do dialog
        titleContentColor = MaterialTheme.colorScheme.onBackground, // Cor do título
        textContentColor = MaterialTheme.colorScheme.onBackground, // Cor do texto
    )
}

@Composable
fun ColumnSenhas(                                                                                   // composable das senhas
    senhasCriadas: List<Senha>,
    categoria: String?,                                                                             //nome da categoria
    viewModel: SenhasViewModel,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    searchQuery : String
){
    val context = LocalContext.current
    var senhaDescriptografada by remember { mutableStateOf("") }
    val uid=auth.currentUser?.uid
    var showInfoDialog by remember { mutableStateOf(false) } //variavel do estado de exibição do dialog de informação
    var showEditDialog by remember { mutableStateOf(false) } //variavel do estado de exibição do dialog de editar
    var senhaClicada by remember { mutableStateOf(Senha())} //variavel para guardar a senha da row que foi clicada
    var showConfirmDelete by remember { mutableStateOf(false) } //variavel do estado de exibição do dialog de confirmação de deletar senha
    val visibleMap = remember { mutableStateMapOf<String, MutableTransitionState<Boolean>>() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(senhasCriadas) {                                                                 // efeito de animações para as senhas
        senhasCriadas.forEachIndexed { index, senha ->
            val state = visibleMap.getOrPut(senha.id) {
                MutableTransitionState(false)
            }
            delay(50L * index) // cascata de entrada
            state.targetState = true
        }
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalDivider(
            thickness = 4.dp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(
            modifier = Modifier.padding(bottom = 80.dp)
        ) {
            items(senhasCriadas.filter {                                                            // filtra as senhas de acordo com a busca
                it.login.contains(searchQuery, ignoreCase =  true ) ||
                it.descricao.contains(searchQuery, ignoreCase = true) ||
                it.url.contains(searchQuery, ignoreCase = true)
            }){senha->
                val infoSenha = when{                                                               // exibe o "nome" da senha por uma ordem de prioridade
                    senha.url.isNotBlank() -> senha.url                                             // url -> descrição -> login
                    senha.descricao.isNotBlank() -> senha.descricao
                    senha.login.isNotBlank() -> senha.login
                    else -> "Senha sem titulo"
                }
                val transitionState = visibleMap.getOrPut(senha.id) {
                    MutableTransitionState(false)
                }
                AnimatedVisibility(
                    visibleState = transitionState,
                    enter = slideInVertically(initialOffsetY = { it / 4 }) + fadeIn(tween(300)),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        PasswordRow(
                            contentDescripiton = infoSenha,
                            text = infoSenha,
                            onClick = {
                                senhaClicada = senha
                                if (uid != null) {
                                    ChaveAesUtils.recuperarChaveDoUsuario(                          // descriptografa a senha para exibição
                                        uid,
                                        onSuccess = { chaveBase64 ->
                                            val secretKey =
                                                CriptoUtils.base64ToSecretKey(chaveBase64)
                                            Log.d("SENHA", "$senhaClicada")
                                            senhaDescriptografada = CriptoUtils.decrypt(
                                                encryptedText = senhaClicada.senha,  // pego do Firestore
                                                ivBase64 = senhaClicada.iv,                        // pego do Firestore
                                                secretKey = secretKey
                                            )
                                            Log.d(
                                                "DESCRIPTO",
                                                "Senha descriptografada:$senhaDescriptografada",
                                            )
                                            showInfoDialog = true
                                        },
                                        onFailure = { e ->
                                            Log.e("DESCRIPTO", "Erro ao descriptografar senha", e)
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    if(showInfoDialog){                                                                             //exibe o dialog de informações da senha
        ViewPasswordInfoDialog(
            senha = senhaClicada.copy(senha = senhaDescriptografada),
            onDismiss = {
                showInfoDialog = false
                senhaDescriptografada = ""
            },
            onDelete = {
                showConfirmDelete = true
                showInfoDialog = false
            },
            onConfirm = {
                showInfoDialog = false
                showEditDialog = true
            },
            categoria = categoria
        )
    }
    if(showConfirmDelete){                                                                          // exibe o dialog de confirmar o apagamento da senha
        ConfirmDeletePasswordDialog(
            senha = senhaClicada.copy(senha = senhaDescriptografada),
            onDelete = {
                val senhaId = senhaClicada.copy(senha = senhaDescriptografada).id
                val transitionState = visibleMap[senhaId]
                showConfirmDelete = false

                scope.launch {                                                                      // anima o desaparecimento da senha
                    delay(200L)
                    transitionState?.targetState = false
                    delay(300L)
                    DeletePasswordOnFirestore(categoria, senhaClicada.copy(senha = senhaDescriptografada), db, auth, context)
                    visibleMap.remove(senhaId)
                    viewModel.buscarSenhas(categoria)
                }
            },
            onDismiss = {
                showConfirmDelete = false
                senhaDescriptografada = ""
            },
            categoria = categoria
        )
    }

    if(showEditDialog){                                                                             //exibe o dialog de editar senha
        EditPasswordDialog(
            senha = senhaClicada.copy(senha = senhaDescriptografada),
            onDismiss = { showEditDialog = false },
            onConfirm = { senhaAtualizada ->
                val senha = senhaClicada.copy(senha = senhaDescriptografada)
                if (senhaAtualizada == senha) {
                    showEditDialog = false
                    return@EditPasswordDialog
                }
                val transitionState = visibleMap[senha.id]
                showEditDialog = false

                scope.launch {                                                                      // anima a troca da senha
                    transitionState?.targetState = false
                    delay(300L)
                    EditPasswordOnFirestore(
                        categoria = categoria,
                        senha = senhaClicada.copy(senha = senhaDescriptografada),
                        novaSenha = senhaAtualizada,
                        viewModel = viewModel,
                        db = db,
                        auth = auth,
                        context = context
                    )
                    transitionState?.targetState = true
                    visibleMap[senha.id] = MutableTransitionState(false)
                    delay(50L)
                    visibleMap[senha.id]?.targetState = true
                    senhaDescriptografada = ""
                }
            },
            categoria = categoria
        )
    }
}