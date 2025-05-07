package com.example.zone.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class friendfindingFragment : Fragment() {
    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var recyclerView: RecyclerView? = null
    private var searchEditText: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_friendfinding, container, false)

        recyclerView = view.findViewById(R.id.searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        mUsers = ArrayList()
        retrieveAllUsers()

        searchEditText = view.findViewById<EditText>(R.id.searchUsersET)

        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {
                TODO("Not yet implemented")
            }
        })
        return view
        //add functionality to the button when you get the chance, i left some working functions for it



        /*
        val view = inflater.inflate(R.layout.fragment_friendfinding, container, false)

        searchView = view.findViewById(R.id.searchView)
        addFriendButton = view.findViewById(R.id.addFriendButton)
        suggestedFriendsList = view.findViewById(R.id.suggestedFriendsList)
        addedFriendsList = view.findViewById(R.id.addedFriendsList)

        loadSuggestedFriends()

         */
        // addedFriendsList stays blank for now
/*
        addFriendButton.setOnClickListener {
            val searchText = searchView.query.toString().trim()
            if (searchText.isNotEmpty()) {
                Toast.makeText(requireContext(), "Friend request sent to $searchText!", Toast.LENGTH_SHORT).show()
                searchView.setQuery("", false) // Clear after adding
            } else {
                Toast.makeText(requireContext(), "Please search a username first!", Toast.LENGTH_SHORT).show()
            }
        }

 */
    }

    private fun retrieveAllUsers() {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val refUsers = FirebaseDatabase.getInstance().reference.child("users")

        refUsers.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for (snapshot in p0.children) {
                    val user: Users? = p0.getValue(Users::class.java)
                    if (!(user!!.getUID()).equals(firebaseUserID)) {
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdapter = UserAdapter(context!!, mUsers!!, false)
                recyclerView!!.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun searchForUsers(str: String) {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val queryUsers = FirebaseDatabase.getInstance().reference
            .child("users").orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryUsers.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for (snapshot in p0.children) {
                    val user: Users? = p0.getValue(Users::class.java)
                    if (!(user!!.getUID()).equals(firebaseUserID)) {
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdapter = UserAdapter(context!!, mUsers!!, false)
                recyclerView!!.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        /*
        val queryUsers = FirebaseDatabase.getInstance().reference
            .child("users").orderByChild("username")
            .startAt(str)
            .endAt(str + "\uf8ff")
        queryUsers.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for (snapshot in p0.children) {
                    val user: Users? = p0.getValue(Users::class.java)
                    if (!(user!!.getUID()).equals(firebaseUserID)) {
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        */


    }
/*
    private fun loadSuggestedFriends() {
        for (friendName in suggestedFriends) {
            val textView = TextView(requireContext()).apply {
                text = friendName
                textSize = 18f
                setPadding(0, 8, 0, 8)
            }
            suggestedFriendsList.addView(textView)
        }
    }

 */
}
