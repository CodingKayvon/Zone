package com.example.zone.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.zone.AdapterClasses.UserAdapter
import com.example.zone.ModelClasses.Users
import com.example.zone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class friendfindingFragment : Fragment() {

    private lateinit var addFriendButton: Button
    private lateinit var suggestedFriendsList: LinearLayout
    private lateinit var addedFriendsList: LinearLayout
    private var searchEditText: EditText? = null
    private var recyclerView: RecyclerView? = null

    private val suggestedFriends = listOf("bot1", "bot2", "bot3", "bot4", "bot5")
    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null

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

        searchEditText = view.findViewById<EditText>(R.id.searchUsers)

        searchEditText!!.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(cs.toString().lowercase())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
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
        return view
    }

    private fun retrieveAllUsers() {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val refUsers = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUserID)

        refUsers.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                if(searchEditText!!.text.toString() == "")
                {
                    for (snapshot in p0.children) {
                        val user: Users? = snapshot.getValue(Users::class.java)
                        if (!(user!!.getUID()).equals(firebaseUserID)) {
                            (mUsers as ArrayList<Users>).add(user)
                        }
                    }
                    userAdapter = UserAdapter(context!!, mUsers!!, false)
                    recyclerView!!.adapter = userAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun searchForUsers(str: String) {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val queryUsers = FirebaseDatabase.getInstance().reference
            .child("users").orderByChild("username")
            .startAt(str)
            .endAt(str + "\uf8ff")
        queryUsers.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for (snapshot in p0.children) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    if (!(user!!.getUID()).equals(firebaseUserID)) {
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdapter = UserAdapter(context!!, mUsers!!, false)
                recyclerView!!.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

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
}
