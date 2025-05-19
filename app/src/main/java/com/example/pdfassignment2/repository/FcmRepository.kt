package com.example.pdfassignment2.repository

import android.content.Context
import android.util.Log
import com.example.pdfassignment2.model.apiService.FcmService
import com.google.auth.oauth2.GoogleCredentials
import com.google.gson.JsonObject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class FcmRepository @Inject constructor(
    private val fcmService: FcmService,
    @ApplicationContext private val context: Context
) {
    private val projectId = "pdfassignment2"  // Find in Firebase Settings
    private val serviceAccountEmail = "ajaykumar.dev2782@gmail.com"  // From Firebase Service Accounts


    suspend fun sendNotification(title: String, body: String) {
        try {
            // Move both token generation and network call to IO dispatcher
            val response = withContext(Dispatchers.IO) {
                val jwtToken = generateFcmJwtToken()
                val message = createMessage(title, body)
                fcmService.sendNotification(
                    projectId = projectId,
                    authHeader = "Bearer $jwtToken",
                    body = message
                )
            }



            // Handle response on Main thread if needed
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("FCM", "Notification sent")
                } else {
                    Log.e("FCM", "Error: ${response.errorBody()?.string()}")
                }
            }
        } catch (e: Exception) {
            Log.e("FCM", "Error sending notification", e)
        }
    }

    private suspend fun generateFcmJwtToken(): String = withContext(Dispatchers.IO) {
        val credentials = GoogleCredentials.fromStream(
            context.assets.open("service_account.json")
        ).createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))

        credentials.refreshIfExpired()
        credentials.accessToken.tokenValue
    }

    private fun createMessage(title: String, body: String): JsonObject {
        return JsonObject().apply {
            add("message", JsonObject().apply {
                addProperty("topic", "all")

                // Notification payload (for when app is in background)
                add("notification", JsonObject().apply {
                    addProperty("title", title)
                    addProperty("body", body)
                    // Remove click_action from here
                })

                // Data payload (for custom handling)
                add("data", JsonObject().apply {
                    addProperty("title", title)
                    addProperty("body", body)
                    // You can add custom key-value pairs here
                    addProperty("custom_action", "OPEN_ACTIVITY")
                })
            })
        }
    }
}
