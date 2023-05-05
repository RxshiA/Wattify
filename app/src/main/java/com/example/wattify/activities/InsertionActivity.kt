package com.example.wattify.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wattify.R
import com.example.wattify.models.DeviceModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class InsertionActivity : AppCompatActivity(){
    private lateinit var etDevName: EditText
    private lateinit var etDevWatts: EditText
    private lateinit var etDevType: EditText
    private lateinit var btnSaveData: Button

    private lateinit var dbRef: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insertion)

        etDevName = findViewById(R.id.etDevName)
        etDevWatts = findViewById(R.id.etDevWatts)
        etDevType = findViewById(R.id.etDevType)
        btnSaveData = findViewById(R.id.btnSave)

        dbRef = FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Devices")

        btnSaveData.setOnClickListener {
            saveDeviceData()
        }
    }

    private fun saveDeviceData() {

        //getting values
        val devName = etDevName.text.toString()
        val devWatts = etDevWatts.text.toString()
        val devType = etDevType.text.toString()

        if (devName.isEmpty()) {
            etDevName.error = "Please enter name"
        }
        if (devWatts.isEmpty()) {
            etDevWatts.error = "Please enter watts"
        }
        if (devType.isEmpty()) {
            etDevType.error = "Please enter type"
        }

        val devId = dbRef.push().key!!

        val device = DeviceModel(devId, devName, devWatts, devType)

        dbRef.child(devId).setValue(device)
            .addOnCompleteListener {
                Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_LONG).show()

                etDevName.text.clear()
                etDevWatts.text.clear()
                etDevType.text.clear()


            }.addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }

    }
}
