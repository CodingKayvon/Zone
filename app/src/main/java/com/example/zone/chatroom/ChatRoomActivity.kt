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
import com.example.zone.ModelClasses.Users
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.firestore.v1.Value
import com.squareup.picasso.Picasso
import kotlin.coroutines.Continuation

class ChatRoomActivity : AppCompatActivity() {
    var userIdVisit: String = ""
    var firebaseUser: FirebaseUser? = null
    var chatsAdapter: ChatsAdapter? = null
    var mChatList: List<Chat>? = null
    lateinit var recycler_view_chats: RecyclerView

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

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id")!!
        firebaseUser =  FirebaseAuth.getInstance().currentUser

        recycler_view_chats = findViewById(R.id.recycler_view_chats)
        recycler_view_chats.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recycler_view_chats.layoutManager = linearLayoutManager

        val reference = FirebaseFirestore.getInstance().collection("users").document(userIdVisit)

        reference.addSnapshotListener{ snapshot, e->
            if (e != null)
            {
                Log.d("Listener", "FAILED")
            }
            else
            {

                reference.get()
                    .addOnSuccessListener { document ->
                        if (document != null)
                        {
                            val user = document.toObject<Users>()
                            retriveMessages(firebaseUser!!.uid, userIdVisit)
                        }
                    }
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sendButton = findViewById<Button>(R.id.send_message_button)
        val textBox = findViewById<EditText>(R.id.text_message)

        sendButton.setOnClickListener() {
            val message = textBox.text.toString()
            if (message =="") {
                Toast.makeText(this@ChatRoomActivity, "Please write a message first...", Toast.LENGTH_LONG).show()
            }
            else {
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
                retriveMessages(firebaseUser!!.uid, userIdVisit)
            }
            textBox.setText("")
        }
        /* WILL NOT WORK WITHOUT PURCHASED FIREBASE STORAGE
        val attatch_image_file_btn = findViewById<Button>(R.id.attatch_image_file_btn)
        attatch_image_file_btn.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/"
            startActivityForResult(Intent.createChooser(intent,"Pick Image"), 438)
        }

         */

        seenMessage(userIdVisit)
    }

    /**************************************************************************************
     * creates a message data fragment to send to database                                *
     **************************************************************************************/
    private fun sendMessageToUser(senderId: String, receiverId: String, message: String) {
        val referenceFirestore = FirebaseFirestore.getInstance()

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = "-1"
        referenceFirestore.collection("chats")
            .add(messageHashMap)
            .addOnSuccessListener { document ->
                val chatlistHash = HashMap<String, Any?>()

                //create chatlist item for sender if it doesnt already exist
                chatlistHash["receiver"] = receiverId
                val chatListReference = FirebaseFirestore.getInstance()
                    chatListReference
                    .collection("chatlist")
                    .document(senderId)
                    .set(chatlistHash)

                //Create chatlist for receiver if it doesnt already exist
                chatlistHash["receiver"] = senderId
                chatListReference
                    .collection("chatlist")
                    .document(receiverId)
                    .set(chatlistHash)

                messageHashMap["messageId"] = document.id
                referenceFirestore
                    .collection("chats")
                    .document(document.id)
                    .update("messageId", messageHashMap["messageId"])
            }




                    //add notifs
    }
    private fun retriveMessages(senderId: String, receiverId: String?)
    {
        var chatside: String = ""
        mChatList = ArrayList()
        val referenceFirestore = FirebaseFirestore.getInstance().collection("chats")

        referenceFirestore.get()
            .addOnSuccessListener { documents ->
                (mChatList as ArrayList<Chat>).clear()
                for (snapshot in documents)
                {
                    val chat = snapshot.toObject<Chat>()
                    if (chat.receiver.equals(senderId) && chat.sender.equals(receiverId))
                    {
                        (mChatList as ArrayList<Chat>).add(chat)
                        chatside = "left"
                    }
                    else if (chat.receiver.equals(receiverId) && chat.sender.equals(senderId))
                    {
                        (mChatList as ArrayList<Chat>).add(chat)
                        chatside = "right"
                    }
                }
                chatsAdapter = ChatsAdapter(this@ChatRoomActivity, mChatList!!, chatside)
                recycler_view_chats.adapter = chatsAdapter
            }
        seenMessage(userIdVisit)
    }
/* WONT WORK WITHOUT FIREBASE STORAGE PURCHASE
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 438 && resultCode == RESULT_OK && data!=null && data!!.data!=null)
        {
            val loadingBar = ProgressDialog(this)
            loadingBar.setMessage("Please wait, image is sending...")
            loadingBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            var uploadTask: StorageTask<*>
            uploadTask = storageReference.putFile(fileUri!!)

            uploadTask.continueWithTask<Uri?>(com.google.android.gms.tasks.Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if (!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener {task ->
                if (task.isSuccessful)
                {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "sent you an image."
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId

                    ref.child("chats").child(messageId!!).setValue(messageHashMap)
                }
            }

        }
    }
    */
    private fun seenMessage(userId: String)
    {
        val referenceFirestore = FirebaseFirestore.getInstance().collection("chats")
            referenceFirestore.get()
                .addOnSuccessListener { documents ->
                    for (snapshot in documents)
                    {
                        val chat = snapshot.toObject<Chat>()

                        if (chat.receiver == firebaseUser!!.uid && chat.receiver == userId)
                        {
                            FirebaseFirestore.getInstance()
                                .collection("chats")
                                .document(snapshot.id)
                                .update("isseen", true)
                        }
                    }
                }

    }

}