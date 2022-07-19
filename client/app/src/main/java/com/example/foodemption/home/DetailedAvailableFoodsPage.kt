package com.example.foodemption.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodemption.*
import com.example.foodemption.ui.theme.FoodemptionTheme
import kotlin.concurrent.thread

class DetailedAvailableFoodsPage : ComponentActivity() {
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
                    DetailedAvailableFoodsPage(this)
                }
            }
        }
    }
}

@Composable
fun DetailedAvailableFoodsPage(context: Context) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        val donations =
            remember { mutableStateOf(emptyList<FoodemptionApiClient.DonationsBodyData>()) }

        LaunchedEffect(Unit) {
            thread {
                donations.value = FoodemptionApiClient.getAllAvailableFood(context)
            }
        }
        val donationsLen = donations.value.size
        for (i in (donationsLen - 1) downTo 0) {
            Spacer(Modifier.size(40.dp))
            DetailedListing(
                context,
                donations.value[i].image_url,
                donations.value[i].title,
                donations.value[i].best_before,
                donations.value[i].description
            )
        }
        Spacer(Modifier.size(60.dp))
    }
}