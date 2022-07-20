package com.example.foodemption.home

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.foodemption.R
import com.example.foodemption.ui.theme.FoodemptionTheme
import com.example.foodemption.utils.SharedPreferenceHelper
import java.util.*

class ConfirmPickUpActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodemptionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val photoUri = intent.getStringExtra("photoUri").toString()
                    val title = intent.getStringExtra("title").toString()
                    val description = intent.getStringExtra("description").toString()
                    val bestBefore = intent.getStringExtra("bestBefore").toString()
                    ConfirmPickUp(this, photoUri, title, description, bestBefore)
                }
            }
        }
    }
}

@Composable
fun ConfirmPickUp(context: Context, photoUri: String, title: String, description:String, bestBefore: String) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(40.dp))
        Title("Confirm Pick Up", SharedPreferenceHelper.getOrgName(context))
        Spacer(modifier = Modifier.size(20.dp))

        Box(
            modifier = Modifier
                .width(293.dp)
                .height(232.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberImagePainter(photoUri),
                    contentDescription = null,
                )
            }
        }

        Spacer(modifier = Modifier.size(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp),
            horizontalAlignment = Alignment.Start
        ) {

            var titleText = title
            Text(titleText, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.size(20.dp))

            var descriptionText = "Description: $description"
            Text(descriptionText, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.size(20.dp))

            var bestBefore = "Best Before: $bestBefore"
            Text(bestBefore, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.size(20.dp))

            var pickUpDate = "Proposed Pick Up Time"
            Text(pickUpDate, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.size(20.dp))

            var pickUpOrgName = "Proposed Pick Up Org"
            Text(pickUpOrgName, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.size(20.dp))

            OutlinedButton(
                onClick = {
                    /*TODO*/
                },
                colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xFF2A3B92)),
                modifier = Modifier
                    .width(298.dp)
                    .height(52.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 9.dp,
                            topEnd = 9.dp,
                            bottomStart = 9.dp,
                            bottomEnd = 9.dp
                        )
                    )
            ) {
                Text(
                    "Confirm Pick Up",
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
        }
    }
}