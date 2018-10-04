package com.example.kimea.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatMainActivity extends AppCompatActivity implements View.OnClickListener{
    JSONObject data = new JSONObject();
    private Socket mSocket;
    private RecyclerView fRecyclerView;
    private LinearLayoutManager fLayoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<GetFriendListItem> items;
    JSONArray fList;
    JSONObject pList;
    ArrayList userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatmain);

        ChatApplication app = (ChatApplication) getApplication();

        mSocket = app.getSocket();
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

    
        fRecyclerView = findViewById(R.id.friend_list);
        fLayoutManager = new LinearLayoutManager(this);
        fRecyclerView.setLayoutManager(fLayoutManager);

        fLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        items = new ArrayList<>();
        adapter = new FriendListAdapter(items);
        fRecyclerView.setAdapter(adapter);

        mSocket.on("friendList",listener);

        /*
        try {
            data.put("email", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("sendUser",data);
        */
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            /*
            case R.id.friendList:
                Intent intent = new Intent(ChatMainActivity.this,ProfileSetActivity.class);
                startActivity(intent);
                break;

            case R.id.goChat:
                intent = new Intent(ChatMainActivity.this,ChatRoomActivity.class);
                startActivity(intent);
                    break;
            case R.id.goAdd:
                intent = new Intent(ChatMainActivity.this,ViewPagerActivity.class);
                startActivity(intent);
                break;
            case R.id.goView:
                intent = new Intent(ChatMainActivity.this,AddFriendActivity.class);
                startActivity(intent);

                break;
                */
        }

    }
     public void addProfile(String setuUserImg,String setUserNickname){
        items.add(new GetFriendListItem(setuUserImg,setUserNickname));
        adapter.notifyItemInserted(items.size());
    }

    private Emitter.Listener listener2 = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pList = (JSONObject) args[0];

                    Log.i("pList", pList.toString());
                    String setUserImg ="";
                    String setUserNickname ="";

                    try {

                            setUserImg = pList.getString("u_pf_img");
                            setUserNickname = pList.getString("u_nickname");
                            Log.i("nickName&img", setUserNickname+", "+setUserImg);




                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    addProfile(setUserImg,setUserNickname);
                }
            });
        }
    };
        private Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fList = (JSONArray)args[0];

                    userList = new ArrayList();


                    try {
                        for(int i = 0; i<fList.length(); i++){
                            Log.i("listener1",fList.toString());
                            JSONObject jo = fList.getJSONObject(i);
                            JSONObject data = new JSONObject();
                            Log.i("fListArray", jo.toString());
                            userList.add(fList.getJSONObject(i).getString("f_email"));
                            Log.i("msg",fList.getJSONObject(i).getString("f_email"));
                            data.put("u_email",userList.get(i).toString());
                            data.put("index",i);
                            mSocket.emit("sendFriend",data);

                        }
                        mSocket.on("sendFriend", listener2);

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
        }
    };
}
