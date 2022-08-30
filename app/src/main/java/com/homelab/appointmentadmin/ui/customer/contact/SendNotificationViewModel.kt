package com.homelab.appointmentadmin.ui.customer.contact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homelab.appointmentadmin.model.network.pushnotification.PushNotification
import com.homelab.appointmentadmin.model.network.pushnotification.PushNotificationData
import com.homelab.appointmentadmin.network.FcmApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SendNotificationViewModel : ViewModel() {
    val notificationTitle = MutableLiveData<String>()
    val notificationMessage = MutableLiveData<String>()

    fun sendNotification(token: String) =
        viewModelScope.launch(Dispatchers.IO) {
            FcmApi.pushNotificationClient.sentNotification(
                PushNotification(
                    PushNotificationData(notificationTitle.value!!, notificationMessage.value!!),
                    token
                )
            )
        }
}