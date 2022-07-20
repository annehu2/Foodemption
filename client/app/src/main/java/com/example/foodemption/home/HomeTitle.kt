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
fun Title(name: String, orgName: String) {
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(64.dp)
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .width(70.dp)
                .height(75.dp)
                .padding(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)

        ) {
            val image: Painter = painterResource(id = R.drawable.logo)
            Image(
                painter = image,
                contentDescription = "",
                alignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        Text(
            text = name,
            textAlign = TextAlign.Start,
            fontSize = 27.sp,
            letterSpacing = 0.sp,
            overflow = TextOverflow.Visible,
            modifier = Modifier
                .padding(start = 80.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
                .width(275.dp)
                .alpha(1f),
            color = Color(red = 0f, green = 0f, blue = 0f, alpha = 1f),
        )
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = orgName,
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                letterSpacing = 0.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 80.dp, top = 35.dp, end = 0.dp, bottom = 0.dp)
                    .alpha(1f),
                color = Color(red = 0f, green = 0f, blue = 0f, alpha = 1f),
            )
            Box(
                modifier = Modifier
                    .width(25.dp)
                    .height(57.dp)
                    .padding(start = 5.dp, top = 37.dp, end = 0.dp, bottom = 0.dp)

            ) {
                val image: Painter = painterResource(id = R.drawable.verified)
                Image(
                    painter = image,
                    contentDescription = "",
                    alignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    FoodemptionTheme {
        Title("Android", "Organization Name")
    }
}