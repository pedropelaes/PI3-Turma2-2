package com.example.superid

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.superid.ui.theme.SuperIdTheme

class PasswordsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            val categoria = intent.getStringExtra("categoria")
            SuperIdTheme(darkTheme = isSystemInDarkTheme()) {
                PasswordsScreen(categoria)
            }
        }
    }
}

@Composable
fun PasswordsScreenDesign(){

}

@Composable
fun PasswordsScreen(categoria: String?){
    if (categoria != null) {
        Text(categoria)
    }
}