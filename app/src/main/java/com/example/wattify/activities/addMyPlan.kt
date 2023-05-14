package com.example.wattify.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.wattify.R
import com.example.wattify.models.PlanModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class addMyPlan : AppCompatActivity() {

    private lateinit var etPlanName:EditText
    private lateinit var btnSavePlan:Button

    private lateinit var dbRef:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?){

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_my_plan)

        etPlanName=findViewById(R.id.etPlanName)
        btnSavePlan=findViewById(R.id.btnSavePlan)

        dbRef=FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("X")

        btnSavePlan.setOnClickListener{


            savePlanData()


        }


    }

    private fun savePlanData(){

        val planName=etPlanName.text.toString()

        if(planName.isEmpty()){


            etPlanName.error="Enter Plan name"

        }else {

            val planId = dbRef.push().key!!
            val plan = PlanModel(planId, planName)

            dbRef.child(planId).setValue(plan)
                .addOnCompleteListener {
                    Toast.makeText(this, "Plan Inserted Succesfully", Toast.LENGTH_LONG).show()
                    etPlanName.text.clear()

                }.addOnFailureListener { err ->
                    Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                }

        }

    }


}