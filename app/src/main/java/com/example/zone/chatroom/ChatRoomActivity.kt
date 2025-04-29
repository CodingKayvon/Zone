package com.example.zone.chatroom

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zone.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class ChatRoomActivity : AppCompatActivity() {
    var userIDVisit: String = ""
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat_room)

        intent = intent
        val userIdVisit = intent.getStringExtra("visit_id")
        firebaseUser =  FirebaseAuth.getInstance().currentUser

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sendButton = findViewById<Button>(R.id.sendButton)
        val textBox = findViewById<EditText>(R.id.textMessage)

        sendButton.setOnClickListener() {
            val message = textBox.text.toString()
            if (message =="") {
                print("no message")
            }
            else {
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
            }
        }
    }

    /**************************************************************************************
     * creates a message data fragment to send to database                                *
     **************************************************************************************/
    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey
        reference.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener {task ->
                if (task.isSuccessful)
                {
                    val chatListReference = FirebaseDatabase.getInstance()
                        .reference
                        .child("ChatList")
                    chatListReference.child("id").setValue(firebaseUser!!.uid)

                    val reference = FirebaseDatabase.getInstance().reference
                        .child("Users").child(firebaseUser!!.uid)
                }
            }
    }
}