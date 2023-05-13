package com.example.wattify.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wattify.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class DevHomeActivity : AppCompatActivity() {
    private lateinit var btnInsertData: Button
    private lateinit var btnFetchData: Button
    lateinit var toogle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_home)

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)
        val intent1 = Intent(this, HomeActivity::class.java)
        val intent2 = Intent(this, DevHomeActivity::class.java)
//        val intent3 = Intent(this, PlanHome::class.java)
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
//                R.id.nav_plan -> startActivity(intent3)
                R.id.nav_profile -> startActivity(intent4)
                R.id.nav_logout -> startActivity(intent5)
            }

            true
        }

        btnInsertData = findViewById(R.id.btnInsertData)
        btnFetchData = findViewById(R.id.btnFetchData)

        btnInsertData.setOnClickListener {
            val intent = Intent(this, InsertionActivity::class.java)
            startActivity(intent)
        }

        btnFetchData.setOnClickListener {
            val intent = Intent(this, FetchingActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toogle.onOptionsItemSelected(item)){

            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

