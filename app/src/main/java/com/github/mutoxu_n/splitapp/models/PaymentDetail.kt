package com.github.mutoxu_n.splitapp.models

data class PaymentDetail(
    val from: Member,
    val to: Member,
    val amount: Int,
    val total: Int,
)