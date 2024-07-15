package com.github.mutoxu_n.splitapp.models

import java.time.LocalDateTime

data class Receipt(
    val id: String = "null",
    val stuff: String,
    val paid: Member,
    val buyers: List<Member>,
    val payment: Int,
    val reportedBy: Member,
    var timestamp: LocalDateTime,
)
