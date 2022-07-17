package com.example.foodemption

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.foodemption.home.DonorHome
import com.example.foodemption.utils.SharedPreferenceHelper
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

fun processLogin(email: String, password: String, deviceToken: String, context: Context) {
    val loginBody = LoginRequestBody(email, password, deviceToken)

    val payload = Json.encodeToString(loginBody)

    val okHttpClient = OkHttpClient()
    val requestBody = payload.toRequestBody()
    val request = Request.Builder()
        .method("POST", requestBody)
        .header("Content-Type", "application/json")
        .url("http://ec2-3-128-157-187.us-east-2.compute.amazonaws.com:8000/login".toHttpUrl())
        .build()
    okHttpClient.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.d("Fail", "you suck")
        }

        override fun onResponse(call: Call, response: Response) {
            val json = response.body.string()
            val responseBody = Json.decodeFromString<LoginResponseBody>(json)
            if (response.code == 200)
            {
                val userJwtToken = responseBody.data.jwt
                SharedPreferenceHelper.setUserJWT(context, userJwtToken)
                context.startActivity(Intent(context, DonorHome::class.java))
            } else {

            }
        }
    })
}

fun donorUploadFood(title: String, description: String, uri: Uri, best_before: String, context: Context){
    val file = File(uri.path)
    val encoded = convertToBase64(file)

    val foodBody = FoodRequestBody(title, description, encoded, best_before)

    val payload = Json.encodeToString(foodBody)
    val okHttpClient = OkHttpClient()
    val requestBody = payload.toRequestBody()

    val jwtToken = SharedPreferenceHelper.getUserJwt(context)

    val request = Request.Builder()
        .method("POST", requestBody)
        .header("Content-Type", "application/json")
        .addHeader("Authorization", jwtToken)
        .url("http://ec2-3-128-157-187.us-east-2.compute.amazonaws.com:8000/donate".toHttpUrl())
        .build()
    okHttpClient.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.i("Failure", "fail")
        }

        override fun onResponse(call: Call, response: Response) {
            Log.i("Success", "Success")
        }
    })
}

fun convertToBase64(attachment: File): String {
    return Base64.encodeToString(attachment.readBytes(), Base64.DEFAULT)
}

@Serializable
data class LoginRequestBody(
    val email: String,
    val password: String,
    val device_token: String,
)

@Serializable
data class LoginResponseBody(
    val status_code: Int,
    val data: JwtData,
)

@Serializable
data class JwtData(
    val jwt: String,
)

@Serializable
data class FoodRequestBody(
    val title: String,
    val description: String,
    val image_base64: String,
    val best_before: String,
)


