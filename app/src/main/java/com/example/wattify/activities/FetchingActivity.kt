package com.example.wattify.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wattify.R
import com.example.wattify.adapters.DevAdapter
import com.example.wattify.models.DeviceModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class FetchingActivity : AppCompatActivity() {

    private lateinit var devRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var btnAdd: ImageView
    private lateinit var devList: ArrayList<DeviceModel>
    private lateinit var dbRef: DatabaseReference
    lateinit var toogle : ActionBarDrawerToggle

    private lateinit var searchView: SearchView
    private lateinit var searchList: ArrayList<DeviceModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fetching)

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)
        val intent1 = Intent(this, HomeActivity::class.java)
        val intent2 = Intent(this, FetchingActivity::class.java)
        val intent3 = Intent(this, FetchPlans::class.java)
        val intent4 = Intent(this, ProfileActivity::class.java)
        val intent5 = Intent(this, LoginActivity::class.java)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        toogle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        navView.setNavigationItemSelectedListener {

            when(it.itemId){
                R.id.nav_home -> startActivity(intent1)
                R.id.nav_devices -> startActivity(intent2)
                R.id.nav_plan -> startActivity(intent3)
                R.id.nav_profile -> startActivity(intent4)
                R.id.nav_logout -> startActivity(intent5)
            }

            true
        }

        btnAdd = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(this, InsertionActivity::class.java)
            startActivity(intent)
        }

        devRecyclerView = findViewById(R.id.rvDev)
        devRecyclerView.layoutManager = LinearLayoutManager(this)
        devRecyclerView.setHasFixedSize(true)
        tvLoadingData = findViewById(R.id.tvLoadingData)

        searchView = findViewById(R.id.search)
        devList = arrayListOf<DeviceModel>()
        searchList = arrayListOf<DeviceModel>()

        getDevicesData()

        searchView.clearFocus()
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()){
                    devList.forEach{
                        if (it.devName?.toLowerCase(Locale.getDefault())!!.contains(searchText)) {
                            searchList.add(it)
                        }
                    }
                    devRecyclerView.adapter!!.notifyDataSetChanged()
                } else {
                    searchList.clear()
                    searchList.addAll(devList)
                    devRecyclerView.adapter!!.notifyDataSetChanged()
                }
                return false
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toogle.onOptionsItemSelected(item)){

            return true
        }
        return super.onOptionsItemSelected(item)
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
                    searchList.addAll(devList)
                    val mAdapter = DevAdapter(searchList)
                    devRecyclerView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : DevAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {

                            val intent = Intent(this@FetchingActivity, DeviceDetailsActivity::class.java)

                            //put extras
                            intent.putExtra("devId", searchList[position].devId)
                            intent.putExtra("devName", searchList[position].devName)
                            intent.putExtra("devWatts", searchList[position].devWatts)
                            intent.putExtra("devType", searchList[position].devType)
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
