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
import com.example.foodemption.VerificationActivity
import com.example.foodemption.maps.MapsConsumerActivity
import com.example.foodemption.showMessage
import com.example.foodemption.ui.theme.FoodemptionTheme
import com.example.foodemption.utils.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

class DetailedActiveFoodListingsPage : ComponentActivity() {
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
                    DetailedActiveFoodListingsPage(this)
                }
            }
        }
    }
}

@Composable
fun DetailedActiveFoodListingsPage(context: Context) {

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
                    "Active Listings",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
        Box(Modifier.padding(), Alignment.CenterStart) {
            OutlinedButton(
                onClick = { val intent = Intent(context, DonorHome::class.java)
                    context.startActivity(intent) },
                colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xFFFFFFFF)),
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp)
            ) {
                Text("Back", color = Color.Blue, fontSize = 15.sp,)
            }
        }

        val donations =
            remember { mutableStateOf(emptyList<FoodemptionApiClient.DonationsBodyData>()) }

        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val result =
                    try {
                        FoodemptionApiClient.getAllDonations(context)
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
            if (!donations.value[i].is_claimed) {
                Spacer(Modifier.size(40.dp))
                DetailedListing(
                    context,
                    donations.value[i].image_url,
                    donations.value[i].title,
                    donations.value[i].best_before,
                    donations.value[i].description,
                    donations.value[i].uuid,
                    0
                )
            }
        }
        Spacer(Modifier.size(60.dp))
    }
}