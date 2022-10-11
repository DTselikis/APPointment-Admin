package gr.evasscissors.appointmentadmin.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Notification(
    val title: String? = null,
    val message: String? = null,
    val type: Int? = null,
    val status: Int? = null,
    @ServerTimestamp
    val timestamp: Timestamp = Timestamp.now()
)