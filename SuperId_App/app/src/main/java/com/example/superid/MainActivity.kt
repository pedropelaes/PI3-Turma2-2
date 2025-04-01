package com.example.superid

import android.graphics.Paint.Align
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.waterfallPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
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
@Preview(showBackground = true)
@Composable
fun SuperID() {
    ViewPagerForInitialScreens()
}

@Composable //Essa função é responsável pelo design das páginas de íniciais
fun InitialScreensDesign(
    imageResId: Int,
    statusBarColor: Color = Color(152034),
    navigationBarColor: Color = Color(152034),
    content: @Composable () -> Unit,
    bottomContent: @Composable () -> Unit
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
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center){
                content()
            }
            bottomContent()
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
            InitialScreensDesign(R.drawable.lockers_background, content = {
                when (page) {
                    0 -> Screen1()
                    1 -> Screen2()
                    2 -> Screen3()
                }
            }, bottomContent = {
                HorizontalPagerIndicator(
                    pageCount = pagerState.pageCount,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 32.dp)
                )
            })

        }
    }
}

@Composable
fun Screen1(){
    val title_font = FontFamily(Font(R.font.fonte_titulo))
    Column(modifier = Modifier){
        Text(
            buildAnnotatedString { //junta strings com estilos diferentes
                withStyle(
                    style = SpanStyle(fontFamily = FontFamily.SansSerif ,fontSize = 50.sp, color = Color.White, fontWeight = FontWeight.Bold)){
                    append("Bem vindo ao ")
                }
                withStyle(
                    style = SpanStyle(fontFamily = title_font, fontSize = 40.sp, color = Color.Black, background = Color.White)){
                    append("Super")
                }
                withStyle(
                    style = SpanStyle(fontFamily = title_font, fontSize = 40.sp, color = Color(0xFF152034), background = Color.White)){
                    append(" ID")
                }
            },
            textAlign = TextAlign.Center,
            modifier = Modifier
                .wrapContentWidth()
                .padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(R.string.app_description), color = Color.White ,fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.wrapContentWidth()
                .padding(16.dp)
        )
    }
}

@Composable
fun Screen2(){
    Text("Termos de condição", color = Color.White)
}

@Composable
fun Screen3(){
    Text("Tela de cadastro", color = Color.White)
}


@Composable
fun HorizontalPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.DarkGray,
    indicatorSize: Dp = 16.dp,
    spacing: Dp = 4.dp
) {
    Row(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 0.dp),
        horizontalArrangement = Arrangement.Center //centraliza os elementos de row
    ) {
        repeat(pageCount) { index ->
            val color = if (currentPage == index) activeColor else inactiveColor
            Box(
                modifier = Modifier
                    .padding(horizontal = spacing)
                    .size(indicatorSize)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}