package com.adryde.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Activity che permette la condivisione dei propi medias.
 */

public class SharingMedia extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SharingMedia";
    private AtomicBoolean connessione = new AtomicBoolean(true);
    private TextView location1, location2;
    private RadioGroup radiogrp;
    private NotificationManagerCompat notificationManager;
    private LinearLayout btndisconnect;
    //private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_media);
        location1 = (TextView) findViewById(R.id.location1);
        location2 = (TextView) findViewById(R.id.location2);
        btndisconnect = (LinearLayout) findViewById(R.id.btndisconnect);
        btndisconnect.setOnClickListener(this);
        radiogrp= (RadioGroup) findViewById(R.id.radiogrp1);
        notificationManager = NotificationManagerCompat.from(this);
        //statusTextView = (TextView) findViewById(R.id.statustext);
        Bundle b = getIntent().getExtras();

        ServerClass server = new ServerClass(handler, connessione, getCacheDir().toString(), null, getContentResolver(), notificationManager, this);
        server.start();

        radiogrp.setOnCheckedChangeListener((group, checkedId) -> {
            if(SendReceive.getInstance()!=null) {
                if (checkedId == location1.getId()) {
                    SendReceive.getInstance().packgeChange(getPackage(1));

                } else {
                    SendReceive.getInstance().packgeChange(getPackage(2));

                }
            }
        });

    }

    private AdsPackageModel getPackage(int loc) {
        AdsPackageModel ads = new AdsPackageModel();

        if (loc == 1) {
            ads.packageId = "1001";
            for (int i = 1; i <= 15; i++) {
                MediaModel mm = new MediaModel();
                mm.fileType = MediaType.IMAGE;
                mm.id = "Adryde Ad (" + i + ").jpg";
                mm.imageAdsDuration = 5;
                ads.mediaFiles.add(mm);

                if (i <= 7) {
                    MediaModel mmV = new MediaModel();
                    mmV.fileType = MediaType.VIDEO;
                    mmV.id = "Adryde Video (" + i + ").mp4";
                    ads.mediaFiles.add(mmV);
                }
            }
        }
        else {
            ads.packageId = "2001";
            for (int i =16 ;i<=30 ;i++)
            {

                    MediaModel mm = new MediaModel();
                    mm.fileType= MediaType.IMAGE;
                    mm.id = "Adryde Ad ("+i+").jpg";
                    mm.imageAdsDuration = 10;
                    ads.mediaFiles.add(mm);

                int vid = i-8;
                if(vid<=14){
                    MediaModel mmV = new MediaModel();
                    mmV.fileType= MediaType.VIDEO;
                    mmV.id = "Adryde Video ("+vid+").mp4";
                    ads.mediaFiles.add(mmV);
                }
            }
        }
        return ads;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == SendReceive.DISCONNECT) {
                disconnect();
            }
            if (msg.what == SendReceive.SEND_MEDIA) {
                int checkedId = radiogrp.getCheckedRadioButtonId();
                if(SendReceive.getInstance()!=null) {
                    if (checkedId == location1.getId()) {
                        SendReceive.getInstance().packgeChange(getPackage(1));

                    } else {
                        SendReceive.getInstance().packgeChange(getPackage(2));

                    }
                }
            }
        }
    };

    /**
     * Disconnessione del service.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v == btndisconnect) {
            if (SendReceive.getInstance() != null)
                SendReceive.getInstance().disconnectSocket();
            else
                disconnect();

        }
    }


    public void disconnect() {
        if (App.getInstance().mManager != null && App.getInstance().mChannel != null) {
           /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }*/
            App.getInstance().mManager.requestGroupInfo(App.getInstance().mChannel, new WifiP2pManager.GroupInfoListener() {

                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && App.getInstance().mManager != null && App.getInstance().mChannel != null) {
                        App.getInstance().mManager.removeGroup(App.getInstance().mChannel, new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "removeGroup onSuccess -");
                                Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), ScannerQRActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                SendReceive.getInstance().cancel();
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d(TAG, "removeGroup onFailure -" + reason);
                                Toast.makeText(getApplicationContext(), "RemoveGroup onFailure", Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else {
                        Log.d(TAG, "Group Null");
                        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ScannerQRActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        SendReceive.getInstance().cancel();

                    }
                }
            });
        }
    }
}
