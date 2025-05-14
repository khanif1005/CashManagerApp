package com.example.cashmanagerapplication.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cashmanagerapplication.BaseActivity
import com.example.cashmanagerapplication.R
import com.example.cashmanagerapplication.UpdateActivity
import com.example.cashmanagerapplication.adapter.TransactionAdapter
import com.example.cashmanagerapplication.databinding.ActivityTransactionBinding
import com.example.cashmanagerapplication.dataclass.Transaction
import com.example.cashmanagerapplication.fragment.DateFragment
import com.example.cashmanagerapplication.preferences.PreferenceManager
import com.example.cashmanagerapplication.util.PrefUtil
import com.example.cashmanagerapplication.util.stringToTimestamp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class TransactionActivity : BaseActivity() {

    private lateinit var binding: ActivityTransactionBinding
    private lateinit var transactionAdapter: TransactionAdapter
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val pref: PreferenceManager by lazy { PreferenceManager (this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupList()
        setupListener()

    }

    override fun onStart() {
        super.onStart()
        getTransaction()
    }

    private fun setupList() {
        transactionAdapter = TransactionAdapter(arrayListOf(), object : TransactionAdapter.AdapterListener{
            override fun onClick(transaction: Transaction) {
                startActivity(
                    Intent(this@TransactionActivity, UpdateActivity::class.java)
                        .putExtra("id", transaction.id)
                )
            }
            override fun onLongClick(transaction: Transaction) {
                val alertDialog = AlertDialog.Builder(this@TransactionActivity)
                alertDialog.apply {
                    setTitle("Hapus")
                    setMessage("Hapus ${transaction.note} dari histori transaksi")
                    setNegativeButton("Batal"){ dialogInterface, _->
                        dialogInterface.dismiss()
                    }
                    setPositiveButton("hapus") { dialogInterface, _->
                        deleteTransaction (transaction.id!!)
                        dialogInterface.dismiss()
                    }
                }
                alertDialog.show()
            }
        })
        binding.listTransaction.adapter = transactionAdapter
    }

    private fun setupListener() {
        binding.swipe.setOnRefreshListener {
            binding.tvTransaction.text = "menampilkan 50 transaksi terakhir"
            getTransaction()
        }
        binding.imgDate.setOnClickListener{
            DateFragment(object : DateFragment.DateListener{
                override fun onSuccess(dateStart: String, dateEnd: String) {
                    Log.e("TransactionActivity", "$dateStart $dateEnd")
                    binding.tvTransaction.text = "$dateStart - $dateEnd"
                    db.collection("transaction")
                        .orderBy("created", Query.Direction.DESCENDING)
                        .whereEqualTo("username", pref.getString(PrefUtil.pref_username))
                        .whereGreaterThanOrEqualTo("created", stringToTimestamp("$dateStart 00:00")!!)
                        .whereLessThanOrEqualTo("created", stringToTimestamp("$dateEnd 23:59")!!)
                        .get()
                        .addOnSuccessListener { result ->
                            binding.swipe.isRefreshing = false
                            setTransaction(result)
                        }

                }
            }).apply {
                show(supportFragmentManager, "dateFragment")
            }
        }
    }

    private fun getTransaction() {
        binding.swipe.isRefreshing = true
        db.collection("transaction")
            .orderBy("created", Query.Direction.DESCENDING)
            .whereEqualTo("username", pref.getString(PrefUtil.pref_username))
            .limit(50)
            .get()
            .addOnSuccessListener { result ->
                binding.swipe.isRefreshing = false
                setTransaction(result)
            }
    }

    private fun setTransaction(result: QuerySnapshot) {
        val transactions: ArrayList<Transaction> = arrayListOf()

        result.forEach{ doc ->
            transactions.add(
                Transaction(
                    id = doc.reference.id,
                    username = doc.data["username"].toString(),
                    category = doc.data["category"].toString(),
                    type = doc.data["type"].toString(),
                    amount = doc.data["amount"].toString().toInt(),
                    note = doc.data["note"].toString(),
                    created = doc.data["created"] as Timestamp
                )
            )
        }
        transactionAdapter.setData( transactions )
    }

    private fun deleteTransaction(id: String) {
        db.collection("transaction")
            .document(id)
            .delete()
            .addOnSuccessListener { getTransaction() }
    }
}