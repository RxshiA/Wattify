package com.example.wattify.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wattify.R
import com.example.wattify.adapters.PlanAdapter
import com.example.wattify.models.PlanModel
import com.google.firebase.database.*

class FetchPlans : AppCompatActivity() {

    private lateinit var planRecylcleView: RecyclerView
    private lateinit var tvLoadingPlans: TextView
    private lateinit var planList:ArrayList<PlanModel>
    private lateinit var dbRef:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fetch_plans)

        planRecylcleView=findViewById(R.id.rvPlan)
        planRecylcleView.layoutManager=LinearLayoutManager(this)
        planRecylcleView.setHasFixedSize(true)
        tvLoadingPlans=findViewById(R.id.tvLoadingPlan)

        planList= arrayListOf<PlanModel>()

        getPlanData()
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

                    val planAdpater=PlanAdapter(planList)
                    planRecylcleView.adapter=planAdpater

                    planAdpater.setOnItemClickListener(object :PlanAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent =Intent(this@FetchPlans, PlanDetails::class.java)


                            intent.putExtra("planId",planList[position].planId)
                            intent.putExtra("planName",planList[position].planName)
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