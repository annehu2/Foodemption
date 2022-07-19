package com.example.foodemption

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.foodemption.utils.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.io.StringBufferInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

// Singleton design pattern
object FoodemptionApiClient {

    private const val backendUrl = "http://ec2-3-128-157-187.us-east-2.compute.amazonaws.com:8000"
    private val okHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder().addHeader("Connection", "close").build()
            chain.proceed(request)
        })
        .connectTimeout(1, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    sealed class Result<out R> {
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val exception: Exception) : Result<Nothing>()
    }

    suspend fun getAllAvailableFood(context: Context): Result<List<DonationsBodyData>> {
        return withContext(Dispatchers.IO) {
            val jwtToken = SharedPreferenceHelper.getUserJwt(context)
            val request = Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", jwtToken)
                .url("$backendUrl/claimed_food".toHttpUrl())
                .build()
            val response = okHttpClient.newCall(request).execute()
            if (response.code == 200) {
                val json = response.body.string()
                val responseBody = Json.decodeFromString<DonationsBody>(json)
                Result.Success(responseBody.data)
            }
            else {
                Result.Error(Exception("Could not retrieve donations."))
            }
        }
    }

    suspend fun getClaimedFood(context: Context): Result<List<DonationsBodyData>> {
        return withContext(Dispatchers.IO) {
            val jwtToken = SharedPreferenceHelper.getUserJwt(context)
            val request = Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", jwtToken)
                .url("$backendUrl/claimed_food".toHttpUrl())
                .build()
            val response = okHttpClient.newCall(request).execute()
            if (response.code == 200) {
                val json = response.body.string()
                val responseBody = Json.decodeFromString<DonationsBody>(json)
                Result.Success(responseBody.data)
            }
            else {
                Result.Error(Exception("Could not retrieve donations."))
            }
        }
    }

    suspend fun getAllDonations(context: Context): Result<List<DonationsBodyData>> {
        return withContext(Dispatchers.IO) {
            val jwtToken = SharedPreferenceHelper.getUserJwt(context)
            val request = Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", jwtToken)
                .url("$backendUrl/donations".toHttpUrl())
                .build()
            val response = okHttpClient.newCall(request).execute()
            if (response.code == 200) {
                val json = response.body.string()
                val responseBody = Json.decodeFromString<DonationsBody>(json)
                Result.Success(responseBody.data)
            }
            else {
                Result.Error(Exception("Error"))
            }
        }
    }

    suspend fun processLogin(email: String, password: String, deviceToken: String, context: Context): Result<LoginResponseBody> {
        return withContext(Dispatchers.IO) {
            val loginBody = LoginRequestBody(email, password, deviceToken)
            val payload = Json.encodeToString(loginBody)
            val requestBody = payload.toRequestBody()
            val request = Request.Builder()
                .method("POST", requestBody)
                .header("Content-Type", "application/json")
                .url("$backendUrl/login".toHttpUrl())
                .build()
            Log.d("INFO", "Making request.")
            val response = okHttpClient.newCall(request).execute()
            val json = response.body.string()
            Log.d("INFO", "Response received.")

            if (response.code == 200) {
                val responseBody = Json.decodeFromString<LoginResponseBody>(json)
                Log.d("INFO", "Login successful. $responseBody")
                Result.Success(responseBody)
            } else if (response.code == 400) {
                Result.Error(Exception("Incorrect username or password."))
            } else {
                Result.Error(Exception("Unknown error occurred."))
            }
        }
    }

    suspend fun processSignup(
        type: String,
        name: String,
        email: String,
        password: String,
        deviceToken: String,
        context: Context
    ): Result<SignupResponseBody> {
        return withContext(Dispatchers.IO) {
            val signupBody = SignupRequestBody(type, email, password, deviceToken, name)
            val payload = Json.encodeToString(signupBody)
            val requestBody = payload.toRequestBody()
            val request = Request.Builder()
                .method("POST", requestBody)
                .header("Content-Type", "application/json")
                .url("$backendUrl/signup".toHttpUrl())
                .build()
            Log.d("INFO", "Making request.")
            val response = okHttpClient.newCall(request).execute()
            val json = response.body.string()
            Log.d("INFO", "Response received.")
            if (response.code == 200) {
                val responseBody = Json.decodeFromString<SignupResponseBody>(json)
                Log.d("INFO", "Signup successful. $responseBody")
                Result.Success(responseBody)
            } else {
                Log.d("INFO", "Signup failed.")
                Result.Error(Exception("Signup failed."))
            }
        }
    }

    fun donorUploadFood(
        title: String,
        description: String,
        uri: Uri,
        best_before: String,
        context: Context
    ) {
        val file = File(uri.path)
        val encoded = convertToBase64(file)

        val foodBody = FoodRequestBody(title, description, encoded, best_before)

        val payload = Json.encodeToString(foodBody)
        val requestBody = payload.toRequestBody()

        val jwtToken = SharedPreferenceHelper.getUserJwt(context)

        val request = Request.Builder()
            .method("POST", requestBody)
            .header("Content-Type", "application/json")
            .addHeader("Authorization", jwtToken)
            .url("$backendUrl/donate".toHttpUrl())
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

    fun donorAcceptFood(
        customer_id: String,
        food_id: String,
        context: Context
    ) {
        val foodBody = AcceptFoodBody(customer_id, food_id)

        val payload = Json.encodeToString(foodBody)
        val requestBody = payload.toRequestBody()

        val jwtToken = SharedPreferenceHelper.getUserJwt(context)

        val request = Request.Builder()
            .method("POST", requestBody)
            .header("Content-Type", "application/json")
            .addHeader("Authorization", jwtToken)
            .url("$backendUrl/accept_claim".toHttpUrl())
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
    data class AcceptFoodBody(
        val customer_uuid: String,
        val food_uuid: String,
    )

    @Serializable
    data class LoginRequestBody(
        val email: String,
        val password: String,
        val device_token: String,
    )

    @Serializable
    data class SignupRequestBody(
        val type: String,
        val email: String,
        val password: String,
        val device_token: String,
        val name: String
    )

    @Serializable
    data class LoginResponseBody(
        val data: JwtData,
    )

    @Serializable
    data class SignupResponseBody(
        val data: JwtData,
    )


    @Serializable
    data class JwtData(
        val email: String,
        val user_type: String,
        val org: String,
        val jwt: String,
        val uuid: String,
    )

    @Serializable
    data class FoodRequestBody(
        val title: String,
        val description: String,
        val image_base64: String,
        val best_before: String,
    )

    @Serializable
    data class DonationsBody(
        val data: List<DonationsBodyData>
    )

    @Serializable
    data class DonationsBodyData(
        val uuid: String,
        val title: String,
        val image_url: String,
        val description: String,
        val best_before: String,
        val is_claimed: Boolean,
    )
}
