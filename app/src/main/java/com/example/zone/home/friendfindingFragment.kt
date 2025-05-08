package com.example.zone.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zone.AdapterClasses.UserAdapter
import com.example.zone.ModelClasses.Users
import com.example.zone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject


class friendfindingFragment : Fragment() {
    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var recyclerView: RecyclerView? = null
    private var searchEditText: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_friendfinding, container, false)

        recyclerView = view.findViewById(R.id.searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        mUsers = ArrayList()
        retrieveAllUsers()

        searchEditText = view.findViewById<EditText>(R.id.searchUsersET)
        /*
        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

         */
        return view

    }

    private fun retrieveAllUsers() {
        Log.d("RETRIEVE", "1")
        val firebaseUserID = FirebaseAuth.getInstance().currentUser?.uid
        val refUsersFireStore = FirebaseFirestore.getInstance().collection("users")


        refUsersFireStore.get()
            .addOnSuccessListener { documentSnapshot ->
                Log.d("RETRIEVE", "2")
                (mUsers as ArrayList<Users>).clear()
                Log.d("RETRIEVE", "3")
                    for (snapshot in documentSnapshot)
                    {
                        Log.d("RETRIEVE", "4")
                        val user = snapshot.toObject<Users>()
                        Log.d("RETRIEVE", "5")
                        if (user.uid != firebaseUserID)
                        {
                            Log.d("RETRIEVE", "6")
                            (mUsers as ArrayList<Users>).add(user)
                        }

                    }
                val ctx = context
                if ((mUsers as ArrayList<Users>).isNotEmpty() && ctx != null)
                {
                    Log.d("RETRIEVE", "7")
                    userAdapter = UserAdapter(ctx, mUsers!!, false)
                    Log.d("RETRIEVE", "8")
                    recyclerView?.adapter = userAdapter
                }
            }

    }
/*
    private fun searchForUsers(str: String) {
        val firebaseUserID = FirebaseAuth.getInstance().currentUser?.uid
        val queryUsersFireStore = FirebaseFirestore.getInstance().collection("users")

        queryUsersFireStore.get()
            .addOnSuccessListener { users ->
                (mUsers as ArrayList<Users>).clear()
                if (searchEditText!!.text.toString() == "")
                {
                    for (snapshot in users)
                    {
                        val user: Users = snapshot.toObject(Users::class.java)
                        if (user.username!!.contains(str) && user.uid != firebaseUserID)
                        {
                            (mUsers as ArrayList<Users>).add(user)
                        }

                    }
                }
                userAdapter = UserAdapter(requireContext(), mUsers!!, false)
                recyclerView!!.adapter = userAdapter
            }
    }

 */
}
