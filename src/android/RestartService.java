package com.service.backgroundcall;

import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Log;
import android.app.PendingIntent;
import android.app.AlarmManager;
/**
 * Created by tuan on 2018/08/15.
 */

public class RestartService extends BroadcastReceiver {


    private static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(Intent.ACTION_BOOT_COMPLETED.equals(action))
        {
            startServiceDirectly(context);
        }
    }

    /* Start RunAfterBootService service directly and invoke the service every 10 seconds. */
    private void startServiceDirectly(Context context)
    {
        try {
            while (true) {

                // This intent is used to start background service. The same service will be invoked for each invoke in the loop.
                Intent startServiceIntent = new Intent(context, service.class);
                context.startService(startServiceIntent);

                // Current thread will sleep one second.
                Thread.sleep(1000);
            }
        }catch(InterruptedException ex)
        {
            Log.e(TAG_BOOT_BROADCAST_RECEIVER, ex.getMessage(), ex);
        }
    }

}