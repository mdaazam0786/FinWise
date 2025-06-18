package com.example.finwise.data.model.transaction

data class Transaction(
    val id : Int,
    val title : String,
    val amount : Double,
    val date : Long,
    val type : TransactionType,
    val category : String
)