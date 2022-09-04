package com.homelab.appointmentadmin.ui.customer.contact.notification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.homelab.appointmentadmin.data.ACTIVE_NOTIFICATIONS_FIELD
import com.homelab.appointmentadmin.data.NOTIFICATIONS_COLLECTION
import com.homelab.appointmentadmin.data.NOTIFICATION_FIELD
import com.homelab.appointmentadmin.data.USERS_COLLECTION
import com.homelab.appointmentadmin.model.Notification
import com.homelab.appointmentadmin.model.network.pushnotification.PushNotification
import com.homelab.appointmentadmin.model.network.pushnotification.PushNotificationData
import com.homelab.appointmentadmin.network.FcmApi
import com.homelab.appointmentadmin.ui.customer.contact.notification.SendNotificationFragment.NotificationStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SendNotificationViewModel : ViewModel() {
    val notificationTitle = MutableLiveData<String>()
    val notificationMessage = MutableLiveData<String>()

    private val _notificationSent = MutableSharedFlow<Boolean>()
    val notificationSent: SharedFlow<Boolean> = _notificationSent

    fun sendNotification(token: String, uid: String, type: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            val response = FcmApi.pushNotificationClient.sentNotification(
                PushNotification(
                    PushNotificationData(notificationTitle.value!!, notificationMessage.value!!),
                    token
                )
            )

            storeNotificationToFirestore(uid, type)

            _notificationSent.emit(response.isSuccessful)
        }

    private fun storeNotificationToFirestore(uid: String, type: Int) {
        val notification = Notification(
            notificationTitle.value,
            notificationMessage.value,
            type,
            NotificationStatus.SENT.code
        )

        val db = Firebase.firestore
        db.runTransaction { transaction ->
            val userNotificationsDocRef = db.collection(NOTIFICATIONS_COLLECTION).document(uid)
            val userDocRef = db.collection(USERS_COLLECTION).document(uid)

            transaction.update(
                userNotificationsDocRef,
                NOTIFICATION_FIELD,
                FieldValue.arrayUnion(notification)
            )
            transaction.update(userDocRef, ACTIVE_NOTIFICATIONS_FIELD, FieldValue.increment(1))
        }.addOnFailureListener {
            if (it is FirebaseFirestoreException) {
                Firebase.firestore.runTransaction { transaction ->
                    val userNotificationsDocRef =
                        createUserNotificationDocument(transaction, uid)
                    val userDocRef = db.collection(USERS_COLLECTION).document(uid)

                    storeNotificationToFirestore(
                        transaction,
                        userNotificationsDocRef,
                        notification
                    )
                    updateActiveNotificationsCount(transaction, userDocRef)
                }
            }
        }

    }

    private fun createUserNotificationDocument(
        transaction: Transaction,
        uid: String
    ): DocumentReference {
        val userNotificationsDocRef =
            Firebase.firestore.collection(NOTIFICATIONS_COLLECTION).document(uid)

        transaction.set(
            userNotificationsDocRef,
            mapOf(NOTIFICATION_FIELD to emptyList<Notification>())
        )

        return userNotificationsDocRef
    }

    private fun storeNotificationToFirestore(
        transaction: Transaction,
        userNotificationDocRef: DocumentReference,
        notification: Notification
    ) {
        transaction.update(
            userNotificationDocRef,
            NOTIFICATION_FIELD,
            FieldValue.arrayUnion(notification)
        )
    }

    private fun updateActiveNotificationsCount(
        transaction: Transaction,
        userDocRef: DocumentReference
    ) {
        transaction.update(userDocRef, ACTIVE_NOTIFICATIONS_FIELD, FieldValue.increment(1))
    }
}