package com.example.foodemption.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.foodemption.R

object SharedPreferenceHelper {

    fun setFCMToken(context: Context, fcmToken: String) {
        val sharedPreferences = getSharedPref(context)
        with(sharedPreferences.edit()) {
            putString(R.string.user_device_token.toString(), fcmToken)
            apply()
        }
    }

    fun getFCMToken(context: Context): String {
        val sharedPref = getSharedPref(context)
        return sharedPref.getString(R.string.user_device_token.toString(), "default").toString()
    }

    fun setUserJWT(context: Context, jwtToken: String){
        val sharedPreferences = getSharedPref(context)
        with(sharedPreferences.edit()) {
            putString(R.string.user_jwt_token.toString(), jwtToken)
            apply()
        }
    }

    fun getUserJwt(context: Context): String {
        val sharedPref = getSharedPref(context)
        return sharedPref.getString(R.string.user_jwt_token.toString(), "default").toString()
    }

    fun setUserType(context: Context, userType: String) {
        val sharedPreferences = getSharedPref(context)
        with(sharedPreferences.edit()) {
            putString(R.string.user_type.toString(), userType)
            apply()
        }
    }

    fun getUserType(context: Context): String {
        val sharedPref = getSharedPref(context)
        return sharedPref.getString(R.string.user_type.toString(), "default").toString()
    }

    fun setOrgName(context: Context, orgName: String) {
        val sharedPreferences = getSharedPref(context)
        with(sharedPreferences.edit()) {
            putString(R.string.user_org_name.toString(), orgName)
            apply()
        }
    }

    fun getOrgName(context: Context): String {
        val sharedPref = getSharedPref(context)
        return sharedPref.getString(R.string.user_org_name.toString(), "default").toString()
    }

    private fun getSharedPref(context: Context) : SharedPreferences {
        return context.getSharedPreferences(R.string.app_shared_pref_key.toString(),Context.MODE_PRIVATE)
    }
}
