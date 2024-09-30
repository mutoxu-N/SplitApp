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
            onNewMemberRequest = RequestType.ACCEPT_BY_MODS,
            acceptRate = 100,
        )
    }

    fun toModel() = SettingsModel(
        name = name,
        splitUnit = splitUnit.unit,
        permissionReceiptCreate = permissionReceiptCreate.toIDString(),
        permissionReceiptEdit = permissionReceiptEdit.toIDString(),
        onNewMemberRequest = onNewMemberRequest.toIDString(),
        acceptRate = acceptRate,
    )
}
