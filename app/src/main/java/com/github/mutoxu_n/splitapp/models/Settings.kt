package com.github.mutoxu_n.splitapp.models

data class Settings(
    val name: String,
    val splitUnit: SplitUnit,
    val permissionReceiptCreate: Role,
    val permissionReceiptEdit: Role,
    val onNewMemberRequest: RequestType,
    val acceptRate: Int,
) {
    companion object {
        val Default = Settings(
            name = "",
            splitUnit = SplitUnit.TEN,
            permissionReceiptCreate = Role.OWNER,
            permissionReceiptEdit = Role.OWNER,
            onNewMemberRequest = RequestType.MODERATOR,
            acceptRate = 0,
        )
    }
}
