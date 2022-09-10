package com.homelab.appointmentadmin.model.network

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Note(
    val description: String = "",
    var photos: List<NotePhoto>? = null,
    @ServerTimestamp
    val timestamp: Timestamp? = Timestamp.now(),
    val title: String = "",
    val hash: String? = null
)