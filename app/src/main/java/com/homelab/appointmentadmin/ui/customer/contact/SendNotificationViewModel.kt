package com.homelab.appointmentadmin.ui.customer.contact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.homelab.appointmentadmin.data.NOTIFICATION_FIELD
import com.homelab.appointmentadmin.data.USERS_COLLECTION
import com.homelab.appointmentadmin.model.Notification
import com.homelab.appointmentadmin.model.network.pushnotification.PushNotification
import com.homelab.appointmentadmin.model.network.pushnotification.PushNotificationData
import com.homelab.appointmentadmin.network.FcmApi
import com.homelab.appointmentadmin.ui.customer.contact.SendNotificationFragment.NotificationStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SendNotificationViewModel : ViewModel() {
    val notificationTitle = MutableLiveData<String>()
    val notificationMessage = MutableLiveData<String>()

    private val _notificationSent = MutableSharedFlow<Boolean>()
    val notificationSent: SharedFlow<Boolean> = _notificationSent

    fun sendNotification(token: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val response = FcmApi.pushNotificationClient.sentNotification(
                PushNotification(
                    PushNotificationData(notificationTitle.value!!, notificationMessage.value!!),
                    token
                )
            )

            _notificationSent.emit(response.isSuccessful)
        }

    fun storeNotificationToFirestore(uid: String, type: Int) {
        val notification = Notification(
            notificationTitle.value,
            notificationMessage.value,
            type,
            NotificationStatus.SENT.code
        )
        Firebase.firestore.collection(USERS_COLLECTION).document(uid)
            .update(NOTIFICATION_FIELD, FieldValue.arrayUnion(notification))
    }
}