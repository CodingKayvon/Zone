package com.example.zone.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.zone.R
import com.example.zone.login.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class profileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user?.uid
        val database = FirebaseDatabase.getInstance().reference

        val emailTextView = view.findViewById<TextView>(R.id.profileEmail)
        val displayNameInput = view.findViewById<EditText>(R.id.displayNameInput)
        val aboutMeInput = view.findViewById<EditText>(R.id.aboutMeInput)
        val saveButton = view.findViewById<Button>(R.id.saveProfileButton)
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)

        emailTextView.text = "Email: ${user?.email ?: "Not logged in"}"

        // Load existing profile info
        uid?.let {
            database.child("users").child(it).get().addOnSuccessListener { snapshot ->
                displayNameInput.setText(snapshot.child("displayName").value?.toString() ?: "")
                aboutMeInput.setText(snapshot.child("aboutMe").value?.toString() ?: "")
            }
        }

        // Save changes
        saveButton.setOnClickListener {
            val displayName = displayNameInput.text.toString().trim()
            val aboutMe = aboutMeInput.text.toString().trim()

            if (uid != null) {
                val updates = mapOf(
                    "displayName" to displayName,
                    "aboutMe" to aboutMe
                )
                database.child("users").child(uid).updateChildren(updates)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to save: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), SignInActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }
}
