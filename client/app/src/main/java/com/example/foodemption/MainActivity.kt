package com.example.foodemption

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodemption.ui.theme.FoodemptionTheme
import com.example.foodemption.utils.SharedPreferenceHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                // Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            SharedPreferenceHelper.setFCMToken(this, token)

        });

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
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        val image: Painter = painterResource(id = R.drawable.logo_title)
        Image(
            painter = image,
            contentDescription = "",
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .scale(2.0F)

        )
        Box(modifier = Modifier.padding(top = 100.dp))
        OutlinedButton(
            onClick = {
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent) },
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
            Text("Login", color = Color.White, fontSize = 22.sp)
        }
        Box(modifier = Modifier.padding(top = 20.dp))
        OutlinedButton(
            onClick = { val intent = Intent(context, SignUpActivity::class.java)
                context.startActivity(intent) },
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
            Text("Sign Up", color = Color.White, fontSize = 22.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FoodemptionTheme {
    }
}