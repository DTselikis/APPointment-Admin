package com.homelab.appointmentadmin.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val nickname: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val fbName: String? = null,
    val token: String? = null
) : Parcelable