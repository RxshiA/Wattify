package com.example.wattify.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.wattify.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity(){
    private lateinit var btnDevices: Button
    private lateinit var btnPlans: Button
    private lateinit var btnUsage: Button
    private lateinit var btnBillSum: Button
    private lateinit var btnProfile: ImageView
    private lateinit var profName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        btnDevices = findViewById(R.id.btnDevices)
        btnPlans = findViewById(R.id.btnPlans)
        btnUsage = findViewById(R.id.btnUsage)
        btnBillSum = findViewById(R.id.btnBillSum)
        btnProfile = findViewById(R.id.userLogoImageView)
        profName = findViewById(R.id.userNameTextView)

        btnDevices.setOnClickListener {
            val intent = Intent(this, FetchingActivity::class.java)
            startActivity(intent)
        }

        btnPlans.setOnClickListener {
            val intent = Intent(this, PlanHome::class.java)
            startActivity(intent)
        }

        btnUsage.setOnClickListener {
            val intent = Intent(this, Usage::class.java)
            startActivity(intent)
        }

        btnBillSum.setOnClickListener {
            val intent = Intent(this, BillSummary::class.java)
            startActivity(intent)
        }

        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("users").child(currentUser!!.uid)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("name").value.toString()
                profName.setText(userName)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Failed to read user data", error.toException())
            }
        })

    }
}