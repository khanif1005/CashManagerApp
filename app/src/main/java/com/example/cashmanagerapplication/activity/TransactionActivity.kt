package com.example.cashmanagerapplication.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cashmanagerapplication.BaseActivity
import com.example.cashmanagerapplication.R
import com.example.cashmanagerapplication.databinding.ActivityTransactionBinding
import com.example.cashmanagerapplication.fragment.DateFragment

class TransactionActivity : BaseActivity() {

    private lateinit var binding: ActivityTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListener()

    }

    private fun setupListener() {
        binding.imgDate.setOnClickListener{
            DateFragment(object : DateFragment.DateListener{
                override fun onSuccess(dateStart: String, dateEnd: String) {
                    Log.e("TransactionActivity", "$dateStart $dateEnd")

                }
            }).apply {
                show(supportFragmentManager, "dateFragment")
            }
        }
    }
}