package com.example.foodemption.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodemption.DonateActivity
import com.example.foodemption.FoodemptionApiClient
import com.example.foodemption.R
import com.example.foodemption.showMessage
import com.example.foodemption.ui.theme.FoodemptionTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

class DetailedClaimedFoodListingsPage : ComponentActivity() {
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
                    DetailedClaimedFoodListingsPage(this)
                }
            }
        }
    }
}

@Composable
fun DetailedClaimedFoodListingsPage(context: Context) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        val image: Painter = painterResource(id = R.drawable.logo)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = image,
                contentDescription = "",
                modifier = Modifier
                    .padding(start = 40.dp, end = 10.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Claimed Foods",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

        val donations =
            remember { mutableStateOf(emptyList<FoodemptionApiClient.DonationsBodyData>()) }

        /*
        LaunchedEffect(Unit) {
            thread {
                donations.value = FoodemptionApiClient.getClaimedFoodOld(context)
            }
        }*/

        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val result =
                    try {
                        FoodemptionApiClient.getClaimedFood(context)
                    }
                    catch(e: Exception) {
                        Log.d("ERROR", e.message.toString())
                        FoodemptionApiClient.Result.Error(Exception("Could not connect to server."))
                    }
                when (result) {
                    is FoodemptionApiClient.Result.Success<List<FoodemptionApiClient.DonationsBodyData>> -> {
                        Log.d("INFO", "HERE")
                        donations.value = result.data
                    }
                    is FoodemptionApiClient.Result.Error -> {
                        val errorMessage = result.exception.message.toString()
                        withContext(Dispatchers.Main) {
                            showMessage(context, errorMessage)
                        }
                    }
                }
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
                donations.value[i].description,
                0
            )
        }
        Spacer(Modifier.size(60.dp))
    }
}