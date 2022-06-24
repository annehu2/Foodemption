package com.example.foodemption.home

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    DonorHome("Anne")
                }
            }
        }
    }
}

@Composable
fun DonorHome(name: String) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
    ) {
        Title("Anne", "Cool Kids Club")
        Spacer(Modifier.size(40.dp))
        HomeListings(subTitle = "My Food Listings")
        Spacer(Modifier.size(40.dp))
        HomeListings(subTitle = "My Claimed Food")
        Spacer(Modifier.size(40.dp))
        HomeListings(subTitle = "Closest Organizations")
        Spacer(Modifier.size(60.dp))
        Box(Modifier.padding(), Alignment.BottomCenter) {
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
                Text("Donate Food", color = Color.White, fontSize = 20.sp,)
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview3() {
    FoodemptionTheme {
        DonorHome("Android")
    }
}