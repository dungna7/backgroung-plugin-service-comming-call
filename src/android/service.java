package com.service.backgroundcall;

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
    private static final String URL = "http://153.127.242.114:3000";
    private Socket mSocket;
    public service() {
}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	try {
            mSocket = IO.socket(URL);
            mSocket.connect();
            // mSocket.emit("join","dungna2");
            // Log.d("MY_TAG", "start socketio listener");
            // mSocket = IO.socket(URL);
            // mSocket.connect();
            Log.d("MY_TAG", "start socketio listener 11");
            mSocket.emit('voicechat:before_call');
            mSocket.on("voicechat:receiveCall", new Emitter.Listener() {
            // mSocket.on("message", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //JSONObject data = (JSONObject) args[0];
                    //Log.d("MY_TAG", data);
                    String message = "chay app tu call comming detected";
                    // mSocket.emit("join",message);
                    Intent dialogIntent = new Intent(service.this, nisshin.ComeEchat.PrototypeVersion.MainActivity.class);
                    dialogIntent.putExtra("data","123456");
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);
                }
            });
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        
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