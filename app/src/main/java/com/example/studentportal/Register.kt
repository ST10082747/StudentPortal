package com.example.studentportal


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage



class Register : AppCompatActivity() {
    // Imports
    private lateinit var textView: TextView
    private lateinit var userEmail: EditText
    private lateinit var userPassword: EditText
    private lateinit var userQualification: EditText
    private lateinit var selectImg: Button
    private lateinit var uploadImg: ImageView
    private lateinit var saveInfo: Button

    // Firebase
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    private var storage = FirebaseStorage.getInstance()


    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Variables
        textView = findViewById(R.id.textView)
        userEmail = findViewById(R.id.userEmail)
        userPassword = findViewById(R.id.userPassword)
        userQualification = findViewById(R.id.userQualification)
        selectImg = findViewById(R.id.selectImg)
        uploadImg = findViewById(R.id.uploadImg)
        saveInfo = findViewById(R.id.saveInfo)

        selectImg.setOnClickListener {
            // Open gallery for image selection
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        saveInfo.setOnClickListener {
            val email = userEmail.text.toString().trim()
            val password = userPassword.text.toString().trim()
            val qualification = userQualification.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && qualification.isNotEmpty()) {
                uploadImageAndRegister(email, password, qualification)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageAndRegister(email: String, password: String, qualification: String) {
        if (selectedImageUri != null) {
            val storageRef = storage.reference.child("images/${System.currentTimeMillis()}_${email}")
            storageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { uploadTask ->
                    // Get the download URL
                    uploadTask.storage.downloadUrl.addOnSuccessListener { uri ->
                        val photoUrl = uri.toString()
                        // register the user with the image URL
                        registerUser(email, password, qualification, photoUrl)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUser(email: String, password: String, qualification: String, photoUrl: String) {

        auth.createUserWithEmailAndPassword(userEmail.text.toString(), userPassword.text.toString() ).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully Singed Up", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Singed Up Failed!", Toast.LENGTH_SHORT).show()
            }
        }

        val student = hashMapOf(
            "email" to email,
            "password" to password,
            "qualification" to qualification,
            "photoUrl" to photoUrl
        )

        db.collection("students").document(email)
            .set(student)
            .addOnSuccessListener {
                Toast.makeText(this, "Details saved", Toast.LENGTH_SHORT).show()
                // Navigate to next activity or do something else
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Details not saved: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
            // Get the URI of the selected image
            selectedImageUri = data.data
            // Display the selected image in the ImageView if needed
            uploadImg.setImageURI(selectedImageUri)
        }
    }
    companion object {
        const val IMAGE_PICK_CODE = 1000
    }
}