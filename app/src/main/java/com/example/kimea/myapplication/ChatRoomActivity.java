package com.example.kimea.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    String email,result;
    SQLiteDatabase db;
    DBHelper helper =  new DBHelper(ChatRoomActivity.this, "chat.db",null,1);
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        msgInput = findViewById(R.id.message_input);


        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        String[] array = email.split("@");
        String ss = array[1];
        String[] ary2 = ss.split("\\.");
        // String result = array[0]+array2[0]+array2[1];
       // Log.i("result3",ary2[0]);
        result = array[0]+ary2[0]+ary2[1];
        db = helper.getWritableDatabase();
       // db.execSQL("drop table '"+result+"'");
        //Log.i("result4",result);
        try {
            SQLiteDatabase database = helper.getReadableDatabase();
            String sql = "select * from '" + result + "'";
            Cursor cursor2 = database.rawQuery(sql, null);
            if (cursor2.equals(null)||cursor2==null){
               // db = helper.getWritableDatabase();
               // db.execSQL("create table '"+result+"'(Chatseq integer primary key autoincrement, ChatId text, ChatText text);");
            }else{
                Log.i("else","else");
            }
        }catch (Exception e){
            db = helper.getWritableDatabase();
            db.execSQL("create table '"+result+"'(Chatseq integer primary key autoincrement, ChatId text, ChatText text);");
            Log.i("create","create");
        }


        mRecyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        items = new ArrayList();

        // RecyclerView를 위해 CustomAdapter를 사용합니다.
        mAdapter = new ChatAdapter(items);
        mRecyclerView.setAdapter(mAdapter);
        // ArrayList 에 Item 객체(데이터) 넣기

        SQLiteDatabase database = helper.getReadableDatabase();
        String sql = "select * from '"+result+"'";
        Cursor cursor = database.rawQuery(sql,null);
        Log.i("select","select");
        while(cursor.moveToNext()){
            int seq = cursor.getInt(0);
            String id = cursor.getString(1);
            String text = cursor.getString(2);
            Log.i("text",text);
            if (!id.equals(null)) {
                items.add(new GetMessageItem(text, id));
            }
        }

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
                items.add(new GetMessageItem(msgInput.getText().toString(),"me"));
                mAdapter.notifyItemInserted(items.size());
                mSocket.emit("sendMsg",msgData);

                insert("me",msgInput.getText().toString());
                msgInput.setText("");

                break;
        }

    }

    public void addMsg(String setName,String setMsg){
       items.add(new GetMessageItem(setMsg,setName));
        mAdapter.notifyItemInserted(items.size());
        insert(setName,setMsg);
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
    public void insert(String id,String text) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤

        // 데이터의 삽입은 put을 이용한다.
        values.put("ChatId", id);
        values.put("ChatText",text);
        db.insert("'"+result+"'", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
        Log.i("insert","insert");
        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.

    }
}
