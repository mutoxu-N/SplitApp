package com.github.mutoxu_n.splitapp.models

import com.github.mutoxu_n.splitapp.common.Store
import com.google.firebase.firestore.IgnoreExtraProperties
import com.squareup.moshi.Json
import java.time.LocalDateTime

@IgnoreExtraProperties
data class ReceiptModel(
    @Json(name = FIELD_ID) val id: String = "null",
    @Json(name = FIELD_STUFF) val stuff: String,
    @Json(name = FIELD_PAID) val paid: String,
    @Json(name = FIELD_BUYERS) val buyers: List<String>,
    @Json(name = FIELD_PAYMENT) val payment: Int,
    @Json(name = FIELD_REPORTED_BY) val reportedBy: String = "null",
    @Json(name = FIELD_TIMESTAMP)  var timestamp: String = "",
) {
    companion object {
        const val FIELD_ID = "id"
        const val FIELD_STUFF = "stuff"
        const val FIELD_PAID = "paid"
        const val FIELD_BUYERS = "buyers"
        const val FIELD_PAYMENT = "payment"
        const val FIELD_REPORTED_BY = "reported_by"
        const val FIELD_TIMESTAMP = "timestamp"

    }

    fun toReceipt(): Receipt? {
        val members = Store.members.value ?: return null
        return Receipt(
            id = id,
            stuff = stuff,
            paid = members.find { it.name == paid } ?: return null,
            buyers = members.filter { buyers.contains(it.name) },
            payment = payment,
            reportedBy = members.find { it.uid == reportedBy } ?: return null,
            timestamp = LocalDateTime.parse(timestamp),
        )
    }
}
