package com.adryde.driver;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Handler;

import androidx.core.app.NotificationManagerCompat;

/**
 * Thread che realizza effettivamente il servizio in background di invio e ricezione dei messaggi sul socket TCP.
 * Questo thread viene instanziato ugualmente su client e server, per predisporre l'applicazione in futuro per implementare
 * la condivisione delle gallerie in tempo reale su tutti e due i device.
 */

public class SendReceive extends Thread {

    private final Context context;
    public Socket socket;
    private DataInputStream inputStream;
    public DataOutputStream outputStream;
    private int type;
    private AtomicBoolean connesso;
    ArrayList<String> immaginiPath = null;
    String chacheDir;
    RecyclerViewAdapter strz;
    ContentResolver contentResolver;
    private static SendReceive Instance;
    public static int HEADER_SIZE = 1024;
    private int fileSharedPerPackage = 0;
    //---------------------
    private StringBuilder fotoDownloaded = new StringBuilder();
    private NotificationManagerCompat notificationManager;
    private Handler handler;
    //---------------------
    public static int DISCONNECT = -1;
    public static int SEND_MEDIA = 1;
    private ProgressDialog pr;
    String elapsedTime = "";
    private AdsPackageModel currentPackage;

    public SendReceive(Socket skt, int ty, AtomicBoolean b, String chache, RecyclerViewAdapter strnz, ContentResolver cont, NotificationManagerCompat nom, Handler hand, Context context) {
        this.context = context;
        socket = skt;
        type = ty;
        connesso = b;
        chacheDir = chache;
        strz = strnz;
        contentResolver = cont;
        notificationManager = nom;
        handler = hand;
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            Instance = this;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Singleton pattern
     *
     * @return
     */
    public static SendReceive getInstance() {
        if (Instance != null) {
            return Instance;
        } else {
            return null;
        }
    }

    /**
     * Cliclo lettura decodifica del messaggio risposta tipica del server.
     * HEADERS dei messaggi:
     * "id:numero," indica la rischiesta dal client di voler vedere l'immagine con quello specifico id
     * "path:nomefile," indica la ricezione di un immagine ad alta qualità da scaricare
     * "send_media" indica il voler ricevere altre foto dal content provider
     * "size1:id1,size2:id2.....sizen.idn" indica la ricezione di n foto con ognuna possiede size[n] e id[n].
     */

    @Override
    public void run() {
        //ExecutorService executor = Executors.newCachedThreadPool();
        while (socket != null && !socket.isClosed()) {
            try {
                Log.v("Connessione", socket.toString());
                if (!socket.isConnected() && !connesso.get()) {
                    disconnectSocket();
                } else {
                    int lenght = inputStream.readInt();
                    if (lenght > 0) {
                        //Richiesta di decodifica delle immagini ricevute
                        byte[] hd = new byte[HEADER_SIZE];
                        inputStream.readFully(hd, 0, HEADER_SIZE);
                        String header = new String(hd, 0, hd.length);

                        if (header.contains(",") && !header.contains("id") && !header.contains("path")) {
                            //Riceve le foto e le decodifica
                            String[] split = header.split(",");
                            int n_foto = split.length - 1;
                            final int[] sizes = new int[n_foto];
                            ArrayList<String> fileSizes = new ArrayList<>();
                            int len = 0;
                            for (int i = 0; i < n_foto; i++) {
                                sizes[i] = Integer.parseInt(split[i].split(":")[0]);
                                fileSizes.add(split[i].split(":")[2]);
                                len += sizes[i];
                            }
                            final byte[] message = new byte[len];
                            inputStream.readFully(message, 0, len);
                            immaginiPath = new ArrayList<>();
                            final ArrayList<String> ids = new ArrayList<>();
                            int letti = 0;
                            for (int i = 0; i < n_foto; i++) {
                                try {
                                    ids.add(split[i].split(":")[1]);
                                    File downloadingMediaFile = new File(chacheDir, split[i].split(":")[1] + ".jpg");
                                    FileOutputStream out = new FileOutputStream(downloadingMediaFile);
                                    out.write(message, letti, sizes[i]);
                                    try {
                                        out.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    String path =/*"file://"+*/chacheDir + "/" + split[i].split(":")[1] + ".jpg";
                                    immaginiPath.add(path);
                                    letti += sizes[i];
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            Log.v("Connessione", "ho caricato:" + n_foto + " foto");

                            //Lettura delle foto completata invio path al thread UI
                            if (immaginiPath != null && immaginiPath.size() > 0 && strz != null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        strz.addFoto(immaginiPath, ids, fileSizes);
                                    }
                                });

                            }
                        }
                        //Richiesta di generare le foto ed inviarle
                        else if (header.contains("send_media")) {
                            Message msg = handler.obtainMessage();
                            msg.what = SEND_MEDIA;
                            handler.sendMessage(msg);
                        } else if (header.contains("id")) {
                            String id = header.split(",")[0].split(":")[1];
                            String path = GetMediaFromID(id);

                            //   byte[] message = new byte[(int)f.length()];
                            //BufferedInputStream buf = new BufferedInputStream(new FileInputStream(f));
                            //  buf.read(message, 0, message.length);
                            //  buf.close();
                            //  write("path:hd_"+id+",",message);

                            //
                            writeWholeFile(id, path);

                            //
                        } else if (header.contains("path")) {
                            //byte[] message = new byte[lenght-HEADER_SIZE];
                            int msgLength = lenght - HEADER_SIZE;
                            String name = header.split(",")[0].split(":")[1];
                            //inputStream.readFully(message, 0, lenght-HEADER_SIZE);
                            File downloadingMediaFile = new File(chacheDir, name + ".mkv");
                            FileOutputStream out = new FileOutputStream(downloadingMediaFile);
                            byte[] len = new byte[85048000];
                            long total = 0;
                            int count;
                            long tStart = System.currentTimeMillis();


                            (App.getInstance().getCurrentActivity()).runOnUiThread(() -> {
                                pr = new ProgressDialog(App.getInstance().getCurrentActivity());
                                pr.setTitle("Downloading File : " + downloadingMediaFile.getName());
                                pr.setMessage("Size : 0/" + getStringSizeLengthFile(msgLength) +
                                        "\nElapsed Time : " + (elapsedTime = getElapsedTime(tStart)));
                                pr.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                pr.setProgress(0);
                                pr.setCancelable(false);
                                pr.setMax(100);
                                pr.show();
                            });


                            while ((count = inputStream.read(len)) != -1) {
                                total += count;
                                // to update the progress bar just call:
                                out.write(len, 0, count);
                                int progress = (int) ((total * 100) / msgLength);
                                Log.e("FTProgress", "" + progress);
                                long finalTotal = total;
                                (App.getInstance().getCurrentActivity()).runOnUiThread(() -> {
                                    pr.setMessage("Size : " + getStringSizeLengthFile(finalTotal) + "/" + getStringSizeLengthFile(msgLength) +
                                            "\nElapsed Time : " + (elapsedTime = getElapsedTime(tStart)));
                                });
                                //sendFileProgress(progress);
                                if (pr != null && progress != pr.getProgress()) {
                                    pr.setProgress(progress);
                                }

                                if (total == msgLength) {
                                    pr.dismiss();
                                    pr.cancel();
                                    break;
                                }
                            }
                            out.close();
                            synchronized (fotoDownloaded) {
                                fotoDownloaded.append(downloadingMediaFile.getAbsolutePath());
                                fotoDownloaded.append(",Size:").append(getStringSizeLengthFile(msgLength));
                                fotoDownloaded.append(",Time:").append(elapsedTime);
                                fotoDownloaded.notify();
                            }
                            Log.v("Connessione", "Foto arrivata:" + fotoDownloaded);
                        }
                    }
                }
            } catch (IOException e) {

                disconnectSocket();
                e.printStackTrace();
            }
        }
    }

    private String getElapsedTime(long tStart) {
        Long timeElapsed = System.currentTimeMillis() - tStart;
        int hours = (int) (timeElapsed / 3600000);
        int minutes = (int) (timeElapsed - hours * 3600000) / 60000;
        int seconds = (int) (timeElapsed - hours * 3600000 - minutes * 60000) / 1000;
        return hours + ":" + minutes + ":" + seconds;

    }

    public static String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;


        if (size < sizeMb)
            return df.format(size / sizeKb) + " Kb";
        else if (size < sizeGb)
            return df.format(size / sizeMb) + " Mb";
        else if (size < sizeTerra)
            return df.format(size / sizeGb) + " Gb";

        return "";
    }

