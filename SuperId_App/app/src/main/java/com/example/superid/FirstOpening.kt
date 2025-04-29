package com.example.superid

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.superid.ui.theme.SuperIdTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.example.superid.ui.theme.ui.common.SuperIdTitle


class FirstOpeningActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SuperIdTheme(darkTheme = true) {
                SuperID()
            }
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
    statusBarColor: Color = Color.Transparent,
    navigationBarColor: Color = Color.Transparent,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ViewPagerForInitialScreens() { //view pager das paginas iniciais
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    var termsAccepted by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            InitialScreensDesign(R.drawable.lockers_background, content = {
                when (page) {
                    0 -> Screen1()
                    1 -> Screen2(termsAccepted, onTermsAcceptedChange = { termsAccepted = it })
                }
            }, bottomContent = {
                HorizontalPagerIndicator(
                    pageCount = pagerState.pageCount,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier.wrapContentWidth()
                )
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (pagerState.currentPage > 0) {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (pagerState.currentPage > 0) {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                            },
                            modifier = Modifier.wrapContentWidth()
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                            Text("Voltar", fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground, textDecoration = TextDecoration.Underline)
                        }
                        Spacer(modifier = Modifier.width(120.dp))
                    }

                    if (pagerState.currentPage == 0) {
                        Spacer(modifier = Modifier.width(250.dp))
                    }

                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                // Impede de ir para a próxima tela se não aceitou os termos.
                                if (pagerState.currentPage == 1 && !termsAccepted) {
                                    Toast.makeText(context, "É necessário aceitar os termos antes de prosseguir.", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }else if(pagerState.currentPage == 1 && termsAccepted) {
                                    val intent = Intent(context, SignUpActivity::class.java)
                                    context.startActivity(intent)
                                }
                                if (pagerState.currentPage < pagerState.pageCount - 1) {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        modifier = Modifier.wrapContentWidth()
                            .padding(8.dp)
                    ) {
                        Text("Próximo", fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground, textDecoration = TextDecoration.Underline)
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Próximo",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            })
        }
    }
}


@Composable
fun Screen1(){
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
        Text("Bem-vindo ao",fontFamily = FontFamily.SansSerif ,fontSize = 50.sp, color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        SuperIdTitle()

        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(R.string.app_description), color = MaterialTheme.colorScheme.onBackground ,fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
fun Screen2(termsAccepted: Boolean, onTermsAcceptedChange: (Boolean) -> Unit) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "Termos e Condições:",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Para usar o SuperID, você precisa aceitar nossos termos e condições:",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        ScrollableTextWithScrollbar()

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Checkbox(
                checked = termsAccepted,
                onCheckedChange = { onTermsAcceptedChange(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = colorResource(R.color.field_text_background)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Li e aceito os Termos e Condições",
                color = Color.White,
                fontSize = 16.sp
            )
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
            .wrapContentWidth()
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

@Composable
fun ScrollableTextWithScrollbar() {
    val scrollState = rememberScrollState()
    val boxHeight = 500.dp
    val scrollbarHeight = 60.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(boxHeight)
            .background(Color.LightGray.copy(0.15f))
            .border(2.dp, shape = RectangleShape, color = Color.White)
            .padding(8.dp)
    ) {
        Box {
            Text(
                text = stringResource(R.string.terms),
                color = Color.White,
                textAlign = TextAlign.Justify,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            )

            // Cálculo do offset com o LocalDensity
            val scrollProgress = scrollState.value.toFloat() / scrollState.maxValue.toFloat().coerceAtLeast(1f)
            val offsetY = with(LocalDensity.current) {
                ((boxHeight - scrollbarHeight) * scrollProgress).toPx()
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = Dp(offsetY / LocalDensity.current.density))
                    .width(4.dp)
                    .height(scrollbarHeight)
                    .background(Color.White.copy(alpha = 0.5f), shape = RoundedCornerShape(2.dp))
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


