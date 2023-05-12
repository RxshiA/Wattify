package com.example.wattify.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.wattify.R
import com.example.wattify.models.DeviceModel
import com.example.wattify.models.PlanModel
import com.google.firebase.database.FirebaseDatabase

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

        btnUpdate.setOnClickListener {
            openUpdateDialog(
                intent.getStringExtra("planId").toString(),
                intent.getStringExtra("planName").toString()
            )
        }


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

    private fun openUpdateDialog(
        planId: String,
        planName: String
    ) {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.update_dialog_plan, null)

        mDialog.setView(mDialogView)

        val etPlanName = mDialogView.findViewById<EditText>(R.id.etPlanName)


        val btnUpdateData = mDialogView.findViewById<Button>(R.id.btnUpdateData)

        etPlanName.setText(intent.getStringExtra("planName").toString())


        mDialog.setTitle("Updating $planName Record")

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnUpdateData.setOnClickListener {
            updateEmpData(
                planId,
                etPlanName.text.toString()

            )

            Toast.makeText(applicationContext, "Plan  Updated", Toast.LENGTH_LONG).show()

            //we are setting updated data to our textviews
            tvPlanName.text = etPlanName.text.toString()


            alertDialog.dismiss()
        }
    }

    private fun updateEmpData(
        id: String,
        name: String,
    ) {
        val dbRef = FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("X").child(id)
        val planInfo = PlanModel(id, name)
        dbRef.setValue(planInfo)
    }
}

