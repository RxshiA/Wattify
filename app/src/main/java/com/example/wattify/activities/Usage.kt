package com.example.wattify.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wattify.R
import com.example.wattify.adapters.DevAdapter
import com.example.wattify.models.DeviceModel
import com.example.wattify.models.UserDeviceModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.util.Locale

class Usage : AppCompatActivity() {

    private lateinit var devRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var devList: ArrayList<DeviceModel>
    private lateinit var dbRef: DatabaseReference
    lateinit var toogle : ActionBarDrawerToggle
    private lateinit var totDaily : TextView
    private lateinit var searchView: SearchView
    private lateinit var searchList: ArrayList<DeviceModel>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage)

        //////////////////////////search///////////////////////
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
        /////////////////////////search  end//////////////////

        devRecyclerView = findViewById(R.id.rvDevice)
        devRecyclerView.layoutManager = LinearLayoutManager(this)
        devRecyclerView.setHasFixedSize(true)
        tvLoadingData = findViewById(R.id.tvItem)
        totDaily = findViewById(R.id.totDaily)

        searchList = arrayListOf<DeviceModel>()
        searchView = findViewById(R.id.search)
        devList = arrayListOf<DeviceModel>()

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

        getDailyTotal()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDailyTotal() {
        val currentDate = LocalDate.now().toString()

        val dbRef = FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("UserDevices")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sum = 0
                for (userSnapshot in dataSnapshot.children) {

                        val date = userSnapshot.child("date").value?.toString()
                        if (date == currentDate) {
                            // Date matches, retrieve the corresponding data
                            val data = userSnapshot.getValue(UserDeviceModel::class.java)
                            // Handle the retrieved data

                            if (data != null) {
                                sum = sum + (data.hrs!!.toInt() * data.units!!.toInt())
                            }
                        }
                }
                totDaily.text = sum.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error retrieving data from database: $error")
                Toast.makeText(applicationContext, "Error retrieving data from database", Toast.LENGTH_LONG).show()
            }
        })

    }
//Get all the corresponding devices

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
                            intent.putExtra("devWatts", devList[position].devWatts)


                            openUpdateDialog(


                                intent.getStringExtra("devId").toString(),
                                intent.getStringExtra("devName").toString(),
                                intent.getStringExtra("devWatts").toString()
                            )

                            //put extras
                            intent.putExtra("devId", devList[position].devId)
                            intent.putExtra("devName", devList[position].devName)
                            intent.putExtra("devWatts", devList[position].devWatts)
                            intent.putExtra("devType", devList[position].devType)
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
//Dialog Box

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openUpdateDialog(
        devId: String,
        devName: String,
        devWatts : String,
    ) {

        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.entry_dialog, null)

        mDialog.setView(mDialogView)

        val etDevTime = mDialogView.findViewById<EditText>(R.id.svTime)
        val etDevWatts = mDialogView.findViewById<EditText>(R.id.svWatts)
        val totWatts = mDialogView.findViewById<EditText>(R.id.totWatts)
        totWatts.isEnabled = false

        val btnSaveData = mDialogView.findViewById<Button>(R.id.btnSaveData)
        val btnDeleteData = mDialogView.findViewById<Button>(R.id.btnDeleteData)

        val maxHours = 24
        val integerInputFilter = object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val inputValue = dest?.subSequence(0, dstart).toString() + source?.subSequence(start, end).toString() + dest?.subSequence(dend, dest.length).toString()

                return if (inputValue.isEmpty() || inputValue.toIntOrNull() ?: 0 <= maxHours) {
                    null
                } else {
                    ""
                }
            }
        }

        etDevTime.filters = arrayOf(integerInputFilter)

        mDialog.setTitle(" $devName ")

        val alertDialog = mDialog.create()

        alertDialog.show()

        ///////////////////////////////////////////////////////////////////////////////////
        val user = Firebase.auth.currentUser
        var userDevId = " "
        val currentDate = LocalDate.now().toString()
        user?.let{
            userDevId = it.uid + devId + currentDate
        }

        val database = FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app")
        val myRef = database.getReference("UserDevices/$userDevId")

        class MyValueEventListener : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val hrs = snapshot.child("hrs").getValue(String::class.java)

                    etDevTime.setText(hrs, TextView.BufferType.EDITABLE)
                }  else {
                    btnSaveData.setOnClickListener {
                        if (etDevTime.text.toString().isEmpty()) {
                         etDevTime.error = "Please enter a value"
                         }else{
                            saveDeviceData(
                                devId,
                                etDevTime.text.toString(),
                                etDevWatts.text.toString(),
                                currentDate
                            )

                            Toast.makeText(applicationContext, "Device Data Saved", Toast.LENGTH_LONG).show()

                            alertDialog.dismiss()
                         }

                    }
                }
                etDevWatts.setText(devWatts)
                etDevWatts.isEnabled = false

                if(etDevTime.text.toString().isNotEmpty()){
                    val totalWatts = etDevTime.text.toString().toInt() * etDevWatts.text.toString().toInt()
                    totWatts.text = Editable.Factory.getInstance().newEditable(totalWatts.toString())
                }

                etDevTime.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {}

                    override fun afterTextChanged(s: Editable?) {
                        val hrs = s?.toString()?.toIntOrNull() ?: 0

                        val totalWatts = hrs * etDevWatts.text.toString().toInt()

                        totWatts.text = Editable.Factory.getInstance().newEditable(totalWatts.toString())
                    }
                })


            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error retrieving data from database: $error")
                Toast.makeText(applicationContext, "Error retrieving data from database", Toast.LENGTH_LONG).show()
            }
        }

        val valueEventListener = MyValueEventListener()
        myRef.addValueEventListener(valueEventListener)
////////////////////////////////////////////////////////////////////////
        btnSaveData.setOnClickListener {
            saveDeviceData(
                devId,
                etDevTime.text.toString(),
                etDevWatts.text.toString(),
                currentDate
            )

            Toast.makeText(applicationContext, "Device Data Saved", Toast.LENGTH_LONG).show()

            alertDialog.dismiss()
        }

        btnDeleteData.setOnClickListener {
            deleteRecord(devId)
        }
    }


//Save Function

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveDeviceData(
        id: String,
        time: String,
        watts: String,
        date: String) {

        val databaseRef = FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("UserDevices")

        val user = Firebase.auth.currentUser
        var userDevId = " "
        val currentDate = LocalDate.now().toString()
        user?.let{
             userDevId = it.uid + id + currentDate
        }

        val userDev = UserDeviceModel(userDevId, time, watts,date )

        databaseRef.child(userDevId).setValue(userDev)
            .addOnCompleteListener {
                Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_LONG).show()
            }.addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }

    }
//Delete Function

    @RequiresApi(Build.VERSION_CODES.O)
    private fun deleteRecord(
        id: String
    ){
        val user = Firebase.auth.currentUser
        var userDevId = " "
        val currentDate = LocalDate.now().toString()
        user?.let{
            userDevId = it.uid + id + currentDate
        }
        val dbRef = FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("UserDevices/$userDevId")
        val mTask = dbRef.removeValue()

        mTask.addOnSuccessListener {
            Toast.makeText(this, " Cleared", Toast.LENGTH_LONG).show()

            val intent = Intent(this, Usage::class.java)
            finish()
            startActivity(intent)
        }.addOnFailureListener{ error ->
            Toast.makeText(this, "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toogle.onOptionsItemSelected(item)){

            return true
        }
        return super.onOptionsItemSelected(item)
    }
}