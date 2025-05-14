package com.example.cashmanagerapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.cashmanagerapplication.adapter.CategoryAdapter
import com.example.cashmanagerapplication.adapter.TransactionAdapter
import com.example.cashmanagerapplication.databinding.ActivityCreateBinding
import com.example.cashmanagerapplication.dataclass.Category
import com.example.cashmanagerapplication.dataclass.Transaction
import com.example.cashmanagerapplication.dataclass.User
import com.example.cashmanagerapplication.preferences.PreferenceManager
import com.example.cashmanagerapplication.util.PrefUtil
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class CreateActivity : BaseActivity() {
    final val TAG: String  = "CreateActivity"

    private lateinit var binding: ActivityCreateBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var type: String = "";
    private var category: String = "";

    private val pref: PreferenceManager by lazy { PreferenceManager (this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupList()
        setupListener()
    }

    override fun onStart() {
        super.onStart()
        getCategory()
    }

    private fun setupList() {
        categoryAdapter = CategoryAdapter(this, arrayListOf(), object : CategoryAdapter.AdapterListener {
            override fun onClick(category: Category) {
                this@CreateActivity.category = category.name!!;
                Log.e(TAG, this@CreateActivity.category)
            }
        })
        binding.listCategory.adapter = categoryAdapter
    }

    private fun setupListener() {
        binding.btnIn.setOnClickListener{
            type = "IN"
            setButton( it as MaterialButton )
        }

        binding.btnOut.setOnClickListener{
            type = "OUT"
            setButton( it as MaterialButton )
        }
        binding.btnSave.setOnClickListener{
            progress(true)
            val transaction = Transaction(
                    id = null,
                    username = pref.getString( PrefUtil.pref_username )!!,
                    category = category,
                    type = type,
                    amount = binding.edtAmount.text.toString().toInt(),
                    note = binding.edtNote.text.toString(),
                    created = Timestamp.now()
            )
            db.collection("transaction")
                .add(transaction)
                .addOnSuccessListener {
                    progress(false)
                    Toast.makeText(this, "Berhasil Menambahkan Transaksi", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    private fun setButton(buttonSelected: MaterialButton) {
        Log.e(TAG, type)
        listOf<MaterialButton>(binding.btnIn, binding.btnOut).forEach{
            it.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        }
        buttonSelected.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_700))
    }

    private fun getCategory() {
        val  categories: ArrayList<Category> = arrayListOf()
        db.collection("category")
            .get()
            .addOnSuccessListener { result ->
                result.forEach { document ->
                    categories.add (Category( document.data["name"].toString()))
                }
                Log.e("MainActivity", "categories $categories" )
                categoryAdapter.setData( categories )
            }
    }

    private fun progress(progress: Boolean) {
        when (progress) {
            true -> {
                binding.btnSave.text = "Loading.."
                binding.btnSave.isEnabled = false
            }
            false -> {
                binding.btnSave.text = "Simpan"
                binding.btnSave.isEnabled = true
            }
        }
    }
}