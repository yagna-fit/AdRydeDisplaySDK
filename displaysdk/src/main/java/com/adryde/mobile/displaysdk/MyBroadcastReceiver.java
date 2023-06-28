package com.adryde.mobile.displaysdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Classe che estende Broadcastreceiver che permette di ascoltare in broadcast le informazioni del WIFI e del WIFI DIRECT.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private ScannerQRActivity scannerizzaActivity;

    public MyBroadcastReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel, ScannerQRActivity activity2) {
        this.mManager = wifiP2pManager;
        this.mChannel = channel;
        this.scannerizzaActivity=activity2;
    }
    public AppCompatActivity getCorrectActivity()
    {
         return (AppCompatActivity)scannerizzaActivity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                getCorrectActivity().runOnUiThread(() -> Toast.makeText(getCorrectActivity(), "Wifi is ON", Toast.LENGTH_SHORT).show());
            } else {
                getCorrectActivity().runOnUiThread(() -> {
                    Toast.makeText(getCorrectActivity(), "Wifi is OFF", Toast.LENGTH_SHORT).show();
                    scannerizzaActivity.setWifiOn();
                });
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.v("BroadcastEvent", "WIFI_P2P_PEERS_CHANGED_ACTION");
            WifiP2pDeviceList list = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (mManager == null) {
                return;
            }
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                mManager.requestConnectionInfo(mChannel,scannerizzaActivity.connectionInfoListener);
            } else {
                getCorrectActivity().runOnUiThread(() -> {
                   Toast.makeText(getCorrectActivity(), "Device disconnected", Toast.LENGTH_SHORT).show();
                });
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
          /*  if(generaActivity!=null) {
                WifiP2pDevice device = (WifiP2pDevice) intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                String macAddress =getMacAddr();
                final String myMac = device.deviceAddress;
                getCorrectActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        generaActivity.generaQR(myMac);
                    }
                });
            }*/
        }
        else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, 10000);
            if (state == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED)
            {
                // Wifi P2P discovery started.
                getCorrectActivity().runOnUiThread(() -> {
                    Toast.makeText(scannerizzaActivity,"Discovery Start "+ state,Toast.LENGTH_SHORT).show();
                });
            }
            else
            {
                getCorrectActivity().runOnUiThread(() -> {

                        Toast.makeText(scannerizzaActivity,"Discovery Stop "+ state,Toast.LENGTH_SHORT).show();

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scannerizzaActivity.startDiscoveryOfDevices();
                            }
                        },1000);

                });
                // Wifi P2P discovery stopped.
                // Do what you want to do when discovery stopped
            }



        }
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif: all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b: macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {}
        return "02:00:00:00:00:00";
    }

}
