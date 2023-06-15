package com.example.punchcardv10

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.punchcardv10.databinding.ActivityRegisterBinding

class RegisterActivity : Activity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val username = binding.usernameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(username, email, password)
                showErrorDialog("User registered successfully.")
                clearFields()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                showErrorDialog("All fields must be filled.")
            }
        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        // Save the user to the database or perform necessary operations
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
}
