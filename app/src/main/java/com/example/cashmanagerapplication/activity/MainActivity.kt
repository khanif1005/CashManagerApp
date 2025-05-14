package com.example.cashmanagerapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.cashmanagerapplication.activity.TransactionActivity
import com.example.cashmanagerapplication.adapter.TransactionAdapter
import com.example.cashmanagerapplication.databinding.ActivityMainBinding
import com.example.cashmanagerapplication.databinding.HomeAvatarBinding
import com.example.cashmanagerapplication.databinding.HomeDashboardBinding
import com.example.cashmanagerapplication.dataclass.Category
import com.example.cashmanagerapplication.dataclass.Transaction
import com.example.cashmanagerapplication.preferences.PreferenceManager
import com.example.cashmanagerapplication.util.PrefUtil
import com.example.cashmanagerapplication.util.amountFormat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var avatarBinding: HomeAvatarBinding
    private lateinit var dashboardBinding: HomeDashboardBinding
    private lateinit var transactionAdapter: TransactionAdapter

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val pref: PreferenceManager by lazy { PreferenceManager (this) }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setupBinding()
        setupListener()
        setupList()
    }


    override fun onStart() {
        super.onStart()
        getAvatar()
        getBalance()
        getTransaction()
    }

    private fun getBalance() {
        var totalBalance = 0
        var totalIn = 0
        var totalOut = 0
        db.collection("transaction")
            .whereEqualTo("username", pref.getString(PrefUtil.pref_username))
            .get()
            .addOnSuccessListener { result ->
                result.forEach{ doc ->
                    val amount = doc.data["amount"].toString().toInt()
                    when (doc.data["type"].toString()) {
                        "IN" -> totalIn += amount
                        "OUT" -> totalOut += amount
                    }
                }

                totalBalance = totalIn - totalOut

                dashboardBinding.tvTextBalance.text = amountFormat( totalBalance )
                dashboardBinding.tvTextIn.text = amountFormat( totalIn )
                dashboardBinding.tvTextOut.text = amountFormat( totalOut )
            }
    }

    private fun getTransaction() {
        val transactions: ArrayList<Transaction> = arrayListOf()

        binding.progressTransaction.visibility = View.VISIBLE
        db.collection("transaction")
            .orderBy("created", Query.Direction.DESCENDING)
            .whereEqualTo("username", pref.getString(PrefUtil.pref_username))
            .limit(5)
            .get()
            .addOnSuccessListener { result ->
                binding.progressTransaction.visibility = View.GONE
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
    }

    private fun getAvatar() {
        avatarBinding.textAvatar.text = pref.getString(PrefUtil.pref_name)
        avatarBinding.imgAvatar.setImageResource( pref.getInt(PrefUtil.pref_avatar)!! )
    }


    private fun setupBinding() {
        setContentView(binding.root)
        avatarBinding = binding.includeAvatar
        dashboardBinding = binding.includeDashboard
    }

    private fun setupList() {
        transactionAdapter = TransactionAdapter(arrayListOf(), object : TransactionAdapter.AdapterListener{
            override fun onClick(transaction: Transaction) {
                startActivity(
                    Intent(this@MainActivity, UpdateActivity::class.java)
                        .putExtra("id", transaction.id)
                )
            }
            override fun onLongClick(transaction: Transaction) : Boolean {
                val alertDialog = AlertDialog.Builder(this@MainActivity)
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
                return true
            }
        })
        binding.listTransaction.adapter = transactionAdapter
    }

    private fun deleteTransaction(id: String) {
        db.collection("transaction")
            .document(id)
            .delete()
            .addOnSuccessListener {
                getTransaction()
                getBalance()
            }
    }

    private fun setupListener() {

        binding.textTransaction.setOnClickListener{
            startActivity(Intent(this, TransactionActivity::class.java))
        }

        binding.includeAvatar.imgAvatar.setOnClickListener{
            startActivity(
                Intent(this, ProfileActivity::class.java)
                    .putExtra("balance", dashboardBinding.tvTextBalance.text.toString())
            )
        }

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, CreateActivity::class.java))
        }
    }

}