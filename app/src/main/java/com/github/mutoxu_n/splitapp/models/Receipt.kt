package com.github.mutoxu_n.splitapp.models

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@IgnoreExtraProperties
data class Receipt(
    val id: String,
    val stuff: String,
    val paid: String,
    val buyers: List<String>,
    val payment: Int,
    val reportedBy: String,
    @ServerTimestamp var timestamp: Date? = null,
) {
    companion object {
        const val FIELD_ID = "id"
        const val FIELD_STUFF = "stuff"
        const val FIELD_PAID = "paid"
        const val FIELD_BUYERS = "buyers"
        const val FIELD_PAYMENT = "payment"
        const val FIELD_REPORTED_BY = "reportedBy"
        const val FIELD_TIMESTAMP = "timestamp"

        @Suppress("UNCHECKED_CAST")
        @Throws(ClassCastException::class)
        fun fromMap(map: Map<String, Any>): Receipt{
            return Receipt(
                id = map[FIELD_ID] as String,
                stuff = map[FIELD_STUFF] as String,
                paid = map[FIELD_PAID] as String,
                buyers = map[FIELD_BUYERS] as List<String>,
                payment = map[FIELD_PAYMENT] as Int,
                reportedBy = map[FIELD_REPORTED_BY] as String,
                timestamp = map[FIELD_TIMESTAMP] as Date,
            )
        }
    }
}
