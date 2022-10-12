package gr.evasscissors.appointmentadmin.network.apiservice

import gr.evasscissors.appointmentadmin.BuildConfig
import gr.evasscissors.appointmentadmin.model.network.pushnotification.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

private const val SERVER_KEY = BuildConfig.FCM_KEY

interface PushNotificationService {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:application/json")
    @POST("fcm/send")
    suspend fun sentNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}