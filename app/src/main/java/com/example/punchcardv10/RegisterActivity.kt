package com.example.punchcardv10

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.punchcardv10.databinding.ActivityRegisterBinding

class RegisterActivity : Activity() {

    private lateinit var binding: ActivityRegisterBinding
    private val userList: MutableList<User> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val username = binding.usernameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (isUsernameTaken(username)) {
                    showErrorDialog("Username is already taken.")
                } else if (isEmailTaken(email)) {
                    showErrorDialog("Email is already taken.")
                } else {
                    saveUserLocally(User(username, email, password))
                    showErrorDialog("User registered successfully.")
                    clearFields()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                showErrorDialog("All fields must be filled.")
            }
        }
    }

    private fun isUsernameTaken(username: String): Boolean {
        for (user in userList) {
            if (user.username == username) {
                return true
            }
        }
        return false
    }

    private fun isEmailTaken(email: String): Boolean {
        for (user in userList) {
            if (user.email == email) {
                return true
            }
        }
        return false
    }

    private fun saveUserLocally(user: User) {
        val sharedPref = getSharedPreferences("UsersData", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(user.username, user.password)
            commit()
        }
    }


    private fun showErrorDialog(message: String) {
        binding.errorMessage.text = message
        binding.errorMessage.visibility = View.VISIBLE
    }

    private fun clearFields() {
        binding.usernameInput.text.clear()
        binding.emailInput.text.clear()
        binding.passwordInput.text.clear()
    }

    data class User(
        val username: String,
        val email: String,
        val password: String
    )
}
