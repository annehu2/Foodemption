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
import com.example.foodemption.maps.MapsConsumerActivity
import com.example.foodemption.maps.SplashScreenActivity
import com.example.foodemption.ui.theme.FoodemptionTheme
import com.example.foodemption.utils.SharedPreferenceHelper

class ConsumerHome : ComponentActivity() {
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
                    ConsumerHome(this)
                }
            }
        }
    }
}

@Composable
fun ConsumerHome(context: Context) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
    ) {
        val orgName = SharedPreferenceHelper.getOrgName(context)
        Title("Welcome", orgName)
        Spacer(Modifier.size(40.dp))
        HomeListingsAvailableFood(context, subTitle = "Available Food")
        Spacer(Modifier.size(40.dp))
        //HomeListings(context, subTitle = "Previously Claimed Food", 4)
        Spacer(Modifier.size(40.dp))
        //HomeListings(context, subTitle = "Closest Organizations", 4)
        Box(Modifier.padding(), Alignment.BottomCenter) {
            OutlinedButton(
                onClick = { val intent = Intent(context, MapsConsumerActivity::class.java)
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
        Spacer(Modifier.size(60.dp))
    }
}