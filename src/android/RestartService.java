package com.service.backgroundcall;

import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Log;
import android.app.PendingIntent;
import android.app.AlarmManager;
import com.service.backgroundcall.service;
import android.content.SharedPreferences;

/**
 * Created by tuan on 2018/08/15 Restart service when device reboot
 */
public class RestartService extends BroadcastReceiver {
    private static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        Boolean isLogout = prefs.getBoolean("isLogout", true);
        Log.d("TAG_BOOT_BROADCAST_RECEIVER_para", Boolean.toString(isLogout));
        if (!isLogout) {
            String action = intent.getAction();
            if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
                Log.d(TAG_BOOT_BROADCAST_RECEIVER, "start service");
                // startServiceDirectly(context);
                Intent startServiceIntent = new Intent(context, service.class);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(startServiceIntent);
                } else {
                    context.startService(startServiceIntent);
                }
            }
        }
    }

    /*
     * Start RunAfterBootService service directly and invoke the service every 10
     * seconds.
     */
    private void startServiceDirectly(Context context) {
        try {
            while (true) {

                // This intent is used to start background service. The same service will be
                // invoked for each invoke in the loop.
                Intent startServiceIntent = new Intent(context, service.class);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                    context.startForegroundService(startServiceIntent);
                } else {
                    context.startService(startServiceIntent);
                }
                // Current thread will sleep one second.
                Thread.sleep(1000);
            }
        } catch (InterruptedException ex) {
            Log.e(TAG_BOOT_BROADCAST_RECEIVER, ex.getMessage(), ex);
        }
    }

}