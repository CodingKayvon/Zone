package com.example.zone.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.zone.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set default fragment (chatsFragment)
        loadFragment(profileFragment())

        // Set up bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.ic_chats -> loadFragment(chatsFragment())
                R.id.nav_friends -> loadFragment(friendfindingFragment())
                R.id.ic_eventMap -> loadFragment(MapsFragment())
                R.id.ic_profile -> loadFragment(profileFragment())
                else -> false
            }
        }

        setUserStatus("online")
    }

    override fun onStop(){
        super.onStop()
        setUserStatus("offline")
    }

    override fun onDestroy() {
        super.onDestroy()
        setUserStatus("offline")
    }


    // Helper function to load a fragment
    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
        return true
    }

    private fun setUserStatus(status: String){
        currentUser?.uid?.let { uid ->
            firestore.collection("users").document(uid)
                .update("status", status)
        }
    }
}
