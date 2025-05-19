package com.example.pdfassignment2.model.apiModel

data class FcmRequest(
    val message: FcmMessage
)

data class FcmMessage(
    val notification: FcmNotification,
    val topic: String
)

data class FcmNotification(
    val title: String,
    val body: String
)

data class FcmResponse(
    val name: String
)


//data class FcmRequest(
//    val message: Message
//)
//
//data class Message(
//    val notification: FcmNotification,
//    val topic: String
//)
//
//data class FcmNotification(
//    val title: String,
//    val body: String
//)
