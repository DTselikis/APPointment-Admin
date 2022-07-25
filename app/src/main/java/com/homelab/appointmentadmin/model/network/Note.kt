package com.homelab.appointmentadmin.model.network

import com.google.firebase.firestore.ServerTimestamp

data class Note(
    val description: String? = null,
    val photos: List<String>? = null,
    @ServerTimestamp
    val timestamp: ServerTimestamp? = null,
    val title: String? = null
)