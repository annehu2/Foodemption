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
import com.example.foodemption.showMessage
import com.example.foodemption.ui.theme.FoodemptionTheme
import com.example.foodemption.utils.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

class DetailedPendingFoodListingsPage : ComponentActivity() {
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
                    DetailedPendingFoodListingsPage(this)
                }
            }
        }
    }
}

@Composable
fun DetailedPendingFoodListingsPage(context: Context) {

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
                    "Pending Requests",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

        val requests =
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
                        requests.value = result.data
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

        val donationsLen = requests.value.size
        for (i in (donationsLen - 1) downTo 0) {
            if (!requests.value[i].is_claimed) {
                Spacer(Modifier.size(40.dp))
                DetailedRequest(
                    context,
                    "Fake PickUp Time",
                    requests.value[i].image_url,
                    requests.value[i].title,
                    requests.value[i].best_before,
                    requests.value[i].description,
                    requests.value[i].uuid,
                    "817c5efb-b6a2-4476-83fc-314802a38c7f",
                    "org name"
                )
            }
        }
        Spacer(Modifier.size(60.dp))
    }
}