package com.example.foodemption

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    SignUpPage()
                }
            }
        }
    }
}

@Composable
fun LandingPage() {

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
            onClick = { /*TODO*/ },
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

@Composable
fun LoginPage() {
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        val image: Painter = painterResource(id = R.drawable.logo)
        Image(
            painter = image,
            contentDescription = "",
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp)
        )
        Text(
            "Login",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            "Please login to continue.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center
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

        Text(
            text = "Forgot Password?",
            textAlign = TextAlign.Start,
            fontSize = 13.sp,
            letterSpacing = 0.sp,

            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 52.dp)
                .width(116.dp)
                .alpha(1f),
            color = Color(red = 0f, green = 0f, blue = 0f, alpha = 0.6000000238418579f),
            fontWeight = FontWeight.Medium,
            fontStyle = FontStyle.Normal,
        )

        Box(modifier = Modifier.padding(top = 20.dp))

        OutlinedButton(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.textButtonColors(backgroundColor = Color.Blue),
            modifier = Modifier.width(200.dp)
        ) {
            Text("Login", color = Color.White)
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