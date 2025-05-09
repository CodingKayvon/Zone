package com.example.zone.Notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.zone.R
import com.example.zone.chatroom.ChatRoomActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessaging : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data

        val senderId = data["sent"] ?: return
        val receiverId = data["user"] ?: return
        val title = data["title"] ?: "New Message"
        val body = data["body"] ?: ""
        val iconRes = R.drawable.zone_logo // Replace with your actual icon resource

        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        val sharedPref = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val currentChatUser = sharedPref.getString("currentUser", "none")

        // Only notify if this user is the receiver, and they are not currently chatting with the sender
        if (currentUser != null && receiverId == currentUser && currentChatUser != senderId) {
            showNotification(senderId, title, body, iconRes)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("FCM", "New token: $token")

        // Optional: Save token to Firestore or your server
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.uid?.let { uid ->
            val tokensRef = FirebaseFirestore.getInstance().collection("Tokens")
            val tokenMap = hashMapOf("token" to token)
            tokensRef.document(uid).set(tokenMap)
        }
    }

    private fun showNotification(senderId: String, title: String, body: String, iconRes: Int) {
        val channelId = "chat_notifications"
        val channelName = "Chat Notifications"

        val intent = Intent(this, ChatRoomActivity::class.java).apply {
            putExtra("userid", senderId)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val requestCode = senderId.replace(Regex("\\D"), "").toIntOrNull() ?: 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(iconRes)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Channel for chat notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(requestCode, notificationBuilder.build())
    }
}