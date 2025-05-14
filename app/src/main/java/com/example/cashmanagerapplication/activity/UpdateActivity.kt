package com.example.cashmanagerapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cashmanagerapplication.adapter.CategoryAdapter
import com.example.cashmanagerapplication.databinding.ActivityCreateBinding
import com.example.cashmanagerapplication.dataclass.Category
import com.example.cashmanagerapplication.dataclass.Transaction
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class UpdateActivity : BaseActivity() {
    private final  val TAG: String = "UpdateActivity"

    private lateinit var binding: ActivityCreateBinding
    private lateinit var transaction: Transaction
    private lateinit var categoryAdapter: CategoryAdapter
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var type: String = "";
    private var category: String = "";
    private val  transactionId by lazy { intent.getStringExtra("id") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupList()
        setupListener()
        Log.e(TAG, "transactionId: $transactionId")
    }

    override fun onStart() {
        super.onStart()
        detailTransaction()
    }

    private fun setupList() {
        categoryAdapter = CategoryAdapter(this, arrayListOf(), object : CategoryAdapter.AdapterListener {
            override fun onClick(category: Category) {
                transaction.category = category.name!!
            }
        })
        binding.listCategory.adapter = categoryAdapter
    }

    private fun setupListener() {
        binding.btnSave.setText("Simpan Perubahan")
        binding.btnSave.setOnClickListener{
            progress(true)
            transaction.amount = binding.edtAmount.text.toString().toInt()
            transaction.note = binding.edtNote.text.toString()

            db.collection("transaction")
                .document(transactionId!!)
                .set(transaction)
                .addOnSuccessListener {
                    progress(false)
                    Toast.makeText(this, "Berhasil Merubah Transaksi", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
        binding.btnIn.setOnClickListener{
            transaction.type = "IN"
            setButton( it as MaterialButton )
        }

        binding.btnOut.setOnClickListener{
            transaction.type = "OUT"
            setButton( it as MaterialButton )
        }
    }

    private fun progress(progress: Boolean) {
        when (progress) {
            true -> {
                binding.btnSave.text = "Loading.."
                binding.btnSave.isEnabled = false
            }
            false -> {
                binding.btnSave.text = "Simpan Perubahan"
                binding.btnSave.isEnabled = true
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
                Handler(Looper.myLooper()!!).postDelayed({
                    categoryAdapter.setButton( transaction.category )
                }, 200)

            }
    }

    private fun detailTransaction() {
        db.collection("transaction")
            .document( transactionId!! )
            .get()
            .addOnSuccessListener { result ->

                transaction = Transaction(
                        id = result.id,
                        amount = result["amount"].toString().toInt(),
                        category = result["category"].toString(),
                        type = result["type"].toString(),
                        note = result["note"].toString(),
                        username = result["username"].toString(),
                        created = result["created"] as Timestamp
                )
                binding.edtAmount.setText( transaction.amount.toString() )
                binding.edtNote.setText( transaction.note.toString() )

                when (transaction.type) {
                    "IN" -> setButton( binding.btnIn )
                    "OUT" -> setButton( binding.btnOut )
                }

                getCategory()
            }
    }

}