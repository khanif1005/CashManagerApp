package com.example.cashmanagerapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.cashmanagerapplication.activity.TransactionActivity
import com.example.cashmanagerapplication.databinding.ActivityMainBinding
import com.example.cashmanagerapplication.databinding.HomeAvatarBinding
import com.example.cashmanagerapplication.databinding.HomeDashboardBinding
import com.example.cashmanagerapplication.dataclass.Category
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var avatarBinding: HomeAvatarBinding
    private lateinit var dashboardBinding: HomeDashboardBinding

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setupBinding()
        setupListener()
        testFirestore()

    }

    private fun testFirestore() {
        val  categories: ArrayList<Category> = arrayListOf()
        db.collection("category")
            .get()
            .addOnSuccessListener { result ->
                result.forEach { document ->
                    categories.add (Category( document.data["name"].toString()))
                }
                Log.e("MainActivity", "categories $categories" )
            }
    }

    private fun setupBinding() {
        setContentView(binding.root)
        avatarBinding = binding.includeAvatar
        dashboardBinding = binding.includeDashboard
    }

    private fun setupListener() {

        binding.textTransaction.setOnClickListener{
            startActivity(Intent(this, TransactionActivity::class.java))
        }

        binding.includeAvatar.imgAvatar.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, CreateActivity::class.java))
        }
    }

}