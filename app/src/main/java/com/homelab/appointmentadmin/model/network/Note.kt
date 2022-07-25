package com.homelab.appointmentadmin.model.network

import com.google.firebase.Timestamp

data class Note(
    val description: String? = null,
    val photos: List<String>? = null,
    val timestamp: Timestamp? = null,
    val title: String? = null
)