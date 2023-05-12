package com.example.wattify.activities


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.wattify.R
import com.example.wattify.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var passwordEditText: EditText
    private lateinit var togglePasswordImageView: ImageView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var currentUser: User
    private lateinit var logoutButton : Button
    private lateinit var editButton: Button
    private var isPasswordVisible = false

    companion object {
        private const val TAG = "ProfileActivity"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mAuth = Firebase.auth
        mDatabase = FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("users")
        nameTextView = findViewById(R.id.profile_name)
        emailTextView = findViewById(R.id.profile_email)
        passwordEditText = findViewById(R.id.profile_password)
        togglePasswordImageView = findViewById(R.id.profile_password_toggle)
        logoutButton = findViewById(R.id.profile_logout_button)
        editButton = findViewById(R.id.editbtn)

        // Get the current user's data
        val userId = mAuth.currentUser?.uid ?: ""
        mDatabase.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    currentUser = snapshot.getValue(User::class.java)!!
                    nameTextView.text = currentUser.name
                    emailTextView.text = currentUser.email
                    passwordEditText.setText(currentUser.password)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error getting user data", error.toException())
                Toast.makeText(this@ProfileActivity, "Error getting user data", Toast.LENGTH_SHORT).show()
            }
        })

        togglePasswordImageView.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                togglePasswordImageView.setImageResource(R.drawable.ic_password_visible)
            } else {
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                togglePasswordImageView.setImageResource(R.drawable.ic_password_hidden)
            }
        }

        logoutButton.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        editButton.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
