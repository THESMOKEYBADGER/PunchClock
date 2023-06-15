package com.example.punchcardv10

import HomeActivity
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : Activity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var registerButton: Button
    private lateinit var punchInButton: Button
    private lateinit var errorMessage: TextView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firestore = FirebaseFirestore.getInstance()

        usernameInput = findViewById(R.id.username_input_li)
        passwordInput = findViewById(R.id.password_input_li)
        registerButton = findViewById(R.id.register_button)
        punchInButton = findViewById(R.id.punch_in_button)
        errorMessage = findViewById(R.id.error_message)

        punchInButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            validateUser(username, password)
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateUser(username: String, password: String) {
        firestore.collection("users")
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    saveUserId(username) // Save the user ID to shared preferences
                    finish()
                } else {
                    showErrorDialog("Invalid username or password.")
                }
            }
            .addOnFailureListener { e ->
                // Handle error validating user
            }
    }

    private fun showErrorDialog(message: String) {
        errorMessage.text = message
        errorMessage.visibility = View.VISIBLE
    }

    private fun saveUserId(username: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userId", username)
        editor.apply()
    }
}
