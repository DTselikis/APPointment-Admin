package com.homelab.appointmentadmin.model.network

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Note(
    val description: String? = null,
    val photos: List<String>? = null,
    @ServerTimestamp
    val timestamp: Timestamp? = Timestamp.now(),
    val title: String? = null,
    var hash: String? = null
)