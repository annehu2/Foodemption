package com.example.foodemption.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.example.foodemption.ui.theme.FoodemptionTheme
import com.example.foodemption.R

@Composable
fun HomeListings(subTitle: String) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            for (i in 1..3) {
                Box(
                    modifier = Modifier
                        .width(112.dp)
                        .height(115.dp)
                        .padding(start = 0.dp, top = 40.dp, end = 0.dp, bottom = 0.dp)

                ) {
                    val image: Painter = painterResource(id = R.drawable.food_placeholder)
                    Image(
                        painter = image,
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview4() {
    FoodemptionTheme {
        HomeListings("My Food Listings")
    }
}