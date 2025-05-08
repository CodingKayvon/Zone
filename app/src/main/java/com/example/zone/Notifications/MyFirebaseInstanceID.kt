package com.example.zone.Notifications

import android.util.Log
import com.example.zone.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceID : FirebaseMessagingService() {

    override fun onNewToken(p0: String)
    {
        super.onNewToken(p0)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if(!task.isSuccessful)
            {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val refreshToken = task.result
            if (firebaseUser != null)
            {
                updateToken(refreshToken)
            }
        })

    }

    private fun updateToken(refreshToken: String?) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseFirestore.getInstance().collection("Tokens")
        val token = Token(refreshToken!!)

        ref.document(firebaseUser!!.uid).set(token)
    }
}