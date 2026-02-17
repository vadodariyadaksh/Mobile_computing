package com.example.blue

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var btnScan: Button
    private lateinit var btnSendFile: Button
    private lateinit var tvStatus: TextView
    private lateinit var listViewDevices: ListView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: ArrayAdapter<String>

    // Dummy list of devices
    private val dummyDevices = listOf(
        "realme p3",
        "Samsung Galaxy Buds",
        "iPhone 13",
        "MacBook Pro",
        "Xiaomi Mi Band"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        btnScan = findViewById(R.id.btnScan)
        btnSendFile = findViewById(R.id.btnSendFile)
        tvStatus = findViewById(R.id.tvStatus)
        listViewDevices = findViewById(R.id.listViewDevices)
        progressBar = findViewById(R.id.progressBar)

        // Set up adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listViewDevices.adapter = adapter

        // Scan button click – simulate discovery
        btnScan.setOnClickListener {
            simulateScan()
        }

        // Send file button (just shows a message, no real sending)
        btnSendFile.setOnClickListener {
            tvStatus.text = "Send button clicked (dummy)"
        }

        // List item click – show popup dialog with Send/Cancel
        listViewDevices.setOnItemClickListener { _, _, position, _ ->
            val device = dummyDevices[position]
            showSendFileDialog(device)
        }
    }

    private fun simulateScan() {
        tvStatus.text = "Scanning for devices..."
        progressBar.visibility = ProgressBar.VISIBLE
        adapter.clear()

        // Simulate a 2-second delay
        Handler(Looper.getMainLooper()).postDelayed({
            adapter.addAll(dummyDevices)
            tvStatus.text = "Found ${dummyDevices.size} devices"
            progressBar.visibility = ProgressBar.GONE
        }, 2000)
    }

    private fun showSendFileDialog(deviceName: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Send File")
        builder.setMessage("Do you want to send a file to $deviceName?")
        builder.setPositiveButton("Send File") { dialog, _ ->
            // Simulate sending – update status
            tvStatus.text = "Sending to $deviceName... "
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}