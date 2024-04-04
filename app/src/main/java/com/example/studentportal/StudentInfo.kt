package com.example.studentportal

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class StudentInfo : AppCompatActivity() {
    // Imports

    private lateinit var profile: ImageView
    private lateinit var username: TextView
    private lateinit var qualification: TextView

    // Firebase
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_info)

        // Variables
        profile = findViewById(R.id.profileImage)
        username = findViewById(R.id.usernameTextView)
        qualification = findViewById(R.id.qualificationTextView)

        // Get currently logged-in user
        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email

        // Retrieve user info

        db.collection("students").document(email!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(StudentClass::class.java)
                    if (user != null) {
                        // Set profile image
                        Glide.with(this)
                            .load(user.photoUrl)
                            .placeholder(R.drawable.ic_launcher_background) // Placeholder image
                            .into(profile)

                        // Set username
                        username.text = "Username: ${user.email}"

                        // Set qualification
                        qualification.text = "Qualification: ${user.qualification}"
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    companion object {
        private const val TAG = "StudentInfoActivity"
    }
}
