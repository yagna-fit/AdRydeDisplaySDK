package com.adryde.mobile.displaysdk

import android.content.Intent
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.adryde.mobile.displaysdk.model.AdsPackageModel
import com.adryde.mobile.displaysdk.viewmodel.DisplayViewModel
import java.util.concurrent.atomic.AtomicBoolean
import androidx.lifecycle.ViewModelProvider


/**
 * Activity che permette la condivisione dei propi medias.
 */
class SharingMedia : AppCompatActivity(), View.OnClickListener {
    private val connessione = AtomicBoolean(true)
    private var location1: TextView? = null
    private var location2: TextView? = null
    private var radiogrp: RadioGroup? = null
    private var notificationManager: NotificationManagerCompat? = null
    private var btndisconnect: LinearLayout? = null
    private var displayViewModel: DisplayViewModel? = null

    //private TextView statusTextView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sharing_media)
        displayViewModel = ViewModelProvider(this).get(DisplayViewModel::class.java)

        location1 = findViewById<View>(R.id.location1) as TextView
        location2 = findViewById<View>(R.id.location2) as TextView
        btndisconnect = findViewById<View>(R.id.btndisconnect) as LinearLayout
        btndisconnect!!.setOnClickListener(this)
        radiogrp = findViewById<View>(R.id.radiogrp1) as RadioGroup
        notificationManager = NotificationManagerCompat.from(this)
        //statusTextView = (TextView) findViewById(R.id.statustext);
        val b = intent.extras
        val server = ServerClass(
            handler,
            connessione,
            cacheDir.toString(),
            null,
            contentResolver,
            notificationManager,
            this
        )
        server.start()
        radiogrp!!.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            if (SendReceive.getInstance() != null) {
                onLocationChange(checkedId)
            }
        }
        initObserver()
    }

    private fun initObserver() {
        displayViewModel?.packageDataResponse?.observe(
            this,
            androidx.lifecycle.Observer { packageData ->
                if (packageData!=null){
                    packageData.mediaFiles = packageData.mediaFiles.sortedWith(compareBy { it.sequance_no })

                    SendReceive.getInstance().packgeChange(packageData)
                }
            }
        )
    }

    private fun onLocationChange(checkedId: Int) {
        if (checkedId == location1!!.id) {
            val map = mapOf("latitude" to "23.08622372149125", "longitude" to "72.57359465445015")
            displayViewModel?.fetchPackage("380052",map)
        } else {
            val map = mapOf("latitude" to " 23.016317936281784", "longitude" to "72.5219661758829")
            displayViewModel?.fetchPackage("380051",map)
        }
    }

    private fun getPackage(loc: Int): AdsPackageModel {
        val ads = AdsPackageModel()
        if (loc == 1) {
            ads.packageId = "1001"
            for (i in 1..15) {
                val mm = MediaModel()
                mm.fileType = MediaType.IMAGE
                mm.id = "Adryde Ad ($i).jpg"
                mm.imageAdsDuration = 5
                ads.mediaFiles.add(mm)
                if (i <= 7) {
                    val mmV = MediaModel()
                    mmV.fileType = MediaType.VIDEO
                    mmV.id = "Adryde Video ($i).mp4"
                    ads.mediaFiles.add(mmV)
                }
            }
        } else {
            ads.packageId = "2001"
            for (i in 16..30) {
                val mm = MediaModel()
                mm.fileType = MediaType.IMAGE
                mm.id = "Adryde Ad ($i).jpg"
                mm.imageAdsDuration = 10
                ads.mediaFiles.add(mm)
                val vid = i - 8
                if (vid <= 14) {
                    val mmV = MediaModel()
                    mmV.fileType = MediaType.VIDEO
                    mmV.id = "Adryde Video ($vid).mp4"
                    ads.mediaFiles.add(mmV)
                }
            }
        }
        return ads
    }

    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == SendReceive.DISCONNECT) {
                disconnect()
            }
            if (msg.what == SendReceive.SEND_MEDIA) {
                val checkedId = radiogrp!!.checkedRadioButtonId
                if (SendReceive.getInstance() != null) {
                    onLocationChange (checkedId)
                }
            }
        }
    }

    /**
     * Disconnessione del service.
     *
     * @param v
     */
    override fun onClick(v: View) {
        if (v === btndisconnect) {
            if (SendReceive.getInstance() != null) SendReceive.getInstance()
                .disconnectSocket() else disconnect()
        }
    }

    private var wifiManager: WifiManager? = null
    private var mManager: WifiP2pManager? = null
    private var mChannel: WifiP2pManager.Channel? = null
    fun disconnect() {
        wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        mManager = getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager
        mChannel = mManager!!.initialize(this, mainLooper) {
            Toast.makeText(
                applicationContext, "onChannelDisconnected", Toast.LENGTH_SHORT
            ).show()
        }
        if (mManager != null && mChannel != null) {
            /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }*/
            mManager!!.requestGroupInfo(mChannel) { group ->
                if (group != null && mManager != null && mChannel != null) {
                    mManager!!.removeGroup(mChannel, object : WifiP2pManager.ActionListener {
                        override fun onSuccess() {
                            Log.d(TAG, "removeGroup onSuccess -")
                            Toast.makeText(applicationContext, "Disconnected", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(applicationContext, ScannerQRActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            finish()
                            SendReceive.getInstance().cancel()
                        }

                        override fun onFailure(reason: Int) {
                            Log.d(TAG, "removeGroup onFailure -$reason")
                            Toast.makeText(
                                applicationContext,
                                "RemoveGroup onFailure",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                } else {
                    Log.d(TAG, "Group Null")
                    Toast.makeText(applicationContext, "Disconnected", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, ScannerQRActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                    SendReceive.getInstance().cancel()
                }
            }
        }
    }

    companion object {
        private const val TAG = "SharingMedia"
    }
}