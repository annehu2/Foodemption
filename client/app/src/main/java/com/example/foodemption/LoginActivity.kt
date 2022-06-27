package com.example.foodemption

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodemption.home.DonorHome
import com.example.foodemption.ui.theme.FoodemptionTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodemptionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LoginPage(this)
                }
            }
        }
    }
}

@Composable
fun LoginPage(context: Context) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            onClick = {
                try {
                    processLogin(emailText.value.text, passwordText.value.text)
                    val intent = Intent(context, DonorHome::class.java)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.i("fail", "wrong user/password")
                }
            },
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
            Text("Login", color = Color.White, fontSize = 20.sp)
        }
    }

}

fun processLogin(email: String, password: String) {
    val loginBody = LoginRequestBody(email, password, "NEWDEVICETOKEN")

    val payload = Json.encodeToString(loginBody)

    val okHttpClient = OkHttpClient()
    val requestBody = payload.toRequestBody()
    val request = Request.Builder()
        .method("POST", requestBody)
        .header("Content-Type", "application/json")
        .url("http://ec2-3-128-157-187.us-east-2.compute.amazonaws.com:8000/login".toHttpUrl())
        .build()
    okHttpClient.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.i("Fail", "you suck")
        }

        override fun onResponse(call: Call, response: Response) {
            val json = response.body.string()
            val responseBody = Json.decodeFromString<LoginResponseBody>(json)
        }
    })
}

@Serializable
data class LoginRequestBody(
    val email: String,
    val password: String,
    val device_token: String,
)

@Serializable
data class LoginResponseBody(
    val status_code: Int,
    val data: JwtData,
)

@Serializable
data class JwtData(
    val jwt: String,
)