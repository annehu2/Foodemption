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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import kotlin.concurrent.thread

@Composable
fun HomeListings(context: Context, subTitle: String, showType: Int) {
// this is super scuffed but this removes the need for copy and pasted composable code
// showType 0 - my active food listings - donor side
// showType 1 - my claimed food - donor side
// showType 3 - all available food - consumer side
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
            if (showType == 1) {
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
            } else if (showType == 0) {
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
            } else if (showType == 3) {
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
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (showType == 1) {
                val donations =
                    remember { mutableStateOf(emptyList<FoodemptionApiClient.DonationsBodyData>()) }

                LaunchedEffect(Unit) {
                    thread {
                        donations.value = FoodemptionApiClient.getAllDonations(context)
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
            else if (showType == 0) {
                val donations =
                    remember { mutableStateOf(emptyList<FoodemptionApiClient.DonationsBodyData>()) }

                LaunchedEffect(Unit) {
                    thread {
                        donations.value = FoodemptionApiClient.getAllDonations(context)
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
            // get all available food
            else if (showType == 3) {
                val donations =
                    remember { mutableStateOf(emptyList<FoodemptionApiClient.DonationsBodyData>()) }

                LaunchedEffect(Unit) {
                    thread {
                        donations.value = FoodemptionApiClient.getAllAvailableFood(context)
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
}