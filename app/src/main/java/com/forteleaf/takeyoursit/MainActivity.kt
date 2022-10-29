package com.forteleaf.takeyoursit

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.companion.CompanionDeviceManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.forteleaf.takeyoursit.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

private const val SCAN_PERIOD: Long = 10000
private const val REQUEST_ENABLE_BT: Int = 5

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val SCAN_PERIOD: Long = 10 * 1000
    private var isScanning: Boolean = false
    val deviceList: Set<BluetoothDevice?> = HashSet()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = DeviceListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        setSupportActionBar(binding.toolbar)

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }



        // button event button_first2
//        val btn1: Button = findViewById(R.id.button_first2);
//        val btn2: Button = findViewById(R.id.button_first3)
//        val textView1: TextView = findViewById(R.id.textview_first);


        requestPermission()

//        btn1.setOnClickListener {
//            scanLeDevice()
//        }
//
//        btn2.setOnClickListener {
//        }


    }

    @SuppressLint("MissingPermission")
    private fun scanLeDevice() {
        val bluetoothManager: BluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        if (!isScanning) {
            bluetoothLeScanner.startScan(scanCallback)
            isScanning = true
        } else {
            isScanning = false
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }

    /**
     * scanCallback
     */
    private val scanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("NewApi", "MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device
            // add device to set list

            Log.d(
                "ScanResult",
                "${result.rssi} ${result.scanRecord} ${result.txPower} ${result.primaryPhy} ${result.secondaryPhy}"
            )

            Log.d(
                "BLE",
                "onScanResult: ${device.name} ${device.address} ${device.type} ${device.uuids} ${device.bluetoothClass} ${device.alias} "
            )
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            Log.i("onBatchScanResults", "!!!!!!!!!!!!$results")
        }
    }

    @SuppressLint("MissingPermission")
    private val leScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        runOnUiThread {
            Toast.makeText(this, "Device found: ${device.name}", Toast.LENGTH_SHORT).show()

        }
    }

    /**
     * request permission
     */
    private fun requestPermission() {
        val permissions = mutableListOf<String>(
            BLUETOOTH, BLUETOOTH_ADMIN, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION,
            BLUETOOTH_SCAN, BLUETOOTH_CONNECT, BLUETOOTH_ADVERTISE
        )

        for (permission in permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions.toTypedArray(), 1)
            }
        }
    }

    private val deviceManager: CompanionDeviceManager by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(CompanionDeviceManager::class.java)
    }

    private fun PackageManager.missingSystemFeature(name: String): Boolean =
        !hasSystemFeature(name)

    /**
     * Returns the [BluetoothAdapter] for handling management of Bluetooth
     */
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}