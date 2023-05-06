package com.example.wattify.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.wattify.R

class PlanDetails : AppCompatActivity() {


    private lateinit var tvPlanId:TextView
    private lateinit var tvPlanName:TextView
    private lateinit var btnUpdate:Button
    private lateinit var btnDelete:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_details)

        initView()
        setValuesToView()


    }

      private fun initView(){

          tvPlanId = findViewById(R.id.tvPlanId)
          tvPlanName = findViewById(R.id.tvPlanName)
          btnUpdate = findViewById(R.id.btnUpdate)
          btnDelete = findViewById(R.id.btnDelete)
      }

    private fun setValuesToView(){

        tvPlanId.text=intent.getStringExtra("planId")
        tvPlanName.text=intent.getStringExtra("planName")


    }
}

