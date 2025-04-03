package com.example.superid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
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
//@Preview(showBackground = true)
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
@Preview
@Composable
fun Screen3(){
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
        Spacer(modifier = Modifier.padding(16.dp))
        Button(onClick = {}, shape = RectangleShape,
            border = BorderStroke(2.dp, Color.White),
            colors = ButtonColors(
                containerColor = Color.LightGray.copy(alpha = 0.5f),
                contentColor = Color.White,
                disabledContainerColor = Color.DarkGray,
                disabledContentColor = Color.Red,
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally).width(230.dp).height(60.dp)){
            Text("Fazer Cadastro", fontSize = 20.sp, fontFamily = FontFamily.SansSerif, color = Color.White)
        }
        Spacer(modifier = Modifier.padding(16.dp))
        Text("Já possuí conta?", fontSize = 18.sp, fontFamily = FontFamily.SansSerif, color = Color.White,
            modifier = Modifier.wrapContentWidth().align(Alignment.CenterHorizontally).background(Color.LightGray.copy(alpha = 0.2f))
        )
        Spacer(modifier = Modifier.padding(3.dp))
        Button(onClick = {}, shape = RectangleShape,
            border = BorderStroke(2.dp, Color.White),
            colors = ButtonColors(
                containerColor = Color.LightGray.copy(alpha = 0.5f),
                contentColor = Color.White,
                disabledContainerColor = Color.DarkGray,
                disabledContentColor = Color.Red,
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally).width(230.dp).height(60.dp)){
            Text("Fazer Login", fontSize = 20.sp, fontFamily = FontFamily.SansSerif, color = Color.White)
        }
    }
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

/*
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
 */


