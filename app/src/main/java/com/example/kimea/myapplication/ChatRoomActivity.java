package com.example.kimea.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    String email,result,my_email,roomname,roomNick;
    SQLiteDatabase db;
    Button sendBtn;
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
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        roomname = intent.getStringExtra("roomname");
        roomNick = intent.getStringExtra("roomNickName");
        Log.i("roomNickName2" ,roomNick);
        SharedPreferences pref = getSharedPreferences("chatEmail",MODE_PRIVATE);
        SharedPreferences.Editor emaile = pref.edit();
        emaile.putString("email",roomname);
        emaile.commit();
        Log.e(TAG,"prefEmail "+pref.getString("email",""));
        msgInput = findViewById(R.id.message_input);
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        sendBtn = findViewById(R.id.send_button);
        msgInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (msgInput.getText().toString().replace(" ", "").equals("")){
                    sendBtn.setEnabled(false);
                }else{
                    sendBtn.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (msgInput.getText().toString().replace(" ", "").equals("")){
                    sendBtn.setEnabled(false);
                }else{
                    sendBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (msgInput.getText().toString().replace(" ", "").equals("")){
                    sendBtn.setEnabled(false);
                }else{
                    sendBtn.setEnabled(true);
                }
            }
        });

        try {
            SharedPreferences emailBadge = getSharedPreferences(roomname, MODE_PRIVATE);
            String mailBadge = emailBadge.getString("badge_count", "0");
            int badgeInt = Integer.valueOf(mailBadge);
            /*
            //TODO
            if(badgeInt>0){
                JSONObject intoAct = new JSONObject();
                intoAct.put("roomname",roomname);
                intoAct.put("readYN", "Y");

            }*/
            Log.e(TAG, "mailBadge: " + mailBadge);
            SharedPreferences appBadge = getSharedPreferences("pref", MODE_PRIVATE);
            int apBadge = appBadge.getInt("badge", 0);
            Log.e(TAG, "appBadge: " + apBadge);
            int badgeResult = apBadge - Integer.parseInt(mailBadge);
            Log.e(TAG, "badgeResult: " + badgeResult);

            SharedPreferences.Editor editor = appBadge.edit();
            editor.putInt("badge", badgeResult);
            editor.commit();
            Log.e(TAG, "commit1 ");
            SharedPreferences.Editor editor2 = emailBadge.edit();
            editor2.putString("badge_count", "0");
            editor2.commit();
            Log.e(TAG, "commit2");
            set_badge_alarm(badgeResult);
            Log.e(TAG, "badge_send");
        }catch (Exception e){
            e.printStackTrace();
        }
