package com.example.foodemption.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.rememberAsyncImagePainter
import com.example.foodemption.SchedulePickUpActivity


@Composable
fun DetailedListing(context: Context, photoUrl: String, title: String, bestBefore: String, description: String, pageCode: Int) {
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
        Row(Modifier.fillMaxWidth()) {
            Image(
                painter = rememberAsyncImagePainter(photoUrl),
                contentDescription = "",
                alignment = Alignment.TopStart,
            )
            Column(Modifier.padding(start = 20.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .width(150.dp)
                )
                Spacer(Modifier.size(5.dp))
                Text(
                    text = "Best Before: $bestBefore",
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .width(150.dp)
                )
                Spacer(Modifier.size(5.dp))
                Text(
                    text = description,
                    fontSize = 10.sp,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .width(150.dp)
                )
                Spacer(Modifier.size(5.dp))
                if (pageCode != 0) {
                    var buttonText = ""
                    var buttonColor = 0xFF2A3B92
                    var intent: Intent
                    if (pageCode == 1) {
                        buttonText = "Schedule Pick Up"
                        buttonColor = 0xFF00a79c
                        intent = Intent(context, SchedulePickUpActivity::class.java)
                        intent.putExtra("bestBefore", bestBefore)
                        intent.putExtra("description", description)
                        intent.putExtra("photoUri", photoUrl)
                        intent.putExtra("title", title)
                    }
                    else {
                        buttonText = "Confirm Pick Up"
                        buttonColor = 0xFF2A3B92
                        intent = Intent(context, ConfirmPickUpActivity::class.java)
                        intent.putExtra("bestBefore", bestBefore)
                        intent.putExtra("description", description)
                        intent.putExtra("photoUri", photoUrl)
                        intent.putExtra("title", title)
                        // TODO: put other intents here from api call
                    }
                    OutlinedButton(
                        onClick = {
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.textButtonColors(backgroundColor = Color(buttonColor)),
                        modifier = Modifier
                            .width(200.dp)
                            .height(40.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 9.dp,
                                    topEnd = 9.dp,
                                    bottomStart = 9.dp,
                                    bottomEnd = 9.dp
                                )
                            )
                    ) {
                        Text(buttonText, color = Color.White)
                    }
                }
            }
        }
    }


}