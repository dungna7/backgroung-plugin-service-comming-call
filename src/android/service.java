package com.service.backgroundcall;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.app.Activity;
import io.socket.client.IO;
import io.socket.client.Socket;
import android.media.AudioManager;
import android.view.Window;
import android.view.WindowManager;
import android.os.PowerManager;
import io.socket.emitter.Emitter;
import org.json.JSONObject;
import android.os.Bundle;
import java.net.URISyntaxException;

import static android.view.WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON;
import static android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;

public class service extends Service {

    private Socket mSocket;
    private static final String URL = "http://153.127.242.114:3000";
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
        String socketURL =  URL;
        String socketEmit = "voicechat:before_call";
        String socketListen = "voicechat:receiveCall";
        // Bundle extras = intent.getExtras();
        // if(extras != null) {
        //     String data = intent.get("data");
        //     String[] socketParam = data.split(",");
        //     if(socketParam[0] != null) socketURL =  socketParam[0];
        //     if(socketParam[1] != null) socketEmit = socketParam[1];
        //     if(socketParam[2] != null) socketListen =  socketParam[2];
        // }
        try {
            mSocket = IO.socket(socketURL);
            mSocket.connect();
            Log.d("URL", socketURL);
            // Log.d("socketEmit", socketEmit);
            // Log.d("socketListen", socketListen);
            mSocket.emit(socketEmit);
            mSocket.on(socketListen, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject data = (JSONObject) args[0];
                    // String packageName = getPackageName();
                    // Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                    // String className = launchIntent.getComponent().getClassName();
                    // System.out.println("className = " + className);
                    // Class mainact = Class.forName("nisshin.ComeEchat.PrototypeVersion.MainActivity");
                    // Log.d("call_data", className);
                    Intent dialogIntent = new Intent(service.this, nisshin.ComeEchat.PrototypeVersion.MainActivity.class);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogIntent.putExtra("serviceCallInfo",data.toString());

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
    static void addWindowFlags(Activity app) {
        final Window window = app.getWindow();

        app.runOnUiThread(new Runnable() {
            public void run() {
                window.addFlags(
                    FLAG_ALLOW_LOCK_WHILE_SCREEN_ON |
                    FLAG_SHOW_WHEN_LOCKED |
                    FLAG_TURN_SCREEN_ON |
                    FLAG_DISMISS_KEYGUARD
                );
            }
        });
    }
}
