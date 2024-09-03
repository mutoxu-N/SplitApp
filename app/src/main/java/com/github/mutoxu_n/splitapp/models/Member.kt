package com.github.mutoxu_n.splitapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Member(
    val name: String,
    val uid: String?,
    val weight: Float,
    val role: Role,
): Parcelable
