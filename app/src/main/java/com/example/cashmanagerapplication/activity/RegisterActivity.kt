package com.example.cashmanagerapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.cashmanagerapplication.databinding.ActivityRegisterBinding
import com.example.cashmanagerapplication.dataclass.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupListener()
        binding.btnRegister.setOnClickListener{
            if (isRequired()) checkUsername()
            else Toast.makeText(this, "Isi data dengan lengkap",Toast.LENGTH_SHORT).show()

        }

    }

    private fun progress(progress: Boolean) {
        binding.tvAllert.visibility = View.GONE
        when (progress) {
            true -> {
                binding.btnRegister.text = "Loading.."
                binding.btnRegister.isEnabled = false
            }
            false -> {
                binding.btnRegister.text = "Register"
                binding.btnRegister.isEnabled = true
            }
        }
    }

    private fun checkUsername() {
        progress(true)
        db.collection("user")
            .whereEqualTo("username", binding.edtUsername.text.toString())
            .get()
            .addOnSuccessListener { result ->
                progress(false)
                if (result.isEmpty) addUser()
                else binding.tvAllert.visibility = View.VISIBLE
            }
    }

    private fun isRequired(): Boolean{
        return (
                        binding.edtName.text.toString().isNotEmpty() &&
                        binding.edtUsername.text.toString().isNotEmpty() &&
                        binding.edtPassword.text.toString().isNotEmpty()
                )
    }

    private fun addUser() {
        progress(true)
        val user = User(
            name = binding.edtName.text.toString(),
            username = binding.edtUsername.text.toString(),
            password = binding.edtPassword.text.toString(),
            created = Timestamp.now()
        )
        db.collection("user")
            .add(user)
            .addOnSuccessListener {
                progress(false)
                Toast.makeText(this, "Berhasil Mendaftar",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
            }
    }

    private fun setupListener() {
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding.btnRegister.setOnClickListener{

        }
    }
}