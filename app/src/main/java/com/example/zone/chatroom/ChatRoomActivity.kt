package com.example.zone.chatroom

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
    var reference: DatabaseReference? = null

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

        reference = FirebaseDatabase.getInstance().reference
            .child("users").child(userIdVisit!!)
        reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val user: Users? = p0.getValue(Users::class.java)

                findViewById<TextView>(R.id.username_mc).text = user!!.getUserName()
                val profile_pic = findViewById<ImageView>(R.id.profile_image_mc)
                Picasso.get().load(user.getProfile()).into(profile_pic)

                retriveMessages(firebaseUser!!.uid, userIdVisit, user!!.getProfile())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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
            }
            textBox.setText("")
        }
        val attatch_image_file_btn = findViewById<Button>(R.id.attatch_image_file_btn)
        attatch_image_file_btn.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/"
            startActivityForResult(Intent.createChooser(intent,"Pick Image"), 438)
        }

        seenMessage(userIdVisit)
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
        reference.child("chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener {task ->
                if (task.isSuccessful)
                {
                    val chatListReference = FirebaseDatabase.getInstance()
                        .reference
                        .child("chatlist")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)
                    chatListReference.addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                            if (!p0.exists())
                            {
                                chatListReference.child("id").setValue(userIdVisit)
                            }
                            val chatListRecieverReference = FirebaseDatabase.getInstance()
                                .reference
                                .child("chatlist")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)
                            chatListRecieverReference.child("id").setValue(firebaseUser!!.uid)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                    chatListReference.child("id").setValue(firebaseUser!!.uid)



                    //add notifs
                }
            }
    }
    private fun retriveMessages(senderId: String, receiverId: String?, profile: String?)
    {
        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("chats")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mChatList as ArrayList<Chat>).clear()
                for (snapshot in p0.children)
                {
                    val chat = snapshot.getValue(Chat::class.java)

                    if (chat!!.getReceiver().equals(senderId) && chat.getSender().equals(receiverId)
                        || chat.getReceiver().equals(receiverId) && chat.getSender().equals(senderId))
                    {
                        (mChatList as ArrayList<Chat>).add(chat)
                    }
                }
                chatsAdapter = ChatsAdapter(this@ChatRoomActivity, (mChatList as ArrayList<Chat>), profile!!)
                recycler_view_chats.adapter = chatsAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

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
    var seenListener: ValueEventListener? = null
    private fun seenMessage(userId: String)
    {
        val reference = FirebaseDatabase.getInstance().reference.child("chats")
        seenListener = reference!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children){
                    val chat = dataSnapshot.getValue(Chat::class.java)

                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && chat!!.getSender().equals(userId))
                    {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        dataSnapshot.ref.updateChildren(hashMap)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }
}