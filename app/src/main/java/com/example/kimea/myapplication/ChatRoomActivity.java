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
    private static final String TAG = "ChatRoomAvtivity";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<GetMessageItem> items;

    TextView msgInput;
    private Socket mSocket;
    String email,result;
    SQLiteDatabase db;
    DBHelper helper =  new DBHelper(ChatRoomActivity.this);
  //  DBHelper helper2 =  new DBHelper(ChatRoomActivity.this, "token.db",null,1);

    Cursor cur;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        msgInput = findViewById(R.id.message_input);
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        Intent intent = getIntent();
        email = intent.getStringExtra("email");

//        Log.i("상대방 아이디",email);
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

        }catch (Exception e){
            db = helper.getWritableDatabase();
            db.execSQL("create table '"+result+"'(Chatseq integer primary key autoincrement, ChatId text,ChatNickName text, ChatText text,type TEXT);");
            Log.i("ChatDataBaseCreate","create");
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
        Log.i("ChatDataBaseSelect","select");
        while(cursor.moveToNext()){
            int seq = cursor.getInt(0);
            String id = cursor.getString(1);
            String nickname = cursor.getString(2);
            String text = cursor.getString(3);
            String type = cursor.getString(4);
            if (type.equals("0")) {
                addMsg(nickname,text,0);
            }else if(type.equals("1")){
                addMsg(nickname,text,1);
            }
        }

        mSocket.on("message",listener);
        scrollToBottom();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){

            case R.id.send_button:
                String myEmail="";
                SQLiteDatabase database = helper.getReadableDatabase();
                String sql = "select * from divice";
                Cursor cursor2 = database.rawQuery(sql, null);
                while(cursor2.moveToNext()){
                    Log.e(TAG ,cursor2.getString(0));
                    myEmail = cursor2.getString(0);
                }

                JSONObject msgData = new JSONObject();
                JSONObject pushData = new JSONObject();
                try{
                    msgData.put("message",msgInput.getText().toString());
                    Log.i("inputMsg",msgInput.getText().toString());
                    msgData.put("u_email",myEmail);
                    pushData.put("message",msgInput.getText().toString());
                    pushData.put("u_email",myEmail);
                    pushData.put("f_email",email);
                    pushData.put("room",email);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                addMsg("me",msgInput.getText().toString(),1);
                mAdapter.notifyItemInserted(items.size());

                mSocket.emit("sendMsg",msgData);
                //채팅방 목록 테이블에 존재하는지 확인
                db = helper.getWritableDatabase();
                String sql3 = "select userID from oneUser where userId = '"+email+"';"; //where userId = '"+email+"';";
                cur = db.rawQuery(sql3,null);
                if(cur.moveToFirst()) {
                    for (; ; ) {
                        Log.i("table name : ", cur.getString(0));
                        if (!cur.moveToNext())
                            break;
                    }
                }else{
                    insert2(email);
                }
                insert("me","me",msgInput.getText().toString(),"1");
                msgInput.setText("");

                scrollToBottom();
                break;
            case R.id.message_input:
                scrollToBottom();
                break;
        }

    }

    public void addMsg(String setName,String setMsg,int type){
        if (type==0){
            items.add(new GetMessageItem.Builder(GetMessageItem.TYPE_MESSAGE).username(setName).userMessage(setMsg).build());
            //insert(setName,setMsg,"0");
        }else{
            items.add(new GetMessageItem.Builder(GetMessageItem.TYPE_MYMSG).username(setName).userMessage(setMsg).build());
            //insert(setName,setMsg,"1");
        }
        mAdapter.notifyItemInserted(items.size());

        scrollToBottom();
    }
    private void scrollToBottom() {
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }
    //socket 데이터 받아온값 처리
    private Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject msg = (JSONObject)args[0];
                    Iterator i = msg.keys();
                    ArrayList<String> keys = null;
                    ArrayList<String> values = null;
                    Log.i("제이슨",msg.toString());
                    String setName ="";
                    String setNickName="";
                    String setMsg="";
                    try {
                        setMsg =  msg.getString("message");
                        setName = msg.getString("email");
                        setNickName = msg.getString("nickName");
                        Log.i("msg",setMsg);
                        Log.i("닉네이이이이이이이임",setNickName);


                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    //상대방 이메일이 맞으면 테이블 인서트
                    if(email.equals(setName)) {
                        addMsg(setNickName, setMsg,0);
                        insert(setName,setNickName,setMsg,"0");
                   }else{
                        //아니면 맞는 이메일에 인서트
                        Log.i("엘즈","else");
                        Log.i("엘즈",setName);
                        String[] arrayy = setName.split("@");
                        String sss = arrayy[1];
                        String[] ary22 = sss.split("\\.");
                        // String result = array[0]+array2[0]+array2[1];
                        // Log.i("result3",ary2[0]);
                        String result2 = arrayy[0]+ary22[0]+ary22[1];
                        Log.i("엘즈",result);
                        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
                        ContentValues elseValue = new ContentValues();
                        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤

                        // 데이터의 삽입은 put을 이용한다.
                        elseValue.put("ChatId", result2);
                        elseValue.put("ChatText",setMsg);
                        db.insert("'"+result2+"'", null, elseValue); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
                    }
                }
            });
        }
    };
    @Override
    public void onDestroy(){
        super.onDestroy();

    }
    //데이터 삽입
    public void insert(String id,String nickName,String text,String type) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤

        // 데이터의 삽입은 put을 이용한다.
        values.put("ChatId", id);
        values.put("ChatNickName",nickName);
        values.put("ChatText",text);
        values.put("type",type);
        db.insert("'"+result+"'", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
        Log.i("insert","insert");
        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.
    }
    //채팅방 유저 확인 테이블 삽입
    public void insert2(String id) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values2 = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤

        // 데이터의 삽입은 put을 이용한다.
        values2.put("userId", id);
        db.insert("oneUser", null, values2); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
        Log.i("insert","insert");
        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.
        SQLiteDatabase database = helper.getReadableDatabase();
        String sql = "select * from oneUser;";
        Cursor cursor2 = database.rawQuery(sql, null);
        while(cursor2.moveToNext()){
            Log.i("id1",cursor2.getString(0));
            Log.i("id1",cursor2.getString(1));
        }
    }

    //채팅방 목록 갱신시 필요한 데이터
    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        Intent intent = new Intent();
        String rs2 = "";

        db = helper.getWritableDatabase();
        String sql2 ="select ChatText from'"+result+"' where Chatseq = (select max(Chatseq) from '"+result+"');";
        String query = "select user from divice";
        Cursor cur = db.rawQuery(query,null);
        Cursor cur2 = db.rawQuery(sql2,null);
        cur.moveToFirst();
        String email = cur.getString(0);
        JSONObject emailJson = new JSONObject();
        try {
            emailJson.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("outActivity", emailJson);
        while (cur2.moveToNext()){
            rs2=cur2.getString(0);
        }
        intent.putExtra("msg", rs2);
        intent.putExtra("email",email);
        setResult(1, intent);
        Log.i("asdasdasd","asdasdasda");
        finish();
    }
}
