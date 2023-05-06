package com.example.wattify.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wattify.R
import com.example.wattify.adapters.DevAdapter
import com.example.wattify.models.DeviceModel
import com.example.wattify.models.UserDeviceModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

class Usage : AppCompatActivity() {

    private lateinit var devRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var devList: ArrayList<DeviceModel>
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage)

        devRecyclerView = findViewById(R.id.rvDevice)
        devRecyclerView.layoutManager = LinearLayoutManager(this)
        devRecyclerView.setHasFixedSize(true)
        tvLoadingData = findViewById(R.id.tvItem)

        devList = arrayListOf<DeviceModel>()

        getDevicesData()

    }

    private fun getDevicesData() {

        devRecyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Devices")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                devList.clear()
                if (snapshot.exists()){
                    for (devSnap in snapshot.children){
                        val devData = devSnap.getValue(DeviceModel::class.java)
                        devList.add(devData!!)
                    }
                    val mAdapter = DevAdapter(devList)
                    devRecyclerView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : DevAdapter.onItemClickListener{
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onItemClick(position: Int) {

                            intent.putExtra("devId", devList[position].devId)
                            intent.putExtra("devName", devList[position].devName)
                            openUpdateDialog(
                                intent.getStringExtra("devId").toString(),
                                intent.getStringExtra("devName").toString()
                            )

                            val intent = Intent(this@Usage, DeviceDetailsActivity::class.java)


                            //put extras
                            intent.putExtra("devId", devList[position].devId)
                            intent.putExtra("devName", devList[position].devName)
                            intent.putExtra("devWatts", devList[position].devWatts)
                            intent.putExtra("devType", devList[position].devType)
//                            startActivity(intent)
                        }

                    })



                    devRecyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openUpdateDialog(
        devId: String,
        devName: String
    ) {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.entry_dialog, null)

        mDialog.setView(mDialogView)

        val etDevTime = mDialogView.findViewById<EditText>(R.id.svTime)
        val etDevWatts = mDialogView.findViewById<EditText>(R.id.svWatts)

        val btnSaveData = mDialogView.findViewById<Button>(R.id.btnSaveData)



        mDialog.setTitle(" $devName ")

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnSaveData.setOnClickListener {
            saveDeviceData(
                devId,
                etDevTime.text.toString(),
                etDevWatts.text.toString()
            )

            Toast.makeText(applicationContext, "Device Data Saved", Toast.LENGTH_LONG).show()

//            //we are setting updated data to our textviews
//            tvDevName.text = etDevName.text.toString()
//            tvDevWatts.text = etDevWatts.text.toString()
//            tvDevType.text = etDevType.text.toString()

            alertDialog.dismiss()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveDeviceData(
        id: String,
         time: String,
        watts: String) {

        //getting values
//        val devName = etDevName.text.toString()
//        val devWatts = etDevWatts.text.toString()
//        val devType = etDevType.text.toString()

//        if (devName.isEmpty()) {
//            etDevName.error = "Please enter name"
//        }
//        if (devWatts.isEmpty()) {
//            etDevWatts.error = "Please enter watts"
//        }
//        if (devType.isEmpty()) {
//            etDevType.error = "Please enter type"
//        }
        val databaseRef = FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("UserDevices")

//        val devId = databaseRef.push().key!!
        val user = Firebase.auth.currentUser
        var userDevId = " "
        val currentDate = LocalDate.now().toString()
        user?.let{
             userDevId = it.uid + id + currentDate
        }


        val userDev = UserDeviceModel(userDevId, time, watts, )

        databaseRef.child(userDevId).setValue(userDev)
            .addOnCompleteListener {
                Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_LONG).show()

//                etDevName.text.clear()
//                etDevWatts.text.clear()
//                etDevType.text.clear()


            }.addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }

    }
}