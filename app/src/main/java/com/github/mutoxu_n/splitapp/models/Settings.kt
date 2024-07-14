package com.github.mutoxu_n.splitapp.models

data class Settings(
    val name: String,
    val splitUnit: Int,
    val permissionReceiptCreate: Role,
    val permissionReceiptEdit: Role,
    val onNewMemberRequest: RequestType,
    val acceptRate: Int,
)
