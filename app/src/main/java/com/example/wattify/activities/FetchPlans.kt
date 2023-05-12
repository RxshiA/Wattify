package com.example.wattify.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wattify.R
import com.example.wattify.adapters.DevAdapter
import com.example.wattify.adapters.PlanAdapter
import com.example.wattify.models.PlanModel
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class FetchPlans : AppCompatActivity() {

    private lateinit var planRecylcleView: RecyclerView
    private lateinit var tvLoadingPlans: TextView
    private lateinit var planList:ArrayList<PlanModel>
    private lateinit var dbRef:DatabaseReference

    private lateinit var searchView: SearchView
    private lateinit var searchList: ArrayList<PlanModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fetch_plans)

        planRecylcleView=findViewById(R.id.rvPlan)
        planRecylcleView.layoutManager=LinearLayoutManager(this)
        planRecylcleView.setHasFixedSize(true)
        tvLoadingPlans=findViewById(R.id.tvLoadingPlan)

        searchView = findViewById(R.id.search)
        planList= arrayListOf<PlanModel>()
        searchList = arrayListOf<PlanModel>()

        getPlanData()

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
                    planList.forEach{
                        if (it.planName?.toLowerCase(Locale.getDefault())!!.contains(searchText)) {
                            searchList.add(it)
                        }
                    }
                    planRecylcleView.adapter?.notifyDataSetChanged()
                } else {
                    searchList.clear()
                    searchList.addAll(planList)
                    planRecylcleView.adapter!!.notifyDataSetChanged()
                }
                return false
            }
        })

    }

    private fun getPlanData(){
        planRecylcleView.visibility= View.GONE
        tvLoadingPlans.visibility=View.VISIBLE

       dbRef = FirebaseDatabase.getInstance("https://wattify-ce140-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("X")

        dbRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                planList.clear()
                if(snapshot.exists()){
                    for(planSnap in snapshot.children){

                        val planData=planSnap.getValue(PlanModel::class.java)
                        planList.add(planData!!)
                    }

                    searchList.addAll(planList)
                    val mAdapter = PlanAdapter(searchList)
                    planRecylcleView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object :PlanAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent =Intent(this@FetchPlans, PlanDetails::class.java)


                            intent.putExtra("planId",searchList[position].planId)
                            intent.putExtra("planName",searchList[position].planName)
                            startActivity(intent)
                        }


                    })

                    planRecylcleView.visibility=View.VISIBLE
                    tvLoadingPlans.visibility=View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }
}