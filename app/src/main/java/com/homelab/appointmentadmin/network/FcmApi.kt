package com.homelab.appointmentadmin.network

import com.homelab.appointmentadmin.network.apiservice.PushNotificationService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "https://fcm.googleapis.com"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

private val pushNotificationService: PushNotificationService by lazy { retrofit.create(PushNotificationService::class.java)}

object FcmApi {
    val pushNotificationClient = pushNotificationService
}