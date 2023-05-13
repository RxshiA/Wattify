package com.example.wattify.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.wattify.R

class HomeActivity : AppCompatActivity(){
    private lateinit var btnDevices: Button
    private lateinit var btnPlans: Button
    private lateinit var btnUsage: Button
    private lateinit var btnBillSum: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        btnDevices = findViewById(R.id.btnDevices)
        btnPlans = findViewById(R.id.btnPlans)
        btnUsage = findViewById(R.id.btnUsage)
        btnBillSum = findViewById(R.id.btnBillSum)

        btnDevices.setOnClickListener {
            val intent = Intent(this, DevHomeActivity::class.java)
            startActivity(intent)
        }

        btnPlans.setOnClickListener {
            val intent = Intent(this, PlanHome::class.java)
            startActivity(intent)
        }
//
//        btnUsage.setOnClickListener {
//            val intent = Intent(this, FetchingActivity::class.java)
//            startActivity(intent)
//        }
//
//        btnBillSum.setOnClickListener {
//            val intent = Intent(this, FetchingActivity::class.java)
//            startActivity(intent)
//        }

    }
}