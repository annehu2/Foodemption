package com.example.foodemption

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.foodemption.home.DonorHome
import com.example.foodemption.home.HomeListings
import com.example.foodemption.home.Title
import com.example.foodemption.ui.theme.FoodemptionTheme

class VerificationFormActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodemptionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    VerificationForm(this)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VerificationForm(context: Context) {
//    var scrollableState: ScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
//            .verticalScroll(scrollableState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val image: Painter = painterResource(id = R.drawable.logo)
        Image(
            painter = image,
            contentDescription = "",
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp)
        )
        Text(
            "Organization Verification",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            "Please enter your details for verification.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Box(modifier = Modifier.padding(top = 20.dp))

        val nameText = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = nameText.value,
            onValueChange = { nameText.value = it },
            label = { Text("Organization name") }
        )

        Box(modifier = Modifier.padding(top = 20.dp))

        val phoneText = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = phoneText.value,
            onValueChange = { phoneText.value = it },
            label = { Text("Organization phone number") }
        )

        Box(modifier = Modifier.padding(top = 20.dp))

        val addressText = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = addressText.value,
            onValueChange = { addressText.value = it },
            label = { Text("Organization address") }
        )

        Box(modifier = Modifier.padding(top = 20.dp))

        // add drop down menu here to choose time

        Box(modifier = Modifier.padding(top = 20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            val checkedState = remember { mutableStateOf(false) }
            Checkbox(
                checked = checkedState.value,
                onCheckedChange = { checkedState.value = it }
            )
            
            Text(text = "I agree all the information provided is true.")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            val checkedState = remember { mutableStateOf(false) }
            Checkbox(
                checked = checkedState.value,
                onCheckedChange = { checkedState.value = it }
            )

            Text(text = "I agree to all the terms and conditions.")
        }

        Row() {
            OutlinedButton(
                onClick = { val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent) },
                colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xFF2A3B92)),
                modifier = Modifier
                    .width(150.dp)
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
                Text("Verify", color = Color.White, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.padding(20.dp))

            OutlinedButton(
                onClick = { val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent) },
                colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xFF2A3B92)),
                modifier = Modifier
                    .width(150.dp)
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
                Text("Cancel", color = Color.White, fontSize = 20.sp)
            }
        }

    }
}