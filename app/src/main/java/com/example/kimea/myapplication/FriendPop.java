package com.example.kimea.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FriendPop extends AppCompatActivity implements FriendPopAdapter.OnSendPop, FriendPopAdapter2.OnSendPop {
    private static final String TAG = "FriendPop";
    private RecyclerView fpRecycler;
    private RecyclerView sFpRecycler;

    private LinearLayoutManager fpLayout;
    private LinearLayoutManager sFpLayout;

    private RecyclerView.Adapter adapter;
    private RecyclerView.Adapter sAdapter;

    private ArrayList<GetFriendPopItem> fpItem;
    private ArrayList<GetFriendPopItem2> sFpItem;
    private SQLiteDatabase db;
    private DBHelper helper = new DBHelper(FriendPop.this);
    private Socket mSocket;
    Cursor cursor;
    String room,roomNick;

    boolean intentCheck = true;
    private ArrayList<String> chatEmail = new ArrayList<>();
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //
        String a="";
        switch (item.getItemId()){
            case R.id.resultButton:
                db = helper.getReadableDatabase();
                String query = "select user from divice";
                Cursor cursor = db.rawQuery(query,null);
                cursor.moveToFirst();
                String user = cursor.getString(0);
                for(int i=0;i<chatEmail.size();i++){
                    if(i== 0){
                        a = user+" "+chatEmail.get(i);
                    }else {
                        a +=" "+chatEmail.get(i);
                    }
                }
                JSONObject s = new JSONObject();
                try {
                    s.put("group", a);
                }catch (Exception e){
                }
                mSocket.emit("createRoom",s);
                mSocket.on("createRoom", listener);


                //mSocket.on("createRoom",listener);
                //this.finish();




        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_friends_pop);
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();

        ActionBar ab = getSupportActionBar();

        ab.setTitle("친구추가");

        fpRecycler = findViewById(R.id.friend_pop_view);
        sFpRecycler = findViewById(R.id.select_friend_pop);

        fpLayout = new LinearLayoutManager(this);
        sFpLayout = new LinearLayoutManager(this);

        fpRecycler.setLayoutManager(fpLayout);
        sFpRecycler.setLayoutManager(sFpLayout);

        fpLayout.setOrientation(LinearLayoutManager.VERTICAL);
        sFpLayout.setOrientation(LinearLayoutManager.HORIZONTAL);

        fpItem = new ArrayList<>();
        sFpItem = new ArrayList<>();

        adapter = new FriendPopAdapter(fpItem, sFpItem, this);
        sAdapter = new FriendPopAdapter2(sFpItem,fpItem,this);

        fpRecycler.setAdapter(adapter);
        sFpRecycler.setAdapter(sAdapter);

        db = helper.getWritableDatabase();
        String sql2 = "select * from friend";
        Cursor cursor = db.rawQuery(sql2,null);
        while(cursor.moveToNext()){
            String email = cursor.getString(0);
            String nick = cursor.getString(1);
            String img = cursor.getString(2);
            // Log.e(TAG,"Cursor" + img);
          //  String text = cursor.getString(3);
            //Log.e(TAG,"Cursor" + text);
            addProfile(img, nick , email);
        }

    }
    private Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    JSONObject jRoom = (JSONObject)args[0];
                    try{
                        room = jRoom.getString("roomname");
                        roomNick = jRoom.getString("groups");
                        Intent intent = new Intent(FriendPop.this, ChatRoomActivity.class);
                        intent.putExtra("roomname", room);
                        intent.putExtra("email","");
                        intent.putExtra("roomNickName", roomNick);
                        Log.i("roomNickName" ,roomNick);
                        if(intentCheck){
                            intentCheck = false;
                            startActivity(intent);
                        }

                        /*ChattingTabFragment ctf = new ChattingTabFragment();

                        ctf.addProfile("","","","",room);*/


                    }catch (Exception e){

                    }
                }
            });
            finish();
        }
    };
    public void end(){
        this.finish();
    }
    public void addProfile(String setUserImg,String setUserNickname, String setEmail){
        fpItem.add(new GetFriendPopItem(setUserImg, setUserNickname, setEmail));
        adapter.notifyDataSetChanged ();
    }
    public void listdel(String email){
        int totalSize = chatEmail.size();
        for (int i = 0; i< totalSize; i++ ){
            if(chatEmail.get(i).equals(email)){
                chatEmail.remove(i);
               break;
            }
        }
        //mSocket.emit("group",ja);
    }
    public void listAdd(String email){
        chatEmail.add(email);
        int totalSize = chatEmail.size();
        for (int i = 0; i< totalSize; i++ ){
        }
    }
    public void addSelectList(String img, String email, String nick){
        sFpRecycler.setVisibility(View.VISIBLE);
        sFpItem.add(new GetFriendPopItem2(img,nick,email));
        sAdapter.notifyDataSetChanged();
    }
    public void refresh(){
        adapter.notifyDataSetChanged();
        sAdapter.notifyDataSetChanged();
    }
    public void setVisibility(){
        sFpRecycler.setVisibility(View.GONE);

    }
}
