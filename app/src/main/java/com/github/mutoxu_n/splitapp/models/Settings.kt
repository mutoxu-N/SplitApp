package com.github.mutoxu_n.splitapp.models

import com.squareup.moshi.Json

data class Settings(
    @Json(name = "split_unit") val splitUnit: Int,
    @Json(name = "permission_receipt_create") val permissionReceiptCreate: String,
    @Json(name = "permission_receipt_edit") val permissionReceiptEdit: String,
    @Json(name = "on_new_member_request") val onNewMemberRequest: String,
    @Json(name = "accept_rate") val acceptRate: Int,
)