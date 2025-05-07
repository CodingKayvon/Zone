package com.example.zone.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zone.AdapterClasses.UserAdapter
import com.example.zone.ModelClasses.Chatlist
import com.example.zone.ModelClasses.Users
import com.example.zone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class chatsFragment : Fragment() {
    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var usersChatList: List<Chatlist>? = null
    lateinit var recycler_view_chat_list: RecyclerView
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        Log.d("CHATS FRAG", "1")
        recycler_view_chat_list = view.findViewById(R.id.recycler_view_chat_list)
        recycler_view_chat_list.setHasFixedSize(true)
        recycler_view_chat_list.layoutManager = LinearLayoutManager(context)
        Log.d("CHATS FRAG", "2")
        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()
        Log.d("CHATS FRAG", "3")
        val refFirestore = FirebaseFirestore.getInstance()
            .collection("chatlist")

        refFirestore.get()
            .addOnSuccessListener { documents ->
                Log.d("CHATS FRAG", "4")
                if (documents != null) {
                    Log.d("CHATS FRAG", "5")
                    (usersChatList as ArrayList<Chatlist>).clear()
                    Log.d("CHATS FRAG", "6")
                    for (snapshot in documents) {
                        Log.d("CHATS FRAG", "7")
                        val chatlist = snapshot.toObject<Chatlist>()

                        if (chatlist.sender == firebaseUser!!.uid) {
                            (usersChatList as ArrayList<Chatlist>).add(chatlist)
                            Log.d("CHATS FRAG", "8")
                        }
                    }
                    Log.d("CHATS FRAG", "9")
                    retrieveChatLists()
                }
            }

        return view

    }
    private fun retrieveChatLists(){
        mUsers = ArrayList()
        val refFireStore = FirebaseFirestore.getInstance().collection("users")
        refFireStore.get()
            .addOnSuccessListener { documents ->
                Log.d("RET FRAG", "1")
                if (documents != null) {
                    (mUsers as ArrayList<Users>).clear()
                    Log.d("RET FRAG", "2")
                    for (snapshot in documents) {
                        Log.d("RET FRAG", "3")
                        val user = snapshot.toObject<Users>()
                        for (eachChatlist in usersChatList!!) {
                            if (user.uid == eachChatlist.receiver) {
                                Log.d("RET FRAG", "4")
                                (mUsers as ArrayList<Users>).add(user)
                            }
                        }
                    }
                    val ctx = context
                    if ((mUsers as ArrayList<Users>).isNotEmpty() && ctx != null)
                    {
                        Log.d("RET FRAG", "5")
                        userAdapter = UserAdapter(requireContext(), (mUsers as ArrayList<Users>), false)
                        Log.d("RET FRAG", "6")
                        recycler_view_chat_list.adapter = userAdapter
                    }
                }
            }

    }
}

