package com.example.foodemption

import android.net.Uri
import android.util.Base64
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

fun donorUploadFood(title: String, description: String, uri: Uri, best_before: String){
    val file = File(uri.path)
    val encoded = convertToBase64(file)

    val foodBody = FoodRequestBody(title, description, encoded, best_before)

    val payload = Json.encodeToString(foodBody)

    val okHttpClient = OkHttpClient()
    val requestBody = payload.toRequestBody()
    val request = Request.Builder()
        .method("POST", requestBody)
        .header("Content-Type", "application/json")
        .addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjUzNDViNThjLWI0ODAtNDc0Zi1iMjhkLTI3NTcxYWM3MDQyNyIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.W8FB4nbbxn6AlpJvuGLPBCAuGXHMlj1JPgZZvuuUfKA")
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
data class FoodRequestBody(
    val title: String,
    val description: String,
    val image_base64: String,
    val best_before: String,
)