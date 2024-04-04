package com.example.studentportal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    // declare variables
    private lateinit var username: EditText
    private lateinit var  password: EditText
    private lateinit var  login: Button
    private lateinit var  signUp: Button

    // Creating firebaseAuth object
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // initialize variables
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        login = findViewById(R.id.btnLogin)
        signUp = findViewById(R.id.regButton)

        // initialising Firebase auth object
        auth = FirebaseAuth.getInstance()

        login.setOnClickListener {
            login()
        }

        signUp.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        // get user input
        val user = username.text.toString()
        val passWrd = password.text.toString()

        // calling signInWithEmailAndPassword(email, pass)
        // function using Firebase auth object
        // On successful response Display a Toast
        auth.signInWithEmailAndPassword(user, passWrd).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
        }

        val intent = Intent(this, StudentInfo::class.java)
        startActivity(intent)
    }
}