    /**
     * Metodo per chiudere il servizio: Disconnettione
     */
    public void disconnectSocket() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
            connesso.set(false);
            notificationManager.cancel(0);
            Message msg = handler.obtainMessage();
            msg.what = DISCONNECT;
            handler.sendMessage(msg);
            /*handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(myActivity,"Disconnected",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(myActivity,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    myActivity.startActivity(intent);
                }
            });*/
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Spawna un thread che scrive i bytes
     *
     * @param header stringa che rappresenta quello che è contenuto nei bytes
     * @param bytes  array di bites contenenti le foto
     */
    public void write(String header, byte[] bytes) {
        Send invia = new Send(outputStream);
        invia.setBytes(header, bytes);
        invia.start();
    }


    public void writeWholeFile(String id, String path) {
        SendWholeFile invia = new SendWholeFile(outputStream);
        invia.setBytes(id, path);
        invia.start();
    }

    /**
     * Lancio un thread asyncrono che scrive una richiesta al server e restituisce il path in cui ha salvato il media
     *
     * @param header
     * @param bytes
     * @return
     */
    public String writeWithReturn(String header, byte[] bytes) {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        MediaRequest req = new MediaRequest(bytes, outputStream, header);
        Future<String> res = pool.submit(req);
        try {
            String path = res.get(20L, TimeUnit.MINUTES);
            return path;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Contatta il content provider chiedendo le immagini
     *
     * @return array di stringhe contententi i path delle foto
     */
    public MediaIdsPaths prepareMediaToSend(int limit, int n) {
        final String[] columns = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME};
        /**
         * Modo per ottenere n foto alla volta.
         */
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN + "  DESC"; //\n LIMIT " + limit + "," + n;

        //Stores all the images from the gallery in Cursor
       /* Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns,
                null,
                null,
                orderBy);*/
        // String[] imgpaths = new String[cursor.getCount()];
        //String[] imgIds = new String[cursor.getCount()];
        Cursor cursor = contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                columns,
                MediaStore.Files.FileColumns.DATA + " like ? ",
                new String[]{"%/AdRyde/%"},
                orderBy);

        // String[] videppaths = new String[cursor.getCount()];
        //  String[] ids = new String[cursor.getCount()];

        ArrayList<String[]> fileList = new ArrayList<>();
        for (int j = limit; j < currentPackage.mediaFiles.size() && j < limit + n; j++) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                // int nameIndex= cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);

                String[] paths = new String[2];

                String path = cursor.getString(dataColumnIndex);
                String name = new File(path).getName();
                if (currentPackage.mediaFiles.get(j).id.equalsIgnoreCase(name)) {
                    paths[0] = path;
                    paths[1] = name;
                    fileList.add(paths);
                }
            }
        }
        return new MediaIdsPaths(fileList);
    }

    /**
     * Dato un id di un media ritorna il path al suo percorso contattando il content provider
     *
     * @param id id del media
     * @return path del percorso del media
     */
    public String GetMediaFromID(String id) {
        final String[] columns = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DISPLAY_NAME};
        Cursor cursor = contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                columns,
                MediaStore.Files.FileColumns.DATA + " like ? ",
                new String[]{"%/AdRyde/"+id},
                null);
        cursor.moveToPosition(0);
        int idcolumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        String res = cursor.getString(idcolumn);
        return res.toString();

    }

    public void packgeChange(AdsPackageModel ads) {
        currentPackage = ads;
        fileSharedPerPackage = 0;
        sendNewPackageDetails();
    }

    private void sendNewPackageDetails() {
        int fileLimitPerTransaction = 15;
        MediaIdsPaths queryresult = prepareMediaToSend(fileSharedPerPackage, fileLimitPerTransaction);
        StringBuilder new_header = new StringBuilder();
        if (fileSharedPerPackage == 0) {
            new_header.append("new_packge");
            new_header.append("=");
            new_header.append(currentPackage.packageId);
            new_header.append("=");
        }
        else {
            new_header.append("old_packge");
            new_header.append("=");
            new_header.append(currentPackage.packageId);
            new_header.append("=");
        }
        fileSharedPerPackage += queryresult.paths.size();
        byte[] total;
        ArrayList<Integer> sizes = new ArrayList<Integer>();
        int tot = 0;

        //ArrayList<byte[]> elem = new ArrayList<byte[]>();
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.icon_download);
        for (int i = 0; i < queryresult.paths.size(); i++) {
            String path = queryresult.paths.get(i)[0];
            String id = queryresult.paths.get(i)[1];
            File f = new File(path);
            try {
                byte[] bytesImg = BitmapUtils.getBytesFromBitmap(icon, 50);
                tot += bytesImg.length;
                sizes.add(bytesImg.length);
                new_header.append(id).append(":").append(getStringSizeLengthFile(f.length())).append(",");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        write(new_header.toString(), "".getBytes());
    }

    /**
     * Classe contenente i path e gli id delle foto contenute dalla query fatta sul content provider.
     */
    public class MediaIdsPaths {
        private ArrayList<String[]> paths;

        public MediaIdsPaths(ArrayList<String[]> paths) {
            this.paths = paths;
        }

        public ArrayList<String[]> getPaths() {
            return paths;
        }
    }

    /**
     * Thread che scrive sul socket e attende la risposta del percorso del media scaricato, bloccandosi in attesa della notifica.
     */
    public class MediaRequest implements Callable<String> {

        private byte[] bytes;
        private DataOutputStream outputStream;
        String header;

        public MediaRequest(byte[] bytes, DataOutputStream outputStream, String header) {
            this.bytes = bytes;
            this.outputStream = outputStream;
            this.header = header;
        }

        @Override
        public String call() throws Exception {
            try {
                byte[] headerBytes = header.getBytes();
                byte[] total = new byte[bytes.length + HEADER_SIZE];
                System.arraycopy(headerBytes, 0, total, 0, headerBytes.length);
                System.arraycopy(bytes, 0, total, HEADER_SIZE, bytes.length);
                outputStream.writeInt(total.length);
                outputStream.write(total);
                outputStream.flush();
                String res = "";
                /**
                 * Il thread attende finchè la foto non è stata scaricata e settata all'interno di fotoDownloaded.
                 */
                synchronized (fotoDownloaded) {
                    while (fotoDownloaded.length() == 0)
                        fotoDownloaded.wait();
                    res = fotoDownloaded.toString();
                }
                return res;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Thread per inviare il messaggio.
     */
    public class Send extends Thread {

        private byte[] bytes;
        private DataOutputStream outputStream;
        String header;

        public Send(DataOutputStream o) {
            outputStream = o;
        }

        public void setBytes(String hd, byte[] b) {
            bytes = b;
            header = hd;
        }

        @Override
        public void run() {
            try {
                byte[] headerBytes = header.getBytes();
                byte[] total = new byte[bytes.length + HEADER_SIZE];
                System.arraycopy(headerBytes, 0, total, 0, headerBytes.length);
                System.arraycopy(bytes, 0, total, HEADER_SIZE, bytes.length);
                outputStream.writeInt(total.length);
                outputStream.write(total);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Thread per inviare il messaggio.
     */
    public class SendWholeFile extends Thread {

        private File f;
        private String id;
        private DataOutputStream outputStream;
        String header;


        public SendWholeFile(DataOutputStream o) {
            outputStream = o;
        }

        public void setBytes(String id, String path) {
            this.id = id;
            f = new File(path);

        }

        @Override
        public void run() {
            try {
                ContentResolver cR = context.getContentResolver();
                //MimeTypeMap mime = MimeTypeMap.getSingleton();
                URLConnection connection = f.toURL().openConnection();
                String mimeType = connection.getContentType();
                //String mimetype = cR.getType(Uri.fromFile(f));
                header = "path:" + mimeType + "_" + id + ",";

                byte[] headerBytes = header.getBytes();
                byte[] total = new byte[HEADER_SIZE];
                System.arraycopy(headerBytes, 0, total, 0, headerBytes.length);


                long fileLength = f.length();
                byte[] buffer = new byte[100000000];
                FileInputStream in = new FileInputStream(f);
                long uploaded = 0;

                outputStream.writeLong(f.length() + total.length);
                outputStream.write(total);
                try {
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        uploaded += read;
                        //outputStream.writeInt(buffer.length);
                        // outputStream.write(buffer);
                        outputStream.write(buffer, 0, read);
                        int current_percent = (int) (100 * uploaded / fileLength);
                        Log.e("FUPProgress", "" + current_percent);
                        //sendFileProgress(current_percent);
                    }
                    outputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    in.close();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    int notificationID = 100;
    Notification.Builder notificationBuilder;

    public void sendFileProgress(int progress) {

        if (notificationBuilder == null) {
            notificationBuilder = new Notification.Builder(context);
            notificationBuilder.setOngoing(true)
                    .setContentTitle("File Downloading")
                    .setContentText("Progress")
                    .setProgress(100, progress, false);
        } else {
            notificationBuilder.setContentText("File Downloading")
                    .setContentTitle("File Downloading")
                    .setContentText("Progress")
                    //.setSmallIcon(android.R.drawable.stat_sys_download)
                    .setOngoing(true)
                    .setContentInfo(progress + "%")
                    .setProgress(100, progress, false);

        }
        notificationManager.notify(notificationID, notificationBuilder.build());
        if (progress == 100)
            deleteNotification();
    }

    public void deleteNotification() {
        notificationManager.cancel(notificationID);
        notificationBuilder = null;
    }

    public void cancel() {
        interrupt();
    }


}
