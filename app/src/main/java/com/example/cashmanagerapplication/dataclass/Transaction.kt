package com.example.cashmanagerapplication.dataclass

import com.google.firebase.Timestamp

data class Transaction (
    var id: String? = "",
    var category: String = "",
    var username: String = "",
    var type: String = "",
    var created: Timestamp? = Timestamp.now(),
    var amount: Int,
    var note: String = ""
)