package com.example.cashmanagerapplication

import android.os.Bundle
import com.example.cashmanagerapplication.databinding.ActivityCreateBinding

class CreateActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}