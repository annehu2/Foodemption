package com.example.foodemption

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent.getIntent
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.foodemption.home.Title
import com.example.foodemption.ui.theme.FoodemptionTheme
import com.example.foodemption.utils.SharedPreferenceHelper
import java.util.*


class SchedulePickUpActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodemptionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val photoUri = intent.getStringExtra("photoUri").toString()
                    val title = intent.getStringExtra("title").toString()
                    val description = intent.getStringExtra("description").toString()
                    val bestBefore = intent.getStringExtra("bestBefore").toString()
                    SchedulePickUp(this, photoUri, title, description, bestBefore)
                }
            }
        }
    }
}


@Composable
fun SchedulePickUp(context: Context, photoUri: String, title: String, description:String, bestBefore: String) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(40.dp))
        Title("Schedule Pick Up", SharedPreferenceHelper.getOrgName(context))
        Spacer(modifier = Modifier.size(20.dp))

        Box(
            modifier = Modifier
                .width(293.dp)
                .height(232.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberImagePainter(photoUri),
                    contentDescription = null,
                )
            }
        }

        Spacer(modifier = Modifier.size(20.dp))

        var titleText = title
        Text(titleText, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.size(20.dp))

        var descriptionText = description
        Text(descriptionText, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.size(20.dp))

        var bestBefore = "Best Before: $bestBefore"
        Text(bestBefore, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.size(20.dp))
        // Calendar code https://www.geeksforgeeks.org/date-picker-in-android-using-jetpack-compose/
        val mContext = LocalContext.current
        val mYear: Int
        val mMonth: Int
        val mDay: Int
        val mHour: Int
        val mMinute: Int
        val mCalendar = Calendar.getInstance()

        // Fetching current year, month and day
        mYear = mCalendar.get(Calendar.YEAR)
        mMonth = mCalendar.get(Calendar.MONTH)
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH)
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY)
        mMinute = mCalendar.get(Calendar.MINUTE)

        mCalendar.time = Date()
        val pickUpDate = remember { mutableStateOf("") }
        val pickUpTime = remember { mutableStateOf("") }

        val mDatePickerDialog = DatePickerDialog(
            mContext,
            { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                pickUpDate.value = "$mDayOfMonth/${mMonth + 1}/$mYear"
            }, mYear, mMonth, mDay
        )

        val mTimePickerDialog = TimePickerDialog(
            context,
            {_, hour : Int, minute: Int ->
                if (minute < 10) {
                    pickUpTime.value = "$hour:0$minute"
                }
                else {
                    pickUpTime.value = "$hour:$minute"
                }
            }, mHour, mMinute, false
        )

        Button(
            modifier = Modifier
                .width(316.dp)
                .height(50.dp),
            onClick = {
                mDatePickerDialog.show()
            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFe0e0e0))
        ) {
            Text(
                text = "Pick Up Date: ${pickUpDate.value}",
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xff757575),
                textAlign = TextAlign.Left,
            )
        }
        
        Spacer(modifier = Modifier.size(20.dp))

        Button(
            modifier = Modifier
                .width(316.dp)
                .height(50.dp),
            onClick = {
                mTimePickerDialog.show()
            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFe0e0e0))
        ) {
            Text(
                text = "Pick Up Time: ${pickUpTime.value}",
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xff757575),
                textAlign = TextAlign.Left,
            )
        }

        Spacer(modifier = Modifier.size(20.dp))

        OutlinedButton(
            onClick = {
                /*TODO*/
            },
            colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xFF2A3B92)),
            modifier = Modifier
                .width(298.dp)
                .height(52.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 9.dp,
                        topEnd = 9.dp,
                        bottomStart = 9.dp,
                        bottomEnd = 9.dp
                    )
                )
        ) {
            Text("Schedule Pick Up",
                color = Color.White,
                fontSize = 20.sp)
        }
    }
}


