package com.homelab.appointmentadmin.model.network

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Note(
    val description: String? = null,
    val photos: List<String>? = null,
    @ServerTimestamp
    val timestamp: Timestamp? = null,
    val title: String? = null,
    val hash: String? = null
)