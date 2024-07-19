package com.github.mutoxu_n.splitapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Receipt(
    val id: String = "null",
    val stuff: String,
    val paid: Member,
    val buyers: List<Member>,
    val payment: Int,
    val reportedBy: Member,
    var timestamp: LocalDateTime,
): Parcelable
