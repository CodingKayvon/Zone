package com.example.zone.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zone.AdapterClasses.ChatsAdapter
import com.example.zone.AdapterClasses.UserAdapter
import com.example.zone.ModelClasses.Chatlist
import com.example.zone.ModelClasses.Users
import com.example.zone.R
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import com.google.firestore.v1.Value

class chatsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var usersList: List<Chatlist>
    private lateinit var mUsers: MutableList<Users>
    private lateinit var adapter: ChatsAdapter
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference

//    private var userAdapter: UserAdapter? = null
//    private var mUsers: List<Users>? = null
//    private var usersChatList: List<Chatlist>? = null
//    lateinit var recycler_view_chat_list: RecyclerView
//    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        recyclerView = view.findViewById(R.id.recylerViewChats)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        usersList = ArrayList()

        databaseReference = FirebaseDatabase.getInstance().reference
            .child("chatlist")
            .child(firebaseUser.uid)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (usersList as ArrayList).clear()
                for(data in snapshot.children) {
                    val chatlist = data.getValue(Chatlist::class.java)
                    if(chatlist != null){
                        (usersList as ArrayList).add(chatlist)
                    }
                }
                retrieveChatLists()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        return view
//
//        recycler_view_chat_list = view.findViewById(R.id.recycler_view_chat_list)
//        recycler_view_chat_list.setHasFixedSize(true)
//        recycler_view_chat_list.layoutManager = LinearLayoutManager(context)
//
//        firebaseUser = FirebaseAuth.getInstance().currentUser
//
//        usersChatList = ArrayList()


    }
    private fun retrieveChatLists(){
        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList).clear()
                for(data in snapshot.children) {
                    val user = data.getValue(Users::class.java)
                    for(chatList in usersList){
                        if(user?.getUID() == chatList.getId()){
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }
                //adapter = ChatsAdapter(requireContext(), mUsers)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}