//        Log.i("상대방 아이디",email);
        String[] array = roomname.split("@");
        String ss = array[1];
        String[] ary2 = ss.split("\\.");
        // String result = array[0]+array2[0]+array2[1];
       // Log.i("result3",ary2[0]);
        result = array[0]+ary2[0]+ary2[1];
        db = helper.getWritableDatabase();
       // db.execSQL("drop table '"+result+"'");
        Log.i("result4",result);
        try {
            SQLiteDatabase database = helper.getReadableDatabase();
            String sql = "select * from '" + result + "'";
            Cursor cursor2 = database.rawQuery(sql, null);
            while(cursor2.moveToNext()){
                Log.e(TAG,"chatId"+cursor2.getString(0));
                Log.e(TAG,"chatnick"+cursor2.getString(1));
                Log.e(TAG,"chattext"+cursor2.getString(2));
            }

        }catch (Exception e){
            db = helper.getWritableDatabase();
            db.execSQL("create table '"+result+"'(Chatseq integer primary key autoincrement, ChatId text,ChatNickName text, ChatText text, ChatRoomNickName text,type TEXT);");
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
            String roomNickname = cursor.getString(4);
            String type = cursor.getString(5);
            try {
                if (nickname.equals("me")) {
                    addMsg(nickname, text, 1);
                } else {
                    addMsg(nickname, text, 0);
                }
            }catch (Exception e){
                    Log.e(TAG,e.toString());

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
                Log.i("ChatRoomAct-myEmail", myEmail);

                JSONObject msgData = new JSONObject();
                JSONObject pushData = new JSONObject();
                try{
                    msgData.put("message",msgInput.getText().toString());
                    Log.i("inputMsg",msgInput.getText().toString());
                    msgData.put("u_email",email);
                    msgData.put("my_email", myEmail);
                    msgData.put("room", roomname);
                    if(email.equals(roomname)){
                        pushData.put("roomname", myEmail);
                        Log.i("roomname is my", myEmail);
                    }else {
                        pushData.put("roomname",roomname);
                        Log.i("roomname is room", roomname);
                    }
                    pushData.put("message",msgInput.getText().toString());
                    pushData.put("u_email",myEmail);
                    pushData.put("f_email",email);
                 //   pushData.put("room",email);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                addMsg("me",msgInput.getText().toString(),1);
                mAdapter.notifyItemInserted(items.size());

                mSocket.emit("pushMsg",pushData);
                mSocket.emit("sendMsg",msgData);
                //채팅방 목록 테이블에 존재하는지 확인
                db = helper.getWritableDatabase();
                String sql3 = "select userID from oneUser where userId = '"+roomname+"';"; //where userId = '"+email+"';";
                cur = db.rawQuery(sql3,null);
                if(cur.moveToFirst()) {
                    for (; ; ) {
                        Log.i("table name : ", cur.getString(0));
                        if (!cur.moveToNext())
                            break;
                    }
                }else{
                    insert2(roomname);
                }
                insert("me","me",msgInput.getText().toString(),"1",roomNick);
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
            Log.i("listener check", "check!!!!!!");
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
                    String setRoom="";
                    try {
                        setMsg =  msg.getString("\"message\"");
                        setName = msg.getString("\"email\"");
                        setNickName = msg.getString("\"nickName\"");
                        setRoom = msg.getString("\"room\"");
                        Log.i("msg",setMsg);
                        Log.i("닉네이이이이이이이임",setNickName);
                        Log.i("roomname equals setRoom", roomname+" = "+setRoom);


                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    //상대방 이메일이 맞으면 테이블 인서트
                    if(roomname.equals(setRoom)) {
                        addMsg(setNickName, setMsg,0);
                        //insert(setName,setNickName,setMsg,"0");
                   }
                }
            });
        }
    };
    @Override
    public void onDestroy(){
        super.onDestroy();

    }


    public String userId (){
        db = helper.getReadableDatabase();
        String query = "select user from divice";
        Cursor cur = db.rawQuery(query,null);
        cur.moveToFirst();
        String user = cur.getString(0);
        return user;
    }

    public void outRoom(JSONObject jsonObject){
        mSocket.emit("outRoom", jsonObject);
    }
    //데이터 삽입
    public void insert(String id,String nickName,String text,String type,String roomNickName) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤

        // 데이터의 삽입은 put을 이용한다.
        values.put("ChatId", id);
        values.put("ChatNickName",nickName);
        values.put("ChatText",text);
        values.put("ChatRoomNickName", roomNickName);

        values.put("type",type);
        db.insert("'"+result+"'", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
        db = helper.getReadableDatabase();
        String sql = "select ChatRoomNickName from '"+result+"';";
        Cursor c = db.rawQuery(sql,null);
        c.moveToFirst();
        Log.i("insert-roomNickName", c.getString(0));
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

        JSONObject actData = new JSONObject();
        db = helper.getReadableDatabase();
        String query2 = "select user from divice";
        Cursor cur3 = db.rawQuery(query2, null);
        cur3.moveToFirst();
        my_email = cur3.getString(0);
        try {
            actData.put("email", my_email);
            //actData.put("activity", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("outActivity", actData);

        Intent intent = new Intent();
        String rs2 = "";

        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences preferences = getSharedPreferences("chatEmail",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email","none");
        editor.commit();
    }
    public void set_badge_alarm(int badge_count){
        Log.e(TAG , "badgeCount :"+badge_count);
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", badge_count);
        intent.putExtra("badge_count_package_name", getPackageName());
        intent.putExtra("badge_count_class_name", "com.example.kimea.myapplication.LoadingActivity");
        sendBroadcast(intent);
    }
}
