package com.example.cashmanagerapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import com.example.cashmanagerapplication.preferences.PreferenceManager


class SplashActivity : BaseActivity() {

    private val pref: PreferenceManager by lazy { PreferenceManager (this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            if (pref.getInt("pref_is_login") == 0) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)

    }
}