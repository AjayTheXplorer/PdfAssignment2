package com.example.pdfassignment2.model.apiService

import com.example.pdfassignment2.model.apiModel.FcmRequest
import com.example.pdfassignment2.model.apiModel.FcmResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

//interface FcmService {
//    @Headers(
//        "Content-Type: application/json",
//        "Authorization: key=115108691553227357082"
//    )
//    @POST("v1/projects/pdfassignment-f204f/messages:send")
//    suspend fun sendNotification(
//        @Body body: FcmRequest,
//        @Header("Authorization") authHeader: String
//    ): Response<FcmResponse>
//}

//interface FcmService {
//    @Headers(
//        "Content-Type: application/json",
//        "Authorization: key=115108691553227357082"
//    )
//    @POST("fcm/send")  // ✅ This is correct for the Legacy API
//    suspend fun sendNotification(
//        @Body body: JsonObject
//    ): Response<JsonObject>
//}


interface FcmService {
    @Headers("Content-Type: application/json")
    @POST("v1/projects/{project_id}/messages:send")
    suspend fun sendNotification(
        @Path("project_id") projectId: String,
        @Header("Authorization") authHeader: String,
        @Body body: JsonObject
    ): Response<JsonObject>
}



//interface FcmService {
//    @POST("fcm/send")
//    suspend fun sendNotification(
//        @Body body: FcmRequest,
//        @Header("Authorization") authHeader: String
//    ): Response<FcmResponse>
//}
