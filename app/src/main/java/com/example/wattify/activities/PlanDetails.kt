package com.example.wattify.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wattify.R
import com.example.wattify.adapters.DevAdapter
import com.example.wattify.models.DeviceModel
import com.example.wattify.models.PlanModel
import com.google.firebase.database.*

class PlanDetails : AppCompatActivity() {


    private lateinit var tvPlanId:TextView
    private lateinit var tvPlanName:TextView
    private lateinit var btnUpdate:Button
    private lateinit var btnDelete:Button
    private lateinit var dbRef: DatabaseReference
    private lateinit var devList: ArrayList<DeviceModel>
    private lateinit var devRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView


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

        btnDelete.setOnClickListener {
            deleteRecord(
                intent.getStringExtra("planId").toString()
            )
        }


        devRecyclerView = findViewById(R.id.rvDev)
        devRecyclerView.layoutManager = LinearLayoutManager(this)
        devRecyclerView.setHasFixedSize(true)
        tvLoadingData = findViewById(R.id.tvLoadingData)
        devList = arrayListOf<DeviceModel>()

        getDevicesData()





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


        mDialog.setTitle("Updating $planName")

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

        if(name.isEmpty()){

            Toast.makeText(this, "Enter Plan Name", Toast.LENGTH_LONG).show()

        }else {
            val dbRef =
                FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("X").child(id)
            val planInfo = PlanModel(id, name)
            dbRef.setValue(planInfo)

        }
    }


    private fun deleteRecord(
        id: String
    ){
        val dbRef = FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("X").child(id)
        val mTask = dbRef.removeValue()

        mTask.addOnSuccessListener {
            Toast.makeText(this, "Plan Deleted", Toast.LENGTH_LONG).show()

            val intent = Intent(this, FetchPlans::class.java)
            finish()
            startActivity(intent)
        }.addOnFailureListener{ error ->
            Toast.makeText(this, "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
        }
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
                        override fun onItemClick(position: Int) {

                            val intent = Intent(this@PlanDetails, DeviceDetailsActivity::class.java)

                            //put extras
                            intent.putExtra("devId", devList[position].devId)
                            intent.putExtra("devName", devList[position].devName)
                            intent.putExtra("devWatts", devList[position].devWatts)
                            intent.putExtra("devType", devList[position].devType)
                            startActivity(intent)
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




}

