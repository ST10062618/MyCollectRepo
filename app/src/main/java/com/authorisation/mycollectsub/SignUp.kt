package com.authorisation.mycollectsub

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class SignUp : AppCompatActivity() {

    private val passwordPattern: Pattern =
        Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#\$%^&*()-_+=|\\\\{}\\[\\]:;<>,.?/~]).{6,}$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Find views by their IDs
        val signUpBtn = findViewById<Button>(R.id.signUpbtn)
        val fullNameInfo = findViewById<EditText>(R.id.nameView)
        val emailInfo = findViewById<EditText>(R.id.emailView)
        val passwordInfo = findViewById<EditText>(R.id.passwordView)
        val confirmPasswordInfo = findViewById<EditText>(R.id.confirmPasswordView)
        val LogInLink = findViewById<TextView>(R.id.LogInLink)

        // Set click listener for login link text view
        LogInLink.setOnClickListener {
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
            finish()
        }

        // Set click listener for sign-up button
        signUpBtn.setOnClickListener {
            val fullName: String = fullNameInfo.text.toString()
            val email: String = emailInfo.text.toString()
            val password: String = passwordInfo.text.toString()
            val confirmPassword: String = confirmPasswordInfo.text.toString()

            // Validate full name
            if (fullName.length < 3) {
                fullNameInfo.setError("Full Name is Invalid")
                return@setOnClickListener
            }

            // Validate email address
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInfo.setError("Email is invalid")
                return@setOnClickListener
            }

            // Validate password length
            if (password.length < 8) {
                passwordInfo.setError("Password must be at least 8 characters long")
                return@setOnClickListener
            }

            // Validate password using custom pattern
            if (!passwordPattern.matcher(password).matches()) {
                passwordInfo.setError("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
                return@setOnClickListener
            }

            // Validate password and confirmed password match
            if (password != confirmPassword) {
                confirmPasswordInfo.setError("Password not matched")
                return@setOnClickListener
            }

            // Create account in Firebase
            createAccountInFirebase(email, password)
        }
    }

    private fun createAccountInFirebase(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Account creation is successful
                    Toast.makeText(
                        this,
                        "Successfully created an account",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to login activity
                    val intent = Intent(this, LogIn::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Account creation failed
                    val errorMessage = task.exception?.localizedMessage ?: "Unknown error occurred"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }
}
