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
        // String socketEmit = "voicechat:before_call";
        // String socketListen = "voicechat:receiveCall";
        // if(intent.getStringExtra("data") != null &&  intent.getStringExtra("data") != ""){
        //     String data = intent.getStringExtra("data");
        //     String[] socketParam = data.split(",");
        //     if(socketParam[0] != null) socketURL =  socketParam[0];
        //     if(socketParam[1] != null) socketEmit = socketParam[1];
        //     if(socketParam[2] != null) socketListen =  socketParam[2];
        // }
        try {
            mSocket = IO.socket(URL);
            mSocket.connect();
            Log.d("URL", socketURL);
            // Log.d("socketEmit", socketEmit);
            // Log.d("socketListen", socketListen);
            mSocket.emit("voicechat:before_call");
            mSocket.on("voicechat:receiveCall", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject data = (JSONObject) args[0];
                    Log.d("call_data", data.toString());
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
}
