package com.example.zone.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.zone.R

class friendfindingFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var addFriendButton: Button
    private lateinit var suggestedFriendsList: LinearLayout
    private lateinit var addedFriendsList: LinearLayout

    private val suggestedFriends = listOf("bot1", "bot2", "bot3", "bot4", "bot5")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friendfinding, container, false)

        searchView = view.findViewById(R.id.searchView)
        addFriendButton = view.findViewById(R.id.addFriendButton)
        suggestedFriendsList = view.findViewById(R.id.suggestedFriendsList)
        addedFriendsList = view.findViewById(R.id.addedFriendsList)

        loadSuggestedFriends()
        // addedFriendsList stays blank for now

        addFriendButton.setOnClickListener {
            val searchText = searchView.query.toString().trim()
            if (searchText.isNotEmpty()) {
                Toast.makeText(requireContext(), "Friend request sent to $searchText!", Toast.LENGTH_SHORT).show()
                searchView.setQuery("", false) // Clear after adding
            } else {
                Toast.makeText(requireContext(), "Please search a username first!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
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
