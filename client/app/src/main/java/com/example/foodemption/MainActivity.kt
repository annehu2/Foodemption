package com.example.foodemption

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.foodemption.ui.theme.FoodemptionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodemptionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LandingPage(this)
                }
            }
        }
    }
}

@Composable
fun LandingPage(context: Context) {

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        val image: Painter = painterResource(id = R.drawable.logo_title)
        Image(
            painter = image,
            contentDescription = "",
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 200.dp)
        )
        Box(modifier = Modifier.padding(top = 20.dp))
        OutlinedButton(
            onClick = {
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent) },
            colors = ButtonDefaults.textButtonColors(backgroundColor = Color.Blue),
            modifier = Modifier.width(200.dp)
        ) {
            Text("Login", color = Color.White)
        }
        Box(modifier = Modifier.padding(top = 20.dp))
        OutlinedButton(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.textButtonColors(backgroundColor = Color.Blue),
            modifier = Modifier.width(200.dp)
        ) {
            Text("Sign Up", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FoodemptionTheme {
    }
}