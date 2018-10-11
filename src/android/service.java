package com.service.backgroundcall;

import android.os.Handler;
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
import android.content.Context;
import android.app.KeyguardManager;
import android.app.Notification;
import static android.view.WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON;
import static android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class service extends Service {

    private Socket mSocket;
    private static final String URL = "http://153.127.242.114:3000";
    private static final String socketEmit = "voicechat:before_call";
    private static final String socketListen = "voicechat:receiveCall";

    public service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, new Notification.Builder(this).build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = IO.socket(URL);
                    Log.d("RECEIVER_serviceCall", "beforce call 10 s");
                    mSocket.connect();
                    mSocket.emit(socketEmit);
                    Log.d("RECEIVER_serviceCall", "after call 10 s");
                    mSocket.on(socketListen, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            JSONObject data = (JSONObject) args[0];
                            // Check if the Device is Locked Or Not
                            KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                            boolean isPhoneLocked = myKM.inKeyguardRestrictedInputMode();
                            // Awake Device
                            PowerManager powerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
                            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                                    PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
                            int level = PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
                            wakeLock = powerManager.newWakeLock(level, "TAG");
                            wakeLock.setReferenceCounted(false);
                            wakeLock.acquire();
                            // start app
                            Intent dialogIntent = new Intent(service.this,
                                    nisshin.ComeEchat.PrototypeVersion.MainActivity.class);
                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            dialogIntent.putExtra("serviceCallInfo", data.toString());
                            dialogIntent.putExtra("serviceScrennInfo", isPhoneLocked);
                            startActivity(dialogIntent);
                        }
                    });
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 10000);
        // If we get killed, after returning from here, restart
        return START_STICKY;
        // return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
