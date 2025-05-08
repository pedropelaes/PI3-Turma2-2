package com.example.superid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import com.example.superid.ui.theme.SuperIdTheme
import com.example.superid.ui.theme.ui.common.PasswordRow
import com.example.superid.ui.theme.ui.common.SuperIdTitle
import com.example.superid.ui.theme.ui.common.TextFieldDesignForMainScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import utils.ChaveAesUtils
import utils.CriptoUtils


class PasswordsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            val categoria = intent.getStringExtra("categoria")
            val icone = intent.getIntExtra("icone", R.drawable.logo_without_text)
            SuperIdTheme(darkTheme = isSystemInDarkTheme()) {
                PasswordsScreen(categoria, icone, SenhasViewModel())
            }
        }
    }
}
data class Senha(
    var login: String = "",
    var senha: String = "",
    var descricao: String = "",
    val id: String = "",
    var iv:  String = ""
)

class SenhasViewModel : ViewModel() {  //view model para buscar as senhas que estão no banco de dados
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    var listaSenhas by mutableStateOf<List<Senha>>(emptyList()) //lista que guarda as senhas vindas do banco
        private set

    fun buscarSenhas(categoria: String?) {
        val uid = auth.currentUser?.uid
        if (uid != null && categoria != null) { //verifica se há um usuario logado e se a categoria existe
            db.collection("users")
                .document(uid)
                .collection("categorias")
                .document(categoria)
                .collection("senhas")
                .whereNotEqualTo(FieldPath.documentId(), "placeholder") //não exibe o placeholder caso ele ainda esteja no banco
                .get()
                .addOnSuccessListener { result ->
                    listaSenhas = result.toObjects(Senha::class.java)
                    Log.d("GETPASSWORDS", "Senhas buscadas no banco")
                }
                .addOnFailureListener { exception ->
                    Log.w("GETPASSWORDS", "Erro ao buscar senhas no banco", exception)
                }
        } else {
            Log.e("GETPASSWORDS", "UID ou categoria nulo")
        }
    }
}

