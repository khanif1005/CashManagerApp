package com.example.cashmanagerapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.cashmanagerapplication.databinding.ActivityLoginBinding
import com.example.cashmanagerapplication.dataclass.User
import com.example.cashmanagerapplication.preferences.PreferenceManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Context
import com.example.cashmanagerapplication.util.timestampToString

class LoginActivity: BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val pref: PreferenceManager by lazy { PreferenceManager (this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListener()

    }

    private fun setupListener() {
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            if (isRequired()) login()
            else Toast.makeText(this, "Isi data dengan lengkap",Toast.LENGTH_SHORT).show()
        }
    }

    private fun progress(progress: Boolean) {
        binding.tvAllert.visibility = View.GONE
        when (progress) {
            true -> {
                binding.btnLogin.text = "Loading.."
                binding.btnLogin.isEnabled = false
            }
            false -> {
                binding.btnLogin.text = "Login"
                binding.btnLogin.isEnabled = true
            }
        }
    }

    private fun login(){
        progress(true)
        db.collection("user")
            .whereEqualTo("username", binding.edtUsername.text.toString())
            .whereEqualTo("password", binding.edtPassword.text.toString())
            .get()
            .addOnSuccessListener { result ->
                progress(false)
                if (result.isEmpty) binding.tvAllert.visibility = View.VISIBLE
                else {
                    result.forEach{ document ->
                        saveSession(
                            User (
                                name = document.data["name"].toString(),
                                username = document.data["username"].toString(),
                                password = document.data["password"].toString(),
                                created = document.data["created"] as Timestamp
                            )
                        )
                    }
                    Toast.makeText(this, "Berhasil Login", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
    }

    private fun isRequired(): Boolean{
        return (
                        binding.edtUsername.text.toString().isNotEmpty() &&
                        binding.edtPassword.text.toString().isNotEmpty()
                )
    }

    private fun saveSession(user: User){
        Log.e("LoginActivity", user.toString())
        pref.put("pref_is_login", 1)
        pref.put("pref_name", user.name)
        pref.put("pref_username", user.username)
        pref.put("pref_password", user.password)
        pref.put("pref_date", timestampToString( user.created )!!)
        if (pref.getInt("pref_avatar") == 0) pref.put("pref_avatar", R.drawable.avatar1)

    }
}