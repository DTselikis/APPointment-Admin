package gr.evasscissors.appointmentadmin.model.network.pushnotification

data class PushNotification(
    val data: PushNotificationData,
    val to: String
)