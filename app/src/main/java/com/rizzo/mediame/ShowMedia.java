package com.rizzo.mediame;

import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.io.File;
import java.util.ArrayList;

/**
 * Activity per visualizzare una singola foto della galleria.
 */

public class ShowMedia extends AppCompatActivity {

    private TextView pathTv;
    private TextView time;
    private TextView size;
    private String id;
    private StringBuilder loadedPath = new StringBuilder();
    private NotificationManagerCompat notificationManager;

    private VLCVideoLayout mVideoLayout = null;

    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;

    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = true;
    private static final String ASSET_FILENAME = "bbb.m4v";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().show();
        setContentView(R.layout.activity_show_media);
        pathTv = (TextView) findViewById(R.id.path);
        time = (TextView) findViewById(R.id.time);
        size = (TextView) findViewById(R.id.size);
        Bundle b = getIntent().getExtras();
        notificationManager = NotificationManagerCompat.from(this);

        if (b != null && b.containsKey("media")) {
            String path = "file://" + (String) b.get("media");
            id = (String) b.get("id");
            //img.setImageURI(Uri.parse(path));
            scaricaFotoHD();
        }

    }

    /**
     * Setta l'option menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mediamenu, menu);
        return true;
    }

    /**
     * Se viene premuto donwload, viene spawnato un thread che chiede di ricevere la foto in qualità alta e si mette in attesa.
     * Una volta ricevuta la foto salva quest'ultima all'interno della nostra galleria.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.download:
                Thread scarica = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String tmp = "";
                            synchronized (loadedPath) {
                                while (loadedPath.length() == 0) {
                                    loadedPath.wait();
                                }
                                tmp = loadedPath.toString();
                                loadedPath.setLength(0);
                            }
                            //String urlStored =  MediaStore.Images.Media.insertImage(getContentResolver(), tmp, "media_photo", "downloaded from MediaMe app");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Image donwloaded", Toast.LENGTH_SHORT).show();
                                }
                            });
                            notificationManager.notify(1, MyNotification.getDownloadNotification(getApplicationContext(), tmp));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                scarica.start();
                return true;
        }
        return true;
    }

    /**
     * Metodo che va a creare un thread con valore di ritorno che contatta il server per richiedere una foto hd
     */
    public void scaricaFotoHD() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String dataString = SendReceive.getInstance().writeWithReturn("id:" + id + ",", "getfoto".getBytes());
                String[] data = dataString.split(",");
                String path = data[0];

                synchronized (loadedPath) {
                    loadedPath.append(path);
                    loadedPath.notify();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pathTv.setText(data[0]);
                        size.setText(data[1]);
                        time.setText(data[2]);
                        initializePlayer(Uri.fromFile(new File(path)));
                    }
                });

            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();


        //mMediaPlayer.play();

    /*    try {
            final Media media = new Media(mLibVLC, getAssets().openFd(ASSET_FILENAME));
            mMediaPlayer.setMedia(media);
            media.release();
        } catch (IOException e) {
            throw new RuntimeException("Invalid asset folder");
        }*/


    }



    @Override
    public void onStop() {
        super.onStop();
        mMediaPlayer.stop();
        mMediaPlayer.detachViews();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.detachViews();
        mMediaPlayer.release();
        mLibVLC.release();

    }

    protected boolean initializePlayer(Uri videoUri) {
        //mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);
        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        mLibVLC = new LibVLC(this, args);
        mMediaPlayer = new MediaPlayer(mLibVLC);

        mVideoLayout = findViewById(R.id.player_view);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        FrameLayout frameLayout = findViewById(R.id.videoFrme);
        double ratio = 16d/9d;  // 16:9
        frameLayout.getLayoutParams().height = (int)(width / ratio);
        final Media media = new Media(mLibVLC, videoUri);
       // media.addOption(":fullscreen");
        mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);
        mMediaPlayer.setMedia(media);
        mMediaPlayer.setVideoScale(MediaPlayer.ScaleType.SURFACE_ORIGINAL);

        media.release();
        mMediaPlayer.play();


        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       finish();
    }

}
