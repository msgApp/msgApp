package com.example.kimea.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class SocketService extends Service {
    private Socket mSocket;
    JSONObject msg;
    String resultMsg = "";
    private IBinder mBinder = new MyBinder();

    public int var = 77;

    class MyBinder extends Binder {
        SocketService getService() { // 서비스 객체를 리턴
            return SocketService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        return mBinder;
    }
    @Override
    public void onCreate() {
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        super.onCreate();

        try {
            mSocket = IO.socket("http://192.168.0.53:1300");
            mSocket.connect();

        Log.i("success","ㅇ");
        }catch(URISyntaxException e) {
            e.printStackTrace();
            Log.i("success","ㄴ");
        }

    }

    public void sendUser(JSONObject data){
        Log.i("data", data.toString());
        mSocket.emit("sendUser", data);
    }
    public void sendMsg(JSONObject data){
        mSocket.emit("sendMsg",data);

    }
    public void disconnect(JSONObject data){
        mSocket.emit("disconnet", data);
    }
    public void sendProfile(JSONObject data){
        mSocket.emit("setProfile",data);
    }
    
    public String getMsg(){

        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                msg = (JSONObject)args[0];
                Iterator i = msg.keys();
                ArrayList<String> keys = null;
                ArrayList<String> values = null;

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            resultMsg =  msg.getString("message");
                            Log.i("msg",msg.getString("message"));
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                });
            }
        };
        mSocket.on("message",listener);
        return resultMsg;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행

    }

}
