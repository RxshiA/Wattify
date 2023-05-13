package com.example.wattify.activities

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wattify.R
import com.example.wattify.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditActivity : AppCompatActivity() {

    private lateinit var nameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var saveBtn: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    companion object {
        private const val TAG = "com.example.wattify.activities.EditActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        nameEdit = findViewById(R.id.nameEditText)
        emailEdit = findViewById(R.id.emailEditText)
        passwordEdit = findViewById(R.id.passwordEditText)
        saveBtn = findViewById(R.id.saveButton)
        mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser?.uid ?: ""
        databaseReference =
            FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users/$userId")


        saveBtn.setOnClickListener {
            Log.d(
                TAG,
                "nameEdit: ${nameEdit.text}, emailEdit: ${emailEdit.text}, passwordEdit: ${passwordEdit.text}"
            )

            val name = nameEdit.text.toString().trim()
            val email = emailEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()

            if (TextUtils.isEmpty(name)) {
                nameEdit.error = "Please enter your name"
                nameEdit.requestFocus()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(email)) {
                emailEdit.error = "Please enter your email"
                emailEdit.requestFocus()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                passwordEdit.error = "Please enter your password"
                passwordEdit.requestFocus()
                return@setOnClickListener
            }

            val user = User(userId, name, email, password)
            databaseReference.setValue(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@EditActivity,
                            "Profile updated successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@EditActivity,
                            "Failed to update profile. Please try again",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}
