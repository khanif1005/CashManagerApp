package com.example.cashmanagerapplication.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val PREF_NAME = "moneyKhanif.pref"
    private val sharedPref: SharedPreferences
    val editor: SharedPreferences.Editor

    init {
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPref.edit()
    }

    fun put (key: String, value: String) {
        editor.putString(key, value)
            .apply()
    }

    fun put (key: String, value: Int) {
        editor.putInt(key, value)
            .apply()
    }

    fun getString (key: String): String? {
        return sharedPref.getString(key, "")
    }

    fun getInt (key: String): Int? {
        return sharedPref.getInt(key, 0)
    }

    fun clear(){
        editor.putInt("pref_is_login", 0)
            .apply()
    }
}