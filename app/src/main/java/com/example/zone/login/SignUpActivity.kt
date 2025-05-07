package com.example.zone.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.zone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    //private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        //Firebase Auth
        //db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        //Connecting designs
        val signInLink = findViewById<TextView>(R.id.suToSignIn)
        val emailInput = findViewById<EditText>(R.id.suEmailInput)
        val passwordInput = findViewById<EditText>(R.id.suPasswordInput)
        val signUpButton = findViewById<Button>(R.id.suLoginButton)


        //On Sign-In Click || Change Screens
        signInLink.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        //Sign Up
        signUpButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            //Missing Email || Password on creation
            if(email.isEmpty() || password.isEmpty()) {
                //Empty Field Error Message
                Toast.makeText(this, "Please enter both the email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Signing up...", Toast.LENGTH_SHORT).show()

            //Add account || Store in firebase
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val userId = authResult.user?.uid ?:return@addOnSuccessListener

                    //User Data
                    val user: MutableMap<String, Any> = HashMap()
                    user["email"] = email
                    user["Password"] = password
                    Toast.makeText(this, "Sign-up Successful", Toast.LENGTH_SHORT).show()

                    val userdata: MutableMap<String, Any> = HashMap()
                    val username = email.substring(0, email.indexOf('@'))
                    userdata["username"] = username
                    userdata["uid"] = userId
                    userdata["description"] = "I'm a new user!"
                    userdata["status"] = "offline"
                    userdata["profile"] = "https://www.gravatar.com/avatar/2c7d99fe281ecd3bcd65ab915bac6dd5?s=250"
                    userdata["search"] = username.toLowerCase()

                    val refUsers = FirebaseDatabase.getInstance().reference.child("users").child(userId)
                    refUsers.updateChildren(userdata)
                        .addOnCompleteListener{ task ->
                            if (task.isSuccessful)
                            {
                               // val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                                Toast.makeText(this, "Sign-up Successful", Toast.LENGTH_SHORT).show()
                                //startActivity(intent)
                                //finish()
                            }
                        }
                    /*
                    db.collection("users").document(userId)
                        .set(userdata)
                        .addOnSuccessListener {

                            Log.d("dbFirebase", "User data saved for ID: ${userId}")
                            Toast.makeText(this, "Sign-up Successful", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.w("dbFirebase", "Failed to save user data: ", e)
                            Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                     */
                }
                .addOnFailureListener { e ->
                    Log.w("dbFirebase", "Sign-up Failed", e)
                    Toast.makeText(this, "Sign-up Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    } //end of override fun
}