package com.example.foodemption.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodemption.DonateActivity
import com.example.foodemption.SplashScreenActivity
import com.example.foodemption.ui.theme.FoodemptionTheme

class DonorHome : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodemptionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DonorHome(this, "Anne")
                }
            }
        }
    }
}

@Composable
fun DonorHome(context: Context, name: String) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
    ) {
        Title("Anne", "Cool Kids Club")
        Spacer(Modifier.size(40.dp))
        HomeListingsActive(context, subTitle = "My Active Food Listings")
        Spacer(Modifier.size(40.dp))
        HomeListingsClaimed(context, subTitle = "My Claimed Food")
        Spacer(Modifier.size(40.dp))
        Box(Modifier.padding(), Alignment.BottomCenter) {
            OutlinedButton(
                onClick = { val intent = Intent(context, SplashScreenActivity::class.java)
                    context.startActivity(intent) },
                colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xFF9A1B11)),
                modifier = Modifier
                    .width(297.dp)
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
                Text("View Nearby Food Banks", color = Color.White, fontSize = 20.sp,)
            }
        }
        Spacer(Modifier.size(30.dp))
        Box(Modifier.padding(), Alignment.BottomCenter) {
            OutlinedButton(
                onClick = { val intent = Intent(context, SplashScreenActivity::class.java)
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
                Text("View Closest Organisations", color = Color.White, fontSize = 18.sp,)
            }
        }
        Spacer(Modifier.size(20.dp))
        Box(Modifier.padding(), Alignment.BottomCenter) {
            OutlinedButton(
                onClick = { val intent = Intent(context, DonateActivity::class.java)
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
                Text("Donate Food", color = Color.White, fontSize = 20.sp,)
            }
        }
    }
}