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

    private lateinit var auth: FirebaseAuth
    private val passwordPattern: Pattern =
        Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#\$%^&*()-_+=|\\\\{}\\[\\]:;<>,.?/~]).{6,}$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        val signUpBtn = findViewById<Button>(R.id.signUpbtn)
        val fullNameInfo = findViewById<EditText>(R.id.nameView)
        val emailInfo = findViewById<EditText>(R.id.emailView)
        val passwordInfo = findViewById<EditText>(R.id.passwordView)
        val confirmPasswordInfo = findViewById<EditText>(R.id.confirmPasswordView)
        val LogInLink = findViewById<TextView>(R.id.LogInLink)

        LogInLink.setOnClickListener {
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
            finish()
        }

        signUpBtn.setOnClickListener {
            val fullName: String = fullNameInfo.text.toString()
            val email: String = emailInfo.text.toString()
            val password: String = passwordInfo.text.toString()
            val confirmPassword: String = confirmPasswordInfo.text.toString()

            if (fullName.length < 3) {
                fullNameInfo.setError("Full Name is Invalid")
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInfo.setError("Email is invalid")
                return@setOnClickListener
            }

            if (password.length < 8) {
                passwordInfo.setError("Password must be at least 8 characters long")
                return@setOnClickListener
            }

            if (!passwordPattern.matcher(password).matches()) {
                passwordInfo.setError("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                confirmPasswordInfo.setError("Password not matched")
                return@setOnClickListener
            }

            // Create account in Firebase
            createAccountInFirebase(email, password)
        }
    }

    private fun createAccountInFirebase(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Account creation is successful
                    Toast.makeText(
                        this,
                        "Successfully created an account",
                        Toast.LENGTH_SHORT
                    ).show()

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
