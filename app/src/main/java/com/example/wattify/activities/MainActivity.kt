package com.example.wattify.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.wattify.R


class MainActivity : AppCompatActivity() {

    private lateinit var btnInsertData: Button
    private lateinit var btnFetchData: Button
    private lateinit var btnInsertPlans: Button
    private lateinit var btnFetchPlans: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnInsertData = findViewById(R.id.btnInsertData)
        btnFetchData = findViewById(R.id.btnFetchData)
        btnInsertPlans = findViewById(R.id.btnInsertPlans)
        btnFetchPlans = findViewById(R.id.btnFetchPlans)

        btnInsertData.setOnClickListener {
            val intent = Intent(this, InsertionActivity::class.java)
            startActivity(intent)
        }

        btnFetchData.setOnClickListener {
            val intent = Intent(this, FetchingActivity::class.java)
            startActivity(intent)
        }


        btnInsertPlans.setOnClickListener {
            val intent = Intent(this, addMyPlan::class.java)
            startActivity(intent)
        }

        btnFetchPlans.setOnClickListener {
            val intent = Intent(this, FetchPlans::class.java)
            startActivity(intent)
        }

    }
}