package com.example.wattify.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wattify.R
import com.example.wattify.adapters.DevAdapter
import com.example.wattify.models.DeviceModel
import com.google.firebase.database.*

class FetchingActivity : AppCompatActivity() {

    private lateinit var devRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var devList: ArrayList<DeviceModel>
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fetching)

        devRecyclerView = findViewById(R.id.rvDev)
        devRecyclerView.layoutManager = LinearLayoutManager(this)
        devRecyclerView.setHasFixedSize(true)
        tvLoadingData = findViewById(R.id.tvLoadingData)

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
                        override fun onItemClick(position: Int) {

                            val intent = Intent(this@FetchingActivity, DeviceDetailsActivity::class.java)

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