@Composable
fun PasswordsScreen(categoria: String?, icone: Int, viewModel: SenhasViewModel){
    var senhasCriadas by remember { mutableStateOf(listOf<Senha>()) } //variavel das senhas iniciada com o estado do view model
    var context = LocalContext.current
    val db = Firebase.firestore
    val auth = Firebase.auth
    PasswordsScreenDesign(
        categoria = categoria, //nome da categoria
        iconPainter = icone, //icone da categoria
        onAddPassword = { novaSenha ->   //adicionando nova senha
            val UID = auth.currentUser?.uid
            if (UID != null) {
                if (categoria != null) {
                    val senhasRef = db.collection("users")
                        .document(UID)
                        .collection("categorias")
                        .document(categoria)
                        .collection("senhas")

                    // Recupera a chave AES do Firestore
                    ChaveAesUtils.recuperarChaveDoUsuario(
                        UID,
                        db,
                        onSuccess = { chaveBase64 ->
                            val secretKey = CriptoUtils.base64ToSecretKey(chaveBase64)

                            // Criptografa a senha que o usuário digitou
                            val (senhaCripto, iv, accessToken) = CriptoUtils.encrypt(novaSenha.senha, secretKey)
                            val novoId = senhasRef.document().id

                            // Agora salva isso no Firestore
                            val doc = mapOf(
                                "senhaCriptografada" to senhaCripto,
                                "iv" to iv,
                                "accessToken" to accessToken,
                                "login" to novaSenha.login,
                                "descricao" to novaSenha.descricao,
                                "id" to novoId,
                            )
                            novaSenha.iv=iv
                            senhasRef.document(novoId)
                                .set(doc)
                                .addOnSuccessListener {
                                    viewModel.buscarSenhas(categoria) //busca de novo as senhas após uma nova ser adicionada
                                }
                        },
                        onFailure = { e -> Log.e("CRYPTO", "Erro ao buscar chave AES", e) }
                    )

                        senhasRef.document("placeholder") //deleta o placeholder caso ainda exista
                            .delete()
                            .addOnSuccessListener {
                                Log.d("ADDPASSWORD", "Placeholder deletado")
                            }
                            .addOnFailureListener{
                                Log.w("ADDPASSOWRD", "Erro ao deletar placeholder", it)
                            }

                }else{
                    Log.e("ADDPASSWORD", "Erro ao adicionar senha, variavel de categoria vazia")
                }
            }else{
                Toast.makeText(context, "É nescessário ter feito login para adicionar uma senha", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, LogInActivity::class.java)
                context.startActivity(intent)
            }

        }
    ) {
        LaunchedEffect(categoria) { //inicia o view model e busca as senhas
            viewModel.buscarSenhas(categoria)
        }
        val senhas = viewModel.listaSenhas
        ColumnSenhas(senhasCriadas = senhas, categoria, viewModel)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordsScreenDesign(
    statusBarColor: Color = Color.Transparent,
    navigationBarColor: Color = Color.Transparent,
    categoria: String?,
    onAddPassword: (Senha) -> Unit,
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
                            modifier = Modifier
                                .wrapContentHeight()
                                .padding(6.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        if (categoria != null) {
                            Text("${categoria.capitalize()}:", color = MaterialTheme.colorScheme.onPrimary)
                        }else{
                            Text("Senhas:", color = MaterialTheme.colorScheme.onPrimary)
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
            AddPasswordDialog(
                onDismiss = {showDialog = false},
                onConfirm = { senhaCriada->
                    onAddPassword(senhaCriada)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AddPasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (Senha) -> Unit
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
                TextFieldDesignForMainScreen(value = senha.login, onValueChange = {senha = senha.copy(login = it)}, label = "Login(opcional)")
                Spacer(modifier = Modifier.size(4.dp))
                TextFieldDesignForMainScreen(value = senha.senha, onValueChange = {senha = senha.copy(senha = it)}, label = "Senha(*obrigatório)", isPassword = true)
                Spacer(modifier = Modifier.size(4.dp))
                TextFieldDesignForMainScreen(value = senha.descricao, onValueChange = {senha = senha.copy(descricao = it)}, label = "Descrição(opcional)")
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(senha) },
                enabled = if((senha.senha).isNotEmpty()) true else false
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
fun ViewPasswordInfoDialog(
    senha: Senha,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Informação da senha:")
        },
        modifier = Modifier.wrapContentSize(),
        text = {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Login:\n${senha.login}")
                Text("Senha:\n${senha.senha}")
                Text("Descrição:\n${senha.descricao}")
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Editar", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Voltar", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        },
        containerColor = MaterialTheme.colorScheme.primary, // Cor de fundo do dialog
        titleContentColor = MaterialTheme.colorScheme.onPrimary, // Cor do título
        textContentColor = MaterialTheme.colorScheme.onPrimary, // Cor do texto
    )
}

@Composable
fun EditPasswordDialog(
    senha: Senha,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onConfirm: (Senha) -> Unit
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
                TextFieldDesignForMainScreen(value = senhaState.login, onValueChange = {senhaState = senhaState.copy(login = it)}, label = "Login(opcional)")
                Spacer(modifier = Modifier.size(4.dp))
                TextFieldDesignForMainScreen(value = senhaState.senha, onValueChange = {senhaState = senhaState.copy(senha = it)}, label = "Senha(*obrigatório)", isPassword = true)
                Spacer(modifier = Modifier.size(4.dp))
                TextFieldDesignForMainScreen(value = senhaState.descricao, onValueChange = {senhaState = senhaState.copy(descricao = it)}, label = "Descrição(opcional)")
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                TextButton(
                    onClick = onDelete
                ) {
                    Text("Apagar senha", color = MaterialTheme.colorScheme.tertiary)
                }

                TextButton(
                    onClick = { onConfirm(senhaState) }

                ) {
                    Text("Confirmar", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Voltar", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        },
        containerColor = MaterialTheme.colorScheme.primary, // Cor de fundo do dialog
        titleContentColor = MaterialTheme.colorScheme.onPrimary, // Cor do título
        textContentColor = MaterialTheme.colorScheme.onPrimary, // Cor do texto
    )
}

fun EditPasswordOnFirestore(categoria: String?, senha: Senha, novaSenha: Senha){
    val db = Firebase.firestore
    val auth = Firebase.auth

    val senhaEditada = mapOf(  //junta os novos valores da senha
        "login" to novaSenha.login,
        "senha" to novaSenha.senha,
        "descricao" to novaSenha.descricao
    )

    val uid = auth.currentUser?.uid
    if (uid != null && categoria != null) {
        db.collection("users")
            .document(uid)
            .collection("categorias")
            .document(categoria)
            .collection("senhas")
            .document(senha.id)
            .update(senhaEditada)
            .addOnSuccessListener {
                Log.d("UPDATEPASSWORD", "Senha atualizada")
            }
            .addOnFailureListener { e->
                Log.e("UPDATEPASSWORD", "Erro ao atualizar senha", e)
            }
    } else {
        Log.e("GETPASSWORDS", "UID ou categoria nulo")
    }
}

fun DeletePasswordOnFirestore(categoria: String?, senha: Senha){
    val db = Firebase.firestore
    val auth = Firebase.auth
    val uid = auth.currentUser?.uid

    if (uid != null && categoria != null) {
        db.collection("users")
            .document(uid)
            .collection("categorias")
            .document(categoria)
            .collection("senhas")
            .document(senha.id)
            .delete()
            .addOnSuccessListener {
                Log.d("DELETEPASSWORD", "Senha deletada")
            }
            .addOnFailureListener { e->
                Log.e("DELETEPASSWORD", "Erro ao apagar senha", e)
            }
    } else {
        Log.e("GETPASSWORDS", "UID ou categoria nulo")
    }
}


@Composable
fun ColumnSenhas(
    senhasCriadas: List<Senha>,
    categoria: String?, //nome da categoria
    viewModel: SenhasViewModel
){
    var senhaDescriptografada by remember { mutableStateOf("") }
    val auth=Firebase.auth
    val uid=auth.currentUser?.uid
    var showInfoDialog by remember { mutableStateOf(false) } //variavel do estado de exibição do dialog de informação
    var showEditDialog by remember { mutableStateOf(false) } //variavel do estado de exibição do dialog de editar
    var senhaClicada by remember { mutableStateOf(Senha())} //variavel para guardar a senha da row que foi clicada
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        senhasCriadas.forEach{senha ->  //para cada senha da lista é criada uma row
            PasswordRow(
                contentDescripiton = "Senha: ${senha.descricao}",
                text = "Senha: ${senha.descricao}",
                onClick = {
                    senhaClicada = senha
                    showInfoDialog = true
                }
            )
        }
    }
    if(showInfoDialog && senhaDescriptografada.isNotBlank()){  //exibe o dialog de informações da senha
        if (uid != null) {
            ChaveAesUtils.recuperarChaveDoUsuario(
                uid,
                onSuccess = { chaveBase64 ->
                    val secretKey = CriptoUtils.base64ToSecretKey(chaveBase64)

                    senhaDescriptografada = CriptoUtils.decrypt(
                        encryptedText = senhaClicada.senha,  // pego do Firestore
                        ivBase64 = senhaClicada.iv,                        // pego do Firestore
                        secretKey = secretKey
                    )

                    // Aqui você pode exibir a senha em um AlertDialog, Toast, ou atualizar um estado Compose

                },
                onFailure = { e ->
                    Log.e("DESCRIPTO", "Erro ao descriptografar senha", e)
                }
            )
        }
        ViewPasswordInfoDialog(
            senha = senhaClicada.copy(senha = senhaDescriptografada),
            onDismiss = {
                showInfoDialog = false
                senhaDescriptografada = ""
            },
            onConfirm = {
                showInfoDialog = false
                showEditDialog = true
                senhaDescriptografada = ""
            }
        )


    }
    if(showEditDialog){ //exibe o dialog de editar senha
        EditPasswordDialog(
            senha = senhaClicada,
            onDismiss = { showEditDialog = false },
            onDelete = {
                DeletePasswordOnFirestore(categoria, senhaClicada)
                showEditDialog = false

                viewModel.buscarSenhas(categoria)
            },
            onConfirm = { senhaAtualizada ->
                EditPasswordOnFirestore(
                    categoria = categoria,
                    senha = senhaClicada,
                    novaSenha = senhaAtualizada
                )
                showEditDialog = false

                viewModel.buscarSenhas(categoria)
            }
        )
    }
}