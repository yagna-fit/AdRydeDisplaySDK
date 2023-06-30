package com.adryde.mobile.displaysdk.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import com.adryde.mobile.displaysdk.R
import com.adryde.mobile.displaysdk.util.appLog
import com.adryde.mobile.displaysdk.viewmodel.DisplayViewModel


/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
class ScannerService : Service() {
    var mBinder: IBinder = LocalBinder()
    private var mBluetoothDeviceAddress: String? = null
    private var mHandler: Handler? = null
    private var myApp: DisplayViewModel? = null
    private var disinfectCountDownTimer: CountDownTimer? = null

    companion object {
        private const val SCAN_PERIOD: Long = 20000
        var TAG = ScannerService::class.java.simpleName
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun stopService(name: Intent): Boolean {
        appLog("Service_Stoppping")
        return super.stopService(name)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.e("ClearFromRecentService", "END")
//        if(mDevice!=null)
//        {
//            mDevice?.disconnect()
//            Handler(Looper.getMainLooper()).postDelayed({stopSelf()},800)
//        }
//        else
//            stopSelf()
    }


    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    fun initialize(mainActivityViewModel: DisplayViewModel): Boolean {

        myApp = mainActivityViewModel



        return true
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    fun close() {
        // refreshDeviceCache(mBluetoothGatt);
        Log.w(TAG, "mBluetoothGatt closed")
        mBluetoothDeviceAddress = null
    }

    inner class LocalBinder : Binder() {
        val service: ScannerService
            get() = this@ScannerService
    }

    private val actContext: Context
        get() = this@ScannerService


    override fun onRebind(intent: Intent?) {
        /* if (disinfectCountDownTimer != null) {
             stopForegroundService()
         }*/
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }


    override fun onUnbind(intent: Intent): Boolean {
        return super.onUnbind(intent)
    }


    val NOTIFICATION_CHANNEL_ID = "default channel"
    val NOTIFICATION_ID = 1

    /**
     * Sets the service as a foreground service
     */
    /*private fun showDisinfectTimerNotification() {
        Log.i(TAG, "star foreground service")
        // when the activity closes we need to show the notification that user is connected to the peripheral sensor
        // We start the service as a foreground service as Android 8.0 (Oreo) onwards kills any running background services
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i(TAG, "star foreground service with custom notification")
            startForeground(NOTIFICATION_ID, notification)
        } else {
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(NOTIFICATION_ID, notification)
        }
    }*/

    /**
     * Stops the service as a foreground service
     */
    private fun stopForegroundService() {
        Log.i(TAG, "stopForegroundService")
        // when the activity rebinds to the service, remove the notification and stop the foreground service
        // on devices running Android 8.0 (Oreo) or above
        cancelNotification()
    }


    /**
     * Cancels the existing notification. If there is no active notification this method does nothing
     */
    private fun cancelNotification() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_ID)
    }

    /**
     * Creates the foreground service notification
     */
   /* private fun createNotification(): Notification {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        val pendingFlags: Int = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, pendingFlags)


        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_message))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()
    }*/


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.notification_title)
            val description = getString(R.string.notification_title)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }




}