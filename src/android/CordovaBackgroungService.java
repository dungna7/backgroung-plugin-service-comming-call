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
/**
 * This class echoes a string called from JavaScript.
 */
public class CordovaBackgroungService extends CordovaPlugin {
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }
    @Override
    protected void pluginInitialize() {
        service.addWindowFlags(this.cordova.getActivity());
    }
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("runService")) {
            String message = args.getString(0);
            this.runService(message, callbackContext);
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
        return false;
    }

    private void runService(String message, CallbackContext callbackContext) {
		Intent intent = new Intent(this.cordova.getActivity(), service.class);  
        // if(message != null && message.length() > 0) intent.putExtra("data", message);
        this.cordova.getActivity().startService(intent);
        callbackContext.success(message);
    }
    private void getCallerInfo(String message, CallbackContext callbackContext) {
        Bundle extras = ((CordovaActivity)this.cordova.getActivity()).getIntent().getExtras();
        if(extras != null){
            String serviceCallInfo = (String) extras.get("serviceCallInfo");
            Log.d("RECEIVER_serviceCallInfo", serviceCallInfo);
            callbackContext.success(serviceCallInfo);
        }
        callbackContext.success(message);
    }
    private void mute(String message, CallbackContext callbackContext) {
        AudioManager audioManager = (AudioManager) this.cordova.getActivity().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMicrophoneMute(true);
        callbackContext.success(message);
    }

    private void unmute(String message, CallbackContext callbackContext) {
        AudioManager audioManager = (AudioManager) this.cordova.getActivity().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMicrophoneMute(false);
        callbackContext.success(message);
    }

    private void speakerOn(String message, CallbackContext callbackContext) {
        AudioManager audioManager = (AudioManager) this.cordova.getActivity().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);
        callbackContext.success(message);
    }

    private void speakerOff(String message, CallbackContext callbackContext) {
        AudioManager audioManager = (AudioManager) this.cordova.getActivity().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
        callbackContext.success(message);
    }
}
