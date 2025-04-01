package com.example.superid

import android.graphics.Paint.Align
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.superid.ui.theme.SuperIdTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                SuperID()
        }
    }
}
@Preview
@Composable
fun SuperID() {
    ViewPagerForInitialScreens()
}

@Composable //Essa função é responsável pelo design das páginas de íniciais
fun InitialScreensDesign(
    imageResId: Int,
    statusBarColor: Color = Color(152034),
    navigationBarColor: Color = Color(152034),
    content: @Composable () -> Unit
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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ){
            content()
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewPagerForInitialScreens() { //view pager das paginas iniciais
    val pagerState = rememberPagerState(pageCount = { 3 })

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> Screen1()
                1 -> Screen2()
                2 -> Screen3()
            }
        }
    }
}

@Composable
fun Screen1(){
    InitialScreensDesign(R.drawable.lockers_background){
        Text("Bem vindo ao SuperID", color = Color.White)
    }
}
@Composable
fun Screen2(){
    InitialScreensDesign(R.drawable.lockers_background){
        Text("Termos de condição", color = Color.White)
    }
}
@Composable
fun Screen3(){
    InitialScreensDesign(R.drawable.lockers_background){
        Text("Tela de login", color = Color.White)
    }
}