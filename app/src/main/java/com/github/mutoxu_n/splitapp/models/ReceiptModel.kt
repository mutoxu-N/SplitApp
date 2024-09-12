package com.github.mutoxu_n.splitapp.models

import android.util.Log
import com.github.mutoxu_n.splitapp.App
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

        @Suppress("UNCHECKED_CAST")
        @Throws(ClassCastException::class)
        fun fromMap(map: Map<String, Any>): ReceiptModel{
            return ReceiptModel(
                id = map[FIELD_ID] as String,
                stuff = map[FIELD_STUFF] as String,
                paid = map[FIELD_PAID] as String,
                buyers = map[FIELD_BUYERS] as List<String>,
                payment = map[FIELD_PAYMENT] as Int,
                reportedBy = map[FIELD_REPORTED_BY] as String,
                timestamp = map[FIELD_TIMESTAMP].toString(),
            )
        }
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
