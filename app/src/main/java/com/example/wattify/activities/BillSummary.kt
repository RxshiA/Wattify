package com.example.wattify.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.wattify.R

class BillSummary : AppCompatActivity() {
    private lateinit var unitsInput: EditText
    private lateinit var usageOutput: TextView
    private lateinit var fixedChargesOutput: TextView
    private lateinit var totalChargeOutput: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)

        unitsInput = findViewById(R.id.unitsInput)
        usageOutput = findViewById(R.id.usageOutput)
        fixedChargesOutput = findViewById(R.id.fixedChargesOutput)
        totalChargeOutput = findViewById(R.id.totalChargeOutput)

        unitsInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val units = s?.toString()?.toIntOrNull() ?: 0

                val usage: Int = when {
                    units < 40 -> units * 120
                    units < 80 -> units * 150
                    else -> units * 200
                }
                usageOutput.text = usage.toString()

                fixedChargesOutput.text = "1500"

                val totalCharge = usage + 1500
                totalChargeOutput.text = totalCharge.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
