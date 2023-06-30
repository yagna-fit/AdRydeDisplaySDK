package com.adryde.mobile.displaysdk_app

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.adryde.mobile.displaysdk.ScannerQRActivity
import com.adryde.mobile.displaysdk.service.ScannerService
import com.adryde.mobile.displaysdk.ui.fragment.PermissionRequestFragment
import com.adryde.mobile.displaysdk.ui.fragment.PermissionRequestType
import com.adryde.mobile.displaysdk.viewmodel.DisplayViewModel
import com.adryde.mobile.util.Constants
import com.adryde.mobile.util.EncSharedPreferences

//import dagger.hilt.androd.AndroidEntryPoint

//@AndroidEntryPoint
class MainActivity : AppCompatActivity(),   MainFragment.Callbacks, PermissionRequestFragment.Callbacks {

    private lateinit var displayViewModel: DisplayViewModel
    private var mService: ScannerService? = null

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        displayViewModel = ViewModelProvider(this).get(DisplayViewModel::class.java)
        EncSharedPreferences.setStringData(Constants.KEY_TOKEN, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6eyJfaWQiOiI2NDlkNmI4ZmI2ZDFjNDQ1NTA1MGIyMmEiLCJuYW1lIjoiWWFnbmEiLCJjb250YWN0TnVtYmVyIjoiMTIzNDU2Nzg5MCIsInN0YXR1cyI6IkFDVElWRSJ9LCJpYXQiOjE2ODgxMjY2NzgsImV4cCI6MTY4ODIxMzA3OH0.5b62yiAV2NuM8JrxRsbbkIevOJloOZ3mnqbZTAHUkHE")
        EncSharedPreferences.setStringData(Constants.USER_ID, "649d6b8fb6d1c4455050b22a")
        startActivity(Intent(this, ScannerQRActivity::class.java))
        finish()
       // initFragment()


    }

    private fun serviceInit(): Boolean {
        if (mService == null) {
            val bindIntent = Intent(this, ScannerService::class.java)
            bindService(bindIntent, mServiceConnection, BIND_AUTO_CREATE)
            return true
        } else {
            return false
        }
    }

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, rawBinder: IBinder) {
            mService = (rawBinder as ScannerService.LocalBinder).service
            mService?.initialize(displayViewModel)
            if (mService != null && !mService!!.initialize(displayViewModel)) {
                finish()
            } else {

               var macAddress  = ""
            }
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            ////     mService.disconnect(mDevice);
            mService = null
        }
    }


    private fun initFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)

        if (currentFragment == null) {
            val fragment = MainFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .commit()
        }
    }

    override fun requestFineLocationPermission() {
        val fragment = PermissionRequestFragment.newInstance(PermissionRequestType.FINE_LOCATION)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .addToBackStack(null)
            .commit()

    }

    override fun requestBackgroundLocationPermission() {
        val fragment = PermissionRequestFragment.newInstance(PermissionRequestType.BACKGROUND_LOCATION)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun displayLocationUI() {
        val fragment = MainFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .commit()
    }

}