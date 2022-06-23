package com.example.foodemption

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodemption.ui.theme.FoodemptionTheme

class DonateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodemptionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DonatePage("org name")
                }
            }
        }
    }
}

@Composable
fun DonatePage(orgName: String) {
    Column(modifier = Modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        val image: Painter = painterResource(id = R.drawable.logo)
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)) {
            Image(
                painter = image,
                contentDescription = "",
                modifier = Modifier
                    .padding(start = 40.dp)
            )
            Column(modifier = Modifier
                .padding(start = 10.dp)) {
                Text(
                    "Donate",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    orgName,
                    fontSize = 16.sp,
                )
            }
        }

        Box(modifier = Modifier.padding(top = 20.dp))

        Box(
            modifier = Modifier
                .width(293.dp)
                .height(232.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .background(
                    Color(
                        red = 0.9725490212440491f,
                        green = 0.9725490212440491f,
                        blue = 1f,
                        alpha = 1f
                    )
                )
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val uploadImg: Painter = painterResource(id = R.drawable.upload)
                Image(
                    painter = uploadImg,
                    contentDescription = ""
                )
                TextButton(onClick = { /*TODO */}) {
                    Text(text = "Add Image",
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center)
                }
                Text(text = "Supported formats: JPEG, PNG, GIF, PDF",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Box(modifier = Modifier.padding(top = 20.dp))

        var descriptionText = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = descriptionText.value,
            onValueChange = { descriptionText.value = it },
            label = { Text("Add Food Description") },
            modifier = Modifier
                .width(316.dp)
                .height(120.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .background(
                    Color(
                        red = 0.9725490212440491f,
                        green = 0.9725490212440491f,
                        blue = 1f,
                        alpha = 1f
                    )
                )
        )

        TextButton(onClick = { /* Do something! */ }) {
            Text("Import Saved Food Description", color = Color.Blue)
        }

        OutlinedButton(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.textButtonColors(backgroundColor = Color.Blue),
            modifier = Modifier
                .width(298.dp)
                .height(48.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 9.dp,
                        topEnd = 9.dp,
                        bottomStart = 9.dp,
                        bottomEnd = 9.dp
                    )
                )
                .background(Color(0x2A3B92))
        ) {
            Text("Add Food", color = Color.White)
        }
    }
}