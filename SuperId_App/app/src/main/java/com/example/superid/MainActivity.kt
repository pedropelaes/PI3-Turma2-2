package com.example.superid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.superid.ui.theme.SuperIdTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperIdTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SuperIdTheme {
        Greeting("Android")
    }
}

//ideia de codigo para usuario aceitar termos de uso
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_id)

    //verifica se ja aceitou alguma vez
    val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
    val aceitouTermos = sharedPreferences.getBoolean("aceitou_termos", false)

    if (!aceitouTermos) {
        mostrarTermosDeUso(sharedPreferences)
    }
}

//exibir os termos
private fun mostrarTermosDeUso(sharedPreferences: SharedPreferences) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Termos de Uso")
    // escrever os termos aqui
    // builder.setMessage("exemplo")

    builder.setPositiveButton("Aceitar") { dialog, _ ->

        val editor = sharedPreferences.edit()
        //salva se aceitou
        editor.putBoolean("aceitou_termos", true)
        editor.apply()
        dialog.dismiss()
    }

    builder.setNegativeButton("Sair") { dialog, _ ->
        dialog.dismiss()
        finish()
        //se o usuario nao aceitar os termos fecha o app
    }

    builder.setCancelable(false)
    builder.show()
    //nao deixa o usuario fechar o termo sem aceitar
}

