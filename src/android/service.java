package com.sinch.apptoappcall;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import io.socket.client.IO;
import io.socket.client.Socket;
import android.media.AudioManager;
import android.view.WindowManager;
import android.os.PowerManager;
import io.socket.emitter.Emitter;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

public class service extends Service {

    private Socket mSocket;
    private static final String URL = "http://192.168.77.39:3000";
    public service() {
}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        try {
            mSocket = IO.socket(URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mSocket.connect();
        if (mSocket.connected()){
            Toast.makeText(getApplicationContext(), "Connected!!",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(), "not Connect!",Toast.LENGTH_SHORT).show();
        }
        mSocket.emit("join","dungna1");
        mSocket.on("message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String message = "chay app tu call comming detected";
                mSocket.emit("join",message);
//                Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
                Intent dialogIntent = new Intent(service.this, this.cordova.getActivity().getClass().class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dialogIntent);
            }
        });
        super.onCreate();
        Log.d(TAG_BOOT_EXECUTE_SERVICE, "RunAfterBootService onCreate() method.");



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG_BOOT_EXECUTE_SERVICE, "RunAfterBootService onStartCommand() method.");

        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        super.onTaskRemoved(rootIntent);
//        Intent startServiceIntent = new Intent(this, service.class);
//        this.startService(startServiceIntent);
//
//    }

  
}