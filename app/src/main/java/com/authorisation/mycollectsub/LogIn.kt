package com.authorisation.mycollectsub

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


class LogIn : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        val auth = FirebaseAuth.getInstance()
        val emailInfo = findViewById<EditText>(R.id.emailView1);
        val passwordInfo = findViewById<EditText>(R.id.passwordView1);
        val LogInBtn = findViewById<Button>(R.id.logInBtn);
        val SignUpLink = findViewById<TextView>(R.id.signUpLink);

        SignUpLink.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent);
            finish();
        }

        LogInBtn.setOnClickListener {

            val email = emailInfo.text.toString();
            val password = passwordInfo.text.toString();

            //validation
            //check email address
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInfo.setError("Email is invalid");
                return@setOnClickListener
            }
            //check password
            if (password.length < 8) {
                passwordInfo.setError("Password must be at least 8 characters long")
                return@setOnClickListener
            }

            else{
                logInFirebase(email, password);
            }
        }


    }

    fun logInFirebase(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful
                    val user = auth.currentUser
                    Toast.makeText(this@LogIn, "Login successful", Toast.LENGTH_SHORT).show();
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Login failed
                    val exception = task.exception
                    Toast.makeText(this@LogIn, "Email or Password is Incorrect", Toast.LENGTH_LONG).show();
                }
            }
    }

}