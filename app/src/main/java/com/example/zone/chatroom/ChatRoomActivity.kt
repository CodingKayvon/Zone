package com.example.zone.chatroom

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zone.AdapterClasses.ChatsAdapter
import com.example.zone.ModelClasses.Chat
import com.example.zone.ModelClasses.Chatlist
import com.example.zone.ModelClasses.Users
import com.example.zone.Notifications.Client
import com.example.zone.Notifications.Data
import com.example.zone.Notifications.MyResponse
import com.example.zone.Notifications.NotificationBody
import com.example.zone.Notifications.Sender
import com.example.zone.Notifications.Token
import com.example.zone.R
import com.example.zone.home.HomeActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.firestore.v1.Value
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.Continuation

class ChatRoomActivity : AppCompatActivity() {
    var userIdVisit: String = ""
    var firebaseUser: FirebaseUser? = null
    var chatsAdapter: ChatsAdapter? = null
    var mChatList: List<Chat>? = null
    lateinit var recycler_view_chats: RecyclerView
    lateinit var userNameText: TextView

    var notify = false
    var apiService: APIService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat_room)

        val toolbar: Toolbar = findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toolbar)

        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            val intent = Intent(this@ChatRoomActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        apiService = Client.Client.getClient("https://fcm.googleapis.com/").create(APIService::class.java)
        userIdVisit = intent.getStringExtra("visit_id")!!
        firebaseUser = FirebaseAuth.getInstance().currentUser
        userNameText = findViewById(R.id.username_mc)

        recycler_view_chats = findViewById(R.id.recycler_view_chats)
        recycler_view_chats.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recycler_view_chats.layoutManager = linearLayoutManager

        val reference = FirebaseFirestore.getInstance().collection("users").document(userIdVisit)

        reference.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("Listener", "FAILED")
            } else {
                reference.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val user = document.toObject<Users>()
                            userNameText.text = user!!.username
                            retriveMessages(firebaseUser!!.uid, userIdVisit)
                        }
                    }
            }
        }

        FirebaseFirestore.getInstance().collection("chats")
            .whereIn("receiver", listOf(firebaseUser!!.uid, userIdVisit))
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("LIVE UPDATE", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    retriveMessages(firebaseUser!!.uid, userIdVisit)
                }
            }

        val sendButton = findViewById<Button>(R.id.send_message_button)
        val textBox = findViewById<EditText>(R.id.text_message)

        sendButton.setOnClickListener {
            notify = true
            val message = textBox.text.toString()
            if (message == "") {
                Toast.makeText(this@ChatRoomActivity, "Please write a message first...", Toast.LENGTH_LONG).show()
            } else {
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
                retriveMessages(firebaseUser!!.uid, userIdVisit)
            }
            textBox.setText("")
        }

        seenMessage(userIdVisit)
    }

    private fun sendMessageToUser(senderId: String, receiverId: String, message: String) {
        val referenceFirestore = FirebaseFirestore.getInstance()
        val messageHashMap = HashMap<String, Any?>()

        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = "-1"
        messageHashMap["createdAt"] = FieldValue.serverTimestamp()

        referenceFirestore.collection("chats")
            .add(messageHashMap)
            .addOnSuccessListener { document ->
                messageHashMap["messageId"] = document.id
                referenceFirestore.collection("chats")
                    .document(document.id)
                    .update("messageId", messageHashMap["messageId"])

                val chatlistHash = HashMap<String, Any?>()

                val chatListReference = FirebaseFirestore.getInstance()
                var chatFound = false
                chatListReference.collection("chatlist")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (snapshot in documents) {
                            val chatListItem = snapshot.toObject<Chatlist>()
                            if (chatListItem.sender == senderId && chatListItem.receiver == receiverId || chatListItem.receiver == senderId && chatListItem.sender == receiverId) {
                                chatFound = true
                            }
                        }
                        if (!chatFound) {
                            chatlistHash["receiver"] = receiverId
                            chatlistHash["sender"] = senderId
                            chatListReference.collection("chatlist")
                                .document()
                                .set(chatlistHash)
                            chatlistHash["receiver"] = senderId
                            chatlistHash["sender"] = receiverId
                            chatListReference.collection("chatlist")
                                .document(receiverId)
                                .set(chatlistHash)
                        }
                    }

                sendNotification(receiverId, firebaseUser!!.uid, message)
            }
    }

    private fun sendNotification(receiverId: String, senderId: String, message: String) {
        val ref = FirebaseFirestore.getInstance().collection("Tokens")

        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot != null) {
                for (item in snapshot) {
                    val token: Token? = item.toObject<Token>()
                    val data = Data(senderId, R.mipmap.ic_launcher_round, "$senderId: $message", "New Message", receiverId)
                    val sender = Sender(data, token!!.token.toString(), NotificationBody("New Message", "$senderId: $message"))

                    apiService!!.sendNotification(sender)
                        .enqueue(object : Callback<MyResponse> {
                            override fun onResponse(p0: Call<MyResponse>, p1: Response<MyResponse>) {
                                if (p1.code() == 200) {
                                    if (p1.body()!!.success != 1) {
                                        Log.w("FCM SEND NOTIF", "FAILED")
                                    }
                                }
                            }

                            override fun onFailure(p0: Call<MyResponse>, p1: Throwable) {
                                Log.w("FCM SEND NOTIF", "FAILED FAILED")
                            }
                        })
                }
            }
        }
    }

    private fun retriveMessages(senderId: String, receiverId: String?) {
        mChatList = ArrayList()
        val referenceFirestore = FirebaseFirestore.getInstance().collection("chats").orderBy("createdAt", Query.Direction.ASCENDING)

        referenceFirestore.get()
            .addOnSuccessListener { documents ->
                (mChatList as ArrayList<Chat>).clear()
                for (snapshot in documents) {
                    val chat = snapshot.toObject<Chat>()
                    if (chat.receiver.equals(senderId) && chat.sender.equals(receiverId)) {
                        (mChatList as ArrayList<Chat>).add(chat)
                    } else if (chat.receiver.equals(receiverId) && chat.sender.equals(senderId)) {
                        (mChatList as ArrayList<Chat>).add(chat)
                    }
                }
                chatsAdapter = ChatsAdapter(this@ChatRoomActivity, mChatList!!)
                recycler_view_chats.adapter = chatsAdapter
            }
        seenMessage(userIdVisit)
    }

    private fun seenMessage(userId: String) {
        val referenceFirestore = FirebaseFirestore.getInstance().collection("chats")
        referenceFirestore.get()
            .addOnSuccessListener { documents ->
                for (snapshot in documents) {
                    val chat = snapshot.toObject<Chat>()
                    if (chat.receiver == firebaseUser!!.uid && chat.receiver == userId) {
                        FirebaseFirestore.getInstance()
                            .collection("chats")
                            .document(snapshot.id)
                            .update("isseen", true)
                    }
                }
            }
    }
}