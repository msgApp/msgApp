package com.example.kimea.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatRoomActivity extends Activity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<GetMessageItem> items;
    JSONObject msg;
    TextView msgInput;
    private Socket mSocket;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        msgInput = findViewById(R.id.message_input);

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        mRecyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        items = new ArrayList();

        // RecyclerView를 위해 CustomAdapter를 사용합니다.
        mAdapter = new ChatAdapter(items);
        mRecyclerView.setAdapter(mAdapter);
        // ArrayList 에 Item 객체(데이터) 넣기

        mSocket.on("message",listener);

    }
    @Override
    public void onClick(View v){
        switch (v.getId()){

            case R.id.send_button:
                JSONObject msgData = new JSONObject();
                try{
                    msgData.put("message",msgInput.getText().toString());
                    Log.i("inputMsg",msgInput.getText().toString());
                    msgData.put("u_email",email);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                items.add(new GetMessageItem(msgInput.getText().toString(),"user"));
                mAdapter.notifyItemInserted(items.size());
                mSocket.emit("sendMsg",msgData);

                break;
        }

    }

    public void addMsg(String setName,String setMsg){
       items.add(new GetMessageItem(setMsg,setName));
        mAdapter.notifyItemInserted(items.size());
        scrollToBottom();
    }
    private void scrollToBottom() {
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }
    private Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msg = (JSONObject)args[0];
                    Iterator i = msg.keys();
                    ArrayList<String> keys = null;
                    ArrayList<String> values = null;
                    String setMsg ="";
                    String setName ="";

                    try {
                        setMsg =  msg.getString("message");
                        setName = msg.getString("nickName");
                        Log.i("msg",setMsg);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    addMsg(setName,setMsg);
                }
            });
        }
    };
    @Override
    public void onDestroy(){
        super.onDestroy();

    }
}
