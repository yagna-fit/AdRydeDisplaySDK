package com.adryde.driver;

import android.content.ContentResolver;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import android.os.Handler;

import androidx.core.app.NotificationManagerCompat;

/**
 * Classe che serve per aprire la connessione accettando la richiesta dal client.
 */
public class ServerClass extends Thread {

    Socket socket;
    ServerSocket serverSocket;
    private Handler handler;
    private AtomicBoolean connesso;
    private String chache;
    private RecyclerViewAdapter stron;
    private ContentResolver contentResolver;
    private NotificationManagerCompat notificationManager;
   private Context context;

    public ServerClass(Handler hand, AtomicBoolean b, String cha, RecyclerViewAdapter str, ContentResolver cont,NotificationManagerCompat not,Context contx)
    {
        handler=hand;
        connesso=b;
        contentResolver=cont;
        stron=str;
        chache=cha;
        notificationManager=not;
        context=contx;
    }


    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(8888);
            socket = serverSocket.accept();
            connesso.set(true);
            notificationManager.notify(0,MyNotification.getServiceNotification(context));
            handler.post(() -> Toast.makeText(context, "Service ready", Toast.LENGTH_LONG).show());

           /* if(App.getInstance().sendReceive== null) {
                App.getInstance().sendReceive = new SendReceive(socket,15,connesso,chache,stron,contentResolver,notificationManager,handler, context);
            }
            if( App.getInstance().sendReceive.getState() ==  Thread.State.NEW) {
                App.getInstance().sendReceive .start();
            }*/

            SendReceive sendReceive = new SendReceive(socket,15,connesso,chache,stron,contentResolver,notificationManager,handler, context);
            sendReceive .start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
