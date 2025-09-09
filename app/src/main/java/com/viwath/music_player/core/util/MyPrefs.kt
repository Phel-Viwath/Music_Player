package com.viwath.music_player.core.util

import android.content.Context
import androidx.core.content.edit

class MyPrefs(context: Context){
    private val sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

    fun saveString(key: String, value: String){
        sharedPreferences.edit { putString(key, value) }
    }

    fun getString(key: String, defaultValue: String): String{
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

}