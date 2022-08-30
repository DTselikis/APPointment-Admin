package com.homelab.appointmentadmin.network.apiservice

import com.homelab.appointmentadmin.model.network.pushnotification.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

private const val SERVER_KEY = ""

interface PushNotificationService {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:application/json")
    @POST("fcm/send")
    suspend fun sentNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}