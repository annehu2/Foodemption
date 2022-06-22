package com.example.foodemption

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    LandingPage()
                }
            }
        }
    }
}

@Composable
fun LandingPage() {

    Box(Modifier.fillMaxSize()) {
        val image: Painter = painterResource(id = R.drawable.logo_title)
        Image(
            painter = image,
            contentDescription = "",
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 200.dp)
        )
    }
    Box(Modifier.padding(bottom = 250.dp), Alignment.BottomCenter) {
        OutlinedButton(onClick = { /*TODO*/ },
            colors = ButtonDefaults.textButtonColors(backgroundColor = Color.Blue),
            modifier = Modifier.width(200.dp)) {
            Text("Login", color = Color.White)
        }
    }
    Box(Modifier.padding(bottom = 200.dp), Alignment.BottomCenter) {
        OutlinedButton(onClick = { /*TODO*/ },
            colors = ButtonDefaults.textButtonColors(backgroundColor = Color.Blue),
            modifier = Modifier.width(200.dp)) {
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