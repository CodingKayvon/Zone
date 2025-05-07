package com.example.zone.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.zone.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

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
    }

    // Helper function to load a fragment
    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
        return true
    }
}
