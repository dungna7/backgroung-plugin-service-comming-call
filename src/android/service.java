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
import org.json.JSONException;
import org.json.JSONArray;
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
import android.content.SharedPreferences;
import android.os.Handler;
import java.util.Timer;
import java.util.TimerTask;
import android.net.wifi.WifiManager;
import android.content.ContentResolver;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class service extends Service {

    private Socket mSocket;
    private static final String URL = "http://153.127.242.114:3000";
    private static final String MY_PREFS_NAME = "MyPrefsFile";
    private String userInfo;
    private String socketEmit;
    private String socketListen;
    private String socketURL;
    private String userName;
    private String userid;
    private String userCompanycd;
    public static final int notify = 50000; // interval between two services(Here Service run every 5 Minute)
    private Handler mHandler = new Handler(); // run on another Thread to avoid crash
    private Timer mTimer = null;

    public service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        // if (mTimer != null) // Cancel if already existed
        // mTimer.cancel();
        // else
        // mTimer = new Timer(); // recreate new
        // mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);
        ContentResolver cr = getContentResolver();

        int current = Settings.Global.WIFI_SLEEP_POLICY_DEFAULT;
        try {
            current = Settings.System.getInt(cr, Settings.Global.WIFI_SLEEP_POLICY);
        } catch (SettingNotFoundException e) {
            // pass over it, assume default
            Log.w("WifiLockService.LOG_TAG", "Setting could not be read, " + "assuming WIFI_SLEEP_POLICY_DEFAULT");
        }

        if (current == Settings.Global.WIFI_SLEEP_POLICY_DEFAULT) {
            Settings.System.putInt(getContentResolver(), Settings.Global.WIFI_SLEEP_POLICY,
                    Settings.Global.WIFI_SLEEP_POLICY_NEVER);
        } else {
            Log.i("WifiLockService.LOG_TAG", "Changing Wifi sleep policy to DEFAULT");
        }
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        this.userName = prefs.getString("userName", "");
        this.userid = prefs.getString("userid", "");
        this.userCompanycd = prefs.getString("userCompanycd", "");
        this.userInfo = prefs.getString("userInfo", "");
        this.socketURL = prefs.getString("url", URL);
        this.socketEmit = prefs.getString("emitChanel", "voicechat:before_call");
        this.socketListen = prefs.getString("listenChanel", "voicechat:receiveCall");
        try {
            this.mSocket = IO.socket(this.socketURL);
            this.mSocket.connect();
            this.mSocket.emit(socketEmit);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        listenService();
        super.onCreate();
        startForeground(1, new Notification.Builder(this).build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // listenService();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSocket.emit(socketEmit);
                }
            });
        }
    }

    private void listenService() {
        this.mSocket.on("voicechat:beforeCall", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                mSocket.emit(socketEmit);
                String tourguiders = "NULL";
                String companycd = "";
                try {
                    tourguiders = data.getJSONObject("users").getString("tourguiders");
                    companycd = data.getJSONObject("users").getString("companyofGroup");
                } catch (Exception e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                }
                if (!tourguiders.equalsIgnoreCase("NULL")) {
                    Boolean isInGroup = false;
                    if (userCompanycd.equalsIgnoreCase(companycd)) {
                        try {
                            JSONArray arr = data.getJSONObject("users").getJSONArray("tourguiders");
                            for (int i = 0; i < arr.length(); i++) {
                                if (userName.equalsIgnoreCase(arr.getJSONObject(i).getString("nickname"))
                                        && userid.equalsIgnoreCase(arr.getJSONObject(i).getString("id"))) {
                                    isInGroup = true;
                                }
                            }
                        } catch (JSONException e) {
                            // some exception handler code.
                            Log.e("MYAPP", "unexpected JSON exception", e);
                        }
                    }
                    // Check if the Device is Locked Or Not
                    if (isInGroup) {
                        KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                        boolean isPhoneLocked = myKM.inKeyguardRestrictedInputMode();
                        // Awake Device
                        PowerManager powerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
                        PowerManager.WakeLock wakeLock = powerManager
                                .newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
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
                }
            }
        });

    }
}
