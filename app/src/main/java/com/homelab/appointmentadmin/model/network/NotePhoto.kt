package com.homelab.appointmentadmin.model.network

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class NotePhoto(
    val localUri: String? = null,
    val driveId: String? = null,
    @ServerTimestamp
    val photoUploaded: Timestamp = Timestamp.now()
)