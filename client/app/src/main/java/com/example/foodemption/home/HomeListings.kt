package com.example.foodemption.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.foodemption.*
import com.example.foodemption.R
import com.example.foodemption.ui.theme.FoodemptionTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

@Composable
fun HomeListingsActive(context: Context, subTitle: String) {
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(119.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
            .background(Color(red = 1f, green = 1f, blue = 1f, alpha = 1f))
            .padding(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
            .alpha(1f)

    ) {
        Row() {
            Text(
                text = subTitle,
                textAlign = TextAlign.Start,
                fontSize = 14.sp,
                textDecoration = TextDecoration.None,
                letterSpacing = 0.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .alpha(1f),
                color = Color(red = 0f, green = 0f, blue = 0f, alpha = 1f),
            )
            OutlinedButton(
                onClick = {
                    val intent = Intent(context, DetailedActiveFoodListingsPage::class.java)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xffFFFFFF)),
                modifier = Modifier
                    .width(170.dp)
                    .height(30.dp)
                    .padding(start = 100.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
            ) {
                val image: Painter = painterResource(id = R.drawable.greyarrow)
                Image(
                    painter = image,
                    contentDescription = "",
                    alignment = Alignment.TopStart,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
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
                    Box(
                        modifier = Modifier
                            .width(112.dp)
                            .height(115.dp)
                            .padding(start = 0.dp, top = 40.dp, end = 0.dp, bottom = 0.dp)

                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(donations.value[i].image_url),
                            contentDescription = "",
                            alignment = Alignment.TopStart,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun HomeListingsClaimed(context: Context, subTitle: String) {
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(119.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
            .background(Color(red = 1f, green = 1f, blue = 1f, alpha = 1f))
            .padding(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
            .alpha(1f)

    ) {
        Row() {
            Text(
                text = subTitle,
                textAlign = TextAlign.Start,
                fontSize = 14.sp,
                textDecoration = TextDecoration.None,
                letterSpacing = 0.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .alpha(1f),
                color = Color(red = 0f, green = 0f, blue = 0f, alpha = 1f),
            )
            OutlinedButton(
                onClick = {
                    val intent = Intent(context, DetailedClaimedFoodListingsPage::class.java)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xffFFFFFF)),
                modifier = Modifier
                    .width(170.dp)
                    .height(30.dp)
                    .padding(start = 100.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
            ) {
                val image: Painter = painterResource(id = R.drawable.greyarrow)
                Image(
                    painter = image,
                    contentDescription = "",
                    alignment = Alignment.TopStart,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
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
                if (donations.value[i].is_claimed) {
                    Box(
                        modifier = Modifier
                            .width(112.dp)
                            .height(115.dp)
                            .padding(start = 0.dp, top = 40.dp, end = 0.dp, bottom = 0.dp)

                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(donations.value[i].image_url),
                            contentDescription = "",
                            alignment = Alignment.TopStart,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeListingsAvailableFood(context: Context, subTitle: String) {
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(119.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
            .background(Color(red = 1f, green = 1f, blue = 1f, alpha = 1f))
            .padding(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
            .alpha(1f)

    ) {
        Row() {
            Text(
                text = subTitle,
                textAlign = TextAlign.Start,
                fontSize = 14.sp,
                textDecoration = TextDecoration.None,
                letterSpacing = 0.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .alpha(1f),
                color = Color(red = 0f, green = 0f, blue = 0f, alpha = 1f),
            )
            OutlinedButton(
                onClick = {
                    val intent = Intent(context, DetailedAvailableFoodsPage::class.java)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xffFFFFFF)),
                modifier = Modifier
                    .width(170.dp)
                    .height(30.dp)
                    .padding(start = 100.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
            ) {
                val image: Painter = painterResource(id = R.drawable.greyarrow)
                Image(
                    painter = image,
                    contentDescription = "",
                    alignment = Alignment.TopStart,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val donations =
                remember { mutableStateOf(emptyList<FoodemptionApiClient.DonationsBodyData>()) }

            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    val result =
                        try {
                            FoodemptionApiClient.getAllAvailableFood(context)
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
                Box(
                    modifier = Modifier
                        .width(112.dp)
                        .height(115.dp)
                        .padding(start = 0.dp, top = 40.dp, end = 0.dp, bottom = 0.dp)

                ) {
                    Image(
                        painter = rememberAsyncImagePainter(donations.value[i].image_url),
                        contentDescription = "",
                        alignment = Alignment.TopStart,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}