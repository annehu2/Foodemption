package com.example.foodemption

import android.net.Uri
import android.util.Base64
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import java.io.File
import java.io.IOException


class FoodemptionApiClient {
    private val okHttpClient = OkHttpClient()
    private val jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjA1OWJiZjRiLTExNTQtNDNmMS1hMGI3LWQ0N2RiY2E0NjkyMCIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.2ldETLveiF5SdBGyWbOY-guxw5faYWqRluiubTYGxWc"
    fun processLogin(email: String, password: String) {
        val loginBody = LoginRequestBody(email, password, "NEWDEVICETOKEN")

        val payload = Json.encodeToString(loginBody)

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
            }
        })
    }

    fun donorUploadFood(title: String, description: String, uri: Uri, best_before: String) {
        val file = File(uri.path)
        val encoded = convertToBase64(file)

        val foodBody = FoodRequestBody(title, description, encoded, best_before)

        val payload = Json.encodeToString(foodBody)

        val requestBody = payload.toRequestBody()
        val request = Request.Builder()
            .method("POST", requestBody)
            .header("Content-Type", "application/json")
            .addHeader("Authorization", jwt)
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

    fun getAllDonors(): List<DonationsBodyData> {
        val request = Request.Builder()
            .header("Content-Type", "application/json")
            .addHeader("Authorization", jwt)
            .url("http://ec2-3-128-157-187.us-east-2.compute.amazonaws.com:8000/donations".toHttpUrl())
            .build()
        val response = OkHttpClient().newCall(request).execute()

        val json = response.body.string()
        val responseBody = Json.decodeFromString<DonationsBody>(json)
        return responseBody.data
    }

    private fun convertToBase64(attachment: File): String {
        return Base64.encodeToString(attachment.readBytes(), Base64.DEFAULT)
    }

    @Serializable
    data class DonationsBody(
        val status_code: Int,
        val data: List<DonationsBodyData>
    )

    @Serializable
    data class DonationsBodyData(
        val uuid: String,
        val title: String,
        val image_url: String,
        val description: String,
        val best_before: String
    )

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
}