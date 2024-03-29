package com.example.foodemption

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodemption.home.ConsumerHome
import com.example.foodemption.home.DonorHome
import com.example.foodemption.ui.theme.FoodemptionTheme
import com.example.foodemption.utils.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

        val emailText = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = emailText.value,
            onValueChange = { emailText.value = it},
            label = { Text("Enter email") },
        )

        Box(modifier = Modifier.padding(top = 20.dp))

        val passwordText = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = passwordText.value,
            onValueChange = { passwordText.value = it },
            label = { Text("Enter password") },
            visualTransformation = PasswordVisualTransformation(),
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

        val openDialog = remember { mutableStateOf(false) }
        val dialogMessage = remember { mutableStateOf("") }

        val coroutineScope = rememberCoroutineScope()
        OutlinedButton(
            onClick = {
                coroutineScope.launch {
                    val deviceToken = SharedPreferenceHelper.getFCMToken(context)
                    val result =
                    try {
                        FoodemptionApiClient.processLogin(
                            emailText.value.text.trim(),
                            passwordText.value.text.trim(),
                            deviceToken
                        )
                    }
                    catch(e: Exception) {
                        Log.d("ERROR", e.message.toString())
                        FoodemptionApiClient.Result.Error(Exception("Could not login."))
                    }
                    when (result) {
                        is FoodemptionApiClient.Result.Success<FoodemptionApiClient.LoginResponseBody> -> {
                            val userJwtToken = result.data.data.jwt
                            val userOrgName = result.data.data.org
                            val userType = result.data.data.user_type
                            SharedPreferenceHelper.setUserJWT(context, userJwtToken)
                            SharedPreferenceHelper.setOrgName(context, userOrgName)
                            SharedPreferenceHelper.setUserType(context, userType.toString())
                            if (result.data.data.user_type == 1) {
                                context.startActivity(Intent(context, ConsumerHome::class.java))
                            } else {
                                context.startActivity(Intent(context, DonorHome::class.java))
                            }
                        }
                        is FoodemptionApiClient.Result.Error -> {
//                            openDialog.value = true
                            dialogMessage.value = result.exception.message.toString()
                            withContext(Dispatchers.Main) {
                                showMessage(context, dialogMessage.value)
                            }
                        }
                    }
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
//        openAlertLoginFailBox(openDialog, "Login Fail", dialogMessage.value)
    }

}

fun showMessage(context: Context, message:String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun openAlertLoginFailBox(
    openDialog: MutableState<Boolean>,
    title: String,
    text: String
): MutableState<Boolean> {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = title)
            },
            text = {
                Text(text = text)
            },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                    }) {
                    Text("Ok")
                }
            }
        )
    }
    return openDialog
}
