package com.service.backgroundcall;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaWebView;
import android.view.WindowManager;
import android.os.PowerManager;
import android.content.Intent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import com.service.backgroundcall.service;
import android.media.AudioManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Build;
import android.view.Window;
import android.app.KeyguardManager;
import android.content.SharedPreferences;
import static android.view.WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON;
import static android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

/**
 * This class echoes a string called from JavaScript.
 */
public class CordovaBackgroungService extends CordovaPlugin {
    public static PowerManager.WakeLock wakeLock;
    public static PowerManager powerManager;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    protected void pluginInitialize() {
        this.addWindowFlags();
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("runService")) {
            String message = args.getString(0);
            this.runService(message, callbackContext);
            return true;
        }
        if (action.equals("stopService")) {
            String message = args.getString(0);
            this.stopService(message, callbackContext);
            return true;
        }
        if (action.equals("getCallerInfo")) {
            String message = args.getString(0);
            this.getCallerInfo(message, callbackContext);
            return true;
        }
        if (action.equals("mute")) {
            String message = args.getString(0);
            this.mute(message, callbackContext);
            return true;
        }
        if (action.equals("unmute")) {
            String message = args.getString(0);
            this.unmute(message, callbackContext);
            return true;
        }
        if (action.equals("speakerOn")) {
            String message = args.getString(0);
            this.speakerOn(message, callbackContext);
            return true;
        }
        if (action.equals("speakerOff")) {
            String message = args.getString(0);
            this.speakerOff(message, callbackContext);
            return true;
        }
        if (action.equals("lockScreen")) {
            this.lockScreen(callbackContext);
            return true;
        }
        if (action.equals("lockStatus")) {
            this.lockStatus(callbackContext);
            return true;
        }
        return false;
    }

    // start service listen on websocket after user login
    private void runService(String message, CallbackContext callbackContext) {
        Intent intent = new Intent(this.cordova.getActivity(), service.class);
        this.cordova.getActivity().startService(intent);
        callbackContext.success(message);
        SharedPreferences.Editor editor = this.cordova.getActivity().getApplicationContext()
                .getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean("isLogout", false);
        editor.apply();
    }

    // stop service ,when user logout
    private void stopService(String message, CallbackContext callbackContext) {
        Intent intent = new Intent(this.cordova.getActivity(), service.class);
        this.cordova.getActivity().stopService(intent);
        callbackContext.success(message);
        SharedPreferences.Editor editor = this.cordova.getActivity().getApplicationContext()
                .getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean("isLogout", true);
        editor.apply();
    }

    // get information from socket chanel voicechat:before_call
    private void getCallerInfo(String message, CallbackContext callbackContext) {
        Bundle extras = ((CordovaActivity) this.cordova.getActivity()).getIntent().getExtras();
        if (extras != null) {
            String serviceCallInfo = (String) extras.get("serviceCallInfo");
            callbackContext.success(serviceCallInfo);
        }
    }

    private void mute(String message, CallbackContext callbackContext) {
        AudioManager audioManager = (AudioManager) this.cordova.getActivity().getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMicrophoneMute(true);
        callbackContext.success(message);
    }

    private void unmute(String message, CallbackContext callbackContext) {
        AudioManager audioManager = (AudioManager) this.cordova.getActivity().getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMicrophoneMute(false);
        callbackContext.success(message);
    }

    private void speakerOn(String message, CallbackContext callbackContext) {
        AudioManager audioManager = (AudioManager) this.cordova.getActivity().getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);
        callbackContext.success(message);
    }

    private void speakerOff(String message, CallbackContext callbackContext) {
        AudioManager audioManager = (AudioManager) this.cordova.getActivity().getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
        callbackContext.success(message);
    }

    // add plag for start app from lockScreen
    private void addWindowFlags() {
        final Window window = this.cordova.getActivity().getWindow();

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                window.addFlags(FLAG_ALLOW_LOCK_WHILE_SCREEN_ON | FLAG_SHOW_WHEN_LOCKED | FLAG_FULLSCREEN
                        | FLAG_DISMISS_KEYGUARD);
                window.addFlags(FLAG_TURN_SCREEN_ON | FLAG_KEEP_SCREEN_ON);
            }
        });
    }

    // check screen status lock or not (call comming from service app isn't running)
    private void lockScreen(CallbackContext callbackContext) {
        Bundle extras = ((CordovaActivity) this.cordova.getActivity()).getIntent().getExtras();
        if (extras != null) {
            Boolean serviceScrennInfo = extras.getBoolean("serviceScrennInfo");
            Log.d("RECEIVER_serviceCallInfo", Boolean.toString(serviceScrennInfo));
            callbackContext.success(Boolean.toString(serviceScrennInfo));
        }
    }

    // check screen status lock or not (call comming when app is running)
    private void lockStatus(CallbackContext callbackContext) {
        KeyguardManager myKM = (KeyguardManager) this.cordova.getActivity().getSystemService(Context.KEYGUARD_SERVICE);
        boolean isPhoneLocked = myKM.inKeyguardRestrictedInputMode();
        Log.d("RECEIVER_lockStatus", Boolean.toString(isPhoneLocked));
        callbackContext.success(Boolean.toString(isPhoneLocked));
    }
}
