package com.example.kimea.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;

public class ChatMainActivity extends AppCompatActivity implements View.OnClickListener{
    JSONObject data = new JSONObject();
    private Socket mSocket;
    /*
    SocketService socketservice;
    boolean isService = false;


    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, SocketService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
// 서비스와 연결되었을 때 호출되는 메서드
// 서비스 객체를 전역변수로 저장
            SocketService.MyBinder mb = (SocketService.MyBinder) service;
            socketservice = mb.getService(); // 서비스가 제공하는 메소드 호출하여
// 서비스쪽 객체를 전달받을수 있슴
            isService = true;
            Log.i("true",String.valueOf(isService));
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
// 서비스와 연결이 끊겼을 때 호출되는 메서드
            isService = false;
            Log.i("true",String.valueOf(isService));
        }
    };
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatmain);

        ChatApplication app = (ChatApplication) getApplication();

        mSocket = app.getSocket();
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        Log.i("id",id);


        try {
            data.put("email", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("sendUser",data);
        /*
        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO

                if(isService) {
                    socketservice.sendUser(data);
                    Toast.makeText(ChatMainActivity.this,"성공",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ChatMainActivity.this,"실패",Toast.LENGTH_SHORT).show();
                }
            }
        }, 500);
        */
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.friendList:
                Intent intent = new Intent(ChatMainActivity.this,AddFriendActivity.class);
                startActivity(intent);
                break;

            case R.id.goChat:
                intent = new Intent(ChatMainActivity.this,ChatRoomActivity.class);
                startActivity(intent);
                    break;
        }

    }
}
