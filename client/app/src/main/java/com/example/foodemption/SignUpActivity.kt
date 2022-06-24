package com.example.foodemption

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodemption.ui.theme.FoodemptionTheme

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodemptionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SignUpPage()
                }
            }
        }
    }
}

@Composable
fun SignUpPage() {
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        val image: Painter = painterResource(id = R.drawable.logo)
        Image(
            painter = image,
            contentDescription = "",
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp)
        )
        Text(
            "Sign Up",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            "Please enter your details to sign up.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Box(modifier = Modifier.padding(top = 20.dp))

        var nameText = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = nameText.value,
            onValueChange = { nameText.value = it },
            label = { Text("Enter name") }
        )

        Box(modifier = Modifier.padding(top = 20.dp))

        var emailText = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = emailText.value,
            onValueChange = { emailText.value = it },
            label = { Text("Enter email") }
        )

        Box(modifier = Modifier.padding(top = 20.dp))

        var passwordText = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = passwordText.value,
            onValueChange = { passwordText.value = it },
            label = { Text("Enter password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Box(modifier = Modifier.padding(top = 20.dp))

        var passwordConfirmText = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = passwordConfirmText.value,
            onValueChange = { passwordConfirmText.value = it },
            label = { Text("Re-enter password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Box(modifier = Modifier.padding(top = 20.dp))

        OutlinedButton(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xFF2A3B92)),
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
        ) {
            Text("Sign Up", color = Color.White, fontSize = 20.sp,)
        }
    }
}