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
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.forteleaf.takeyoursit.NewDeviceActivity.Companion.EXTRA_REPLY
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
    private lateinit var editWordView: EditText

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)

        setContentView(R.layout.activity_new_device)
        editWordView = findViewById(R.id.edit_device)
        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if( TextUtils.isEmpty(editWordView.text)) {
                setResult(RESULT_CANCELED, replyIntent)
            } else {
                val word = editWordView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, word)
                setResult(RESULT_OK, replyIntent)
            }
            finish()
        }


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = DeviceListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        deviceViewModel.allDevices.observe(this, { devices ->
            // Update the cached copy of the words in the adapter.
            devices?.let { adapter.submitList(it) }
        })

        setSupportActionBar(binding.toolbar)

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
//            scanLeDevice()

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

    companion object {
        const val EXTRA_REPLY = "com.forteleaf.takeyoursit.REPLY"
    }

    @SuppressLint("MissingPermission")
    private fun scanLeDevice() {
        val bluetoothManager: BluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        if (!isScanning) {
            bluetoothLeScanner.startScan(scanCallback)
            Log.d("scanLeDevice", "startScan")
            isScanning = true
        } else {
            isScanning = false
            bluetoothLeScanner.stopScan(scanCallback);
            Log.d("scanLeDevice", "stopScan")
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

    /**
     * by lazy 키워드는 lateinit과 비슷하게 값을 지정하는 작업을 미루는 작업인데 **assign** 되는 시점이 변수를 호출하는 시점이다.
     * 아래 코드를 보면 name 변수 선언에 by lazy 키워드가 붙고 내부 브래킷에 "abcd" 코드가 있다.
     * name변수가 호출되는 시점에 "abcd"로 assign 하겠다는 의미다. 실제 코드를 동작시켜보면 name 호출 시점에 by lazy 내부 로그가 먼저 호출되는 것을 볼 수 있다.
     */
    private val deviceViewModel: DeviceViewModel by viewModels {
        DeviceViewModelFactory((application as DevicesApplication).repository)
    }
}