package com.example.foodemption.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.foodemption.FoodemptionApiClient
import com.example.foodemption.R
import com.example.foodemption.openAlertPickupBox
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
                    val pickUpTime = intent.getStringExtra("pickUpTime").toString()
                    val customer_uuid = intent.getStringExtra("customer_uuid").toString()
                    val food_uuid = intent.getStringExtra("food_uuid").toString()
                    val orgName = intent.getStringExtra("orgName").toString()
                    ConfirmPickUp(this, photoUri, title, description, bestBefore, pickUpTime, customer_uuid, food_uuid, orgName)
                }
            }
        }
    }
}

@Composable
fun ConfirmPickUp(context: Context, photoUri: String, title: String, description:String, bestBefore: String, pickUpTime: String, customer_uuid:String, food_uuid:String, orgName:String) {

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

            var pickUpDate = "Pick Up Time: $pickUpTime"
            Text(pickUpDate, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.size(20.dp))

            var pickUpOrgName = "Pick Up By: $orgName"
            Text(pickUpOrgName, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.size(20.dp))

            val openDialog = remember { mutableStateOf(false) }

            OutlinedButton(
                onClick = {
                    openDialog.value = true
                    FoodemptionApiClient.donorAcceptFood(
                        customer_uuid,
                        food_uuid,
                        context
                    )
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
            openAlertConfirm(
                context,
                openDialog,
                "Success!",
                "Pick up is Confirmed! $orgName will be on their way soon!"
            )
        }
    }
}

@Composable
fun openAlertConfirm(
    context: Context,
    openDialog: MutableState<Boolean>,
    title: String,
    text: String
): MutableState<Boolean> {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                val intent = Intent(context, DonorHome::class.java)
                context.startActivity(intent)
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
                        val intent = Intent(context, DonorHome::class.java)
                        context.startActivity(intent)
                    }) {
                    Text("Ok")
                }
            }
        )
    }
    return openDialog
}