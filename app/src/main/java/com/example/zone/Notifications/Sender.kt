package com.example.zone.Notifications

data class NotificationBody(
    val title: String,
    val body: String
)

class Sender(
    var data: Data,
    var to: String,
    var notification: NotificationBody? = null
)