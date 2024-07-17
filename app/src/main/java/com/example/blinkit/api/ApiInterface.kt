package com.example.blinkit.api

import com.example.blinkit.models.Notification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer c4321c4e4c5cc85fa63e7d7d6cda5a7551d7b05a",
    )

    @POST("v1/projects/blinkit-3dd96/messages:send")
    fun sendNotification(@Body notification: Notification): Call<Notification>
}