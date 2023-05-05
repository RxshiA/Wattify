package com.example.wattify.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wattify.R
import com.example.wattify.models.DeviceModel
import com.google.firebase.database.FirebaseDatabase

class DeviceDetailsActivity : AppCompatActivity() {
    private lateinit var tvDevId: TextView
    private lateinit var tvDevName: TextView
    private lateinit var tvDevWatts: TextView
    private lateinit var tvDevType: TextView
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_details)

        initView()
        setValuesToViews()

        btnUpdate.setOnClickListener {
            openUpdateDialog(
                intent.getStringExtra("devId").toString(),
                intent.getStringExtra("devName").toString()
            )
        }

        btnDelete.setOnClickListener {
            deleteRecord(
                intent.getStringExtra("devId").toString()
            )
        }
    }

    private fun initView() {
        tvDevId = findViewById(R.id.tvDevId)
        tvDevName = findViewById(R.id.tvDevName)
        tvDevWatts = findViewById(R.id.tvDevWatts)
        tvDevType = findViewById(R.id.tvDevType)

        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)
    }

    private fun setValuesToViews() {
        tvDevId.text = intent.getStringExtra("devId")
        tvDevName.text = intent.getStringExtra("devName")
        tvDevWatts.text = intent.getStringExtra("devWatts")
        tvDevType.text = intent.getStringExtra("devType")
    }

    private fun openUpdateDialog(
        devId: String,
        devName: String
    ) {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.update_dialog, null)

        mDialog.setView(mDialogView)

        val etDevName = mDialogView.findViewById<EditText>(R.id.etDevName)
        val etDevWatts = mDialogView.findViewById<EditText>(R.id.etDevWatts)
        val etDevType = mDialogView.findViewById<EditText>(R.id.etDevType)

        val btnUpdateData = mDialogView.findViewById<Button>(R.id.btnUpdateData)

        etDevName.setText(intent.getStringExtra("devName").toString())
        etDevWatts.setText(intent.getStringExtra("devWatts").toString())
        etDevType.setText(intent.getStringExtra("devType").toString())

        mDialog.setTitle("Updating $devName Record")

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnUpdateData.setOnClickListener {
            updateEmpData(
                devId,
                etDevName.text.toString(),
                etDevWatts.text.toString(),
                etDevType.text.toString()
            )

            Toast.makeText(applicationContext, "Device Data Updated", Toast.LENGTH_LONG).show()

            //we are setting updated data to our textviews
            tvDevName.text = etDevName.text.toString()
            tvDevWatts.text = etDevWatts.text.toString()
            tvDevType.text = etDevType.text.toString()

            alertDialog.dismiss()
        }
    }

    private fun updateEmpData(
        id: String,
        name: String,
        watts: String,
        type: String
    ) {
        val dbRef = FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Devices").child(id)
        val devInfo = DeviceModel(id, name, watts, type)
        dbRef.setValue(devInfo)
    }

    private fun deleteRecord(
        id: String
    ){
        val dbRef = FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Devices").child(id)
        val mTask = dbRef.removeValue()

        mTask.addOnSuccessListener {
            Toast.makeText(this, "Device data deleted", Toast.LENGTH_LONG).show()

            val intent = Intent(this, FetchingActivity::class.java)
            finish()
            startActivity(intent)
        }.addOnFailureListener{ error ->
            Toast.makeText(this, "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
        }
    }
}
