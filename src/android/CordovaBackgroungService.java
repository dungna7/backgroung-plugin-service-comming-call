package com.service.backgroundcall;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import android.view.WindowManager;
import android.os.PowerManager;
import android.content.Intent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.service.backgroundcall.service;
import android.util.Log;
/**
 * This class echoes a string called from JavaScript.
 */
public class CordovaBackgroungService extends CordovaPlugin {
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("runService")) {
            String message = args.getString(0);
             Log.d("BROADCAST_RECEIVER1", message);
            this.runService(message, callbackContext);
            return true;
        }
        return false;
    }

    private void runService(String message, CallbackContext callbackContext) {
        Log.d("BROADCAST_RECEIVER", message);
		Intent intent = new Intent(this.cordova.getActivity(), service.class);  
        intent.putExtra("data", "myValue");
		this.cordova.getActivity().startService(intent);
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

}
