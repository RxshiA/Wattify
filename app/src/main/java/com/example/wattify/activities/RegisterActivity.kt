package com.example.wattify.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.wattify.R
import com.example.wattify.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var loginTextView: TextView
    private lateinit var passwordToggleButton: ImageButton

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        mDatabase =
            FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child(
                "users"
            )

        nameEditText = findViewById(R.id.register_name)
        emailEditText = findViewById(R.id.register_email)
        passwordEditText = findViewById(R.id.register_password)
        registerButton = findViewById(R.id.register_button)
        loginTextView = findViewById(R.id.login_text)
        passwordToggleButton = findViewById(R.id.password_toggle_button)

        passwordToggleButton.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                passwordEditText.transformationMethod = null
                passwordToggleButton.setImageResource(R.drawable.ic_eye_off)
            } else {
                passwordEditText.transformationMethod = PasswordTransformationMethod()
                passwordToggleButton.setImageResource(R.drawable.ic_eye_24)
            }
        }

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(
                    this,
                    "Please fill in both email and password fields",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val passwordPattern =
                Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,}\$")
            if (!passwordPattern.matches(password)) {
                Toast.makeText(
                    this,
                    "Password must contain at least 8 characters, 1 uppercase letter, 1 number, and 1 symbol",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val currentUser = mAuth.currentUser
                        val userId = currentUser?.uid ?: ""
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                        val user = User(userId, name, email, password)

                        mDatabase.child(userId).setValue(user)
                            .addOnSuccessListener {
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                if (exception is FirebaseAuthUserCollisionException) {
                                    Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
