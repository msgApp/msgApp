package com.example.kimea.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    JSONObject data = new JSONObject();
    SQLiteDatabase db;
    DBHelper helper =  new DBHelper(MainActivity.this);
    TextView search_pw, search_id, register, login_id, login_pw, tx_view;
    JSONArray fList;
    JSONObject pList;
    JSONArray msg;
    ArrayList userList;
    String result, result2, userId, msgToken="";
    int countItem = 0;
    int go=0;
    private Socket mSocket;
    ArrayList<GetFriendListItem2> mainList;
    String fEmail,fNickName,fImg,fText;
    int listenCount = 0;
    int cCount = 0;

    @Override
    protected void onResume() {
        super.onResume();
        countItem=0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        mainList = new ArrayList<>();
     //   Log.e(TAG,"CREATEMSGARRAY " + msg);
        try {
            db = helper.getWritableDatabase();
            String sql = "select user from divice";
            Cursor cursor = db.rawQuery(sql,null);
            if(cursor.moveToFirst()){
              //  Log.e(TAG,"DIVICESEARCHsuccess");
              //  Log.e(TAG,"DIVICESEARCHsuccess "+cursor.getString(0));
                userId = cursor.getString(0);
                cursor.close();
            }else{
               // Log.e(TAG,"DIVICESEARCHfailed");
                userId = null;
            }
            if (userId==null) {

            }else{
                mSocket.connect();
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                JSONObject data = new JSONObject();
                try {
                    data.put("email",  pref.getString("myEmail",null));
                    data.put("divice", pref.getString("msgToken",null));
                }catch (Exception e){
                }
                mSocket.emit("sendFriend",data);
                mSocket.on("friendList", listener);

                Intent intent2 = new Intent(MainActivity.this,ViewPagerActivity.class);
                intent2.putExtra("noti",getIntent().getStringExtra("noti"));
                intent2.putExtra("email",getIntent().getStringExtra("email"));
                intent2.putExtra("roomNickName",getIntent().getStringExtra("roomNickName"));
                intent2.putExtra("roomname", getIntent().getStringExtra("roomname"));
                intent2.putExtra("id", userId);
                Log.e(TAG,"NOTI = "+getIntent().getStringExtra("noti"));
                startActivity(intent2);
            }
        }catch (Exception e){
            //Log.e(TAG," "+e);
            db = helper.getWritableDatabase();
            db.execSQL("create table divice(user text,token text,msgToken text,loginyn text Default 'n');");
           // Log.e(TAG,"DIVICETABLECREATE");

        }
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build()
        );
        search_id = findViewById(R.id.search_id);
        search_pw = findViewById(R.id.search_pw);
        register = findViewById(R.id.register);
        login_id = findViewById(R.id.login_id);
        login_pw = findViewById(R.id.login_pw);
        tx_view= findViewById(R.id.tx_view);

        search_id.setOnClickListener(this);
        search_pw.setOnClickListener(this);
        register.setOnClickListener(this);

    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.search_id :

                break;
            case R.id.search_pw:

                break;

            case R.id.register:
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.login_btn:
                //insert
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                final JSONObject loginData = new JSONObject();
                try{
                    loginData.put("login_id",login_id.getText().toString());
                    loginData.put("login_pw",login_pw.getText().toString());
                }catch (JSONException e){
                    e.printStackTrace();
                }
                String url = "http://122.40.72.34:1300/login";
                ServerTask serverTask = new ServerTask(url,loginData.toString());
                serverTask.execute();
                db = helper.getWritableDatabase();
                db.execSQL("delete from token where token is not null");

                final Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(!result2.equals("false")){
                                //insert(result2);

                                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this,
                                        new OnSuccessListener<InstanceIdResult>() {
                                            @Override
                                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                                getToken(instanceIdResult.getToken());
                                                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = pref.edit();
                                                editor.putString("msgToken", instanceIdResult.getToken());
                                                editor.putString("myEmail", login_id.getText().toString());
                                                editor.apply();
                                                Log.e(TAG, pref.getString("msgToken",""));
                                                Log.e(TAG,"FIREBASETOKEN = "+instanceIdResult.getToken());
                                                JSONObject data2 = new JSONObject();
                                                try {
                                                    data2.put("email", pref.getString("myEmail",null));
                                                    data2.put("divice", instanceIdResult.getToken());
                                                }catch (Exception e){

                                                }
                                                mSocket.emit("sendFriend",data2);

                                                ContentValues contentValues = new ContentValues();
                                                contentValues.put("user",login_id.getText().toString());
                                                String newToken = instanceIdResult.getToken();
                                                msgToken = instanceIdResult.getToken();
                                                contentValues.put("msgToken",newToken);
                                                Log.e(TAG,"login_id = "+login_id.getText().toString()+" newToken = "+newToken);
                                                db.insert("divice","null",contentValues);
                                                Log.e(TAG,"DBINSERT");

                                            }
                                        });
                                mSocket.connect();

                                final String DATABASE_TABLE_ONEUSER = "CREATE TABLE oneUser(user_seq INTEGER PRIMARY KEY, userId TEXT)";
                                db = helper.getWritableDatabase();
                                db.execSQL(DATABASE_TABLE_ONEUSER);

                                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("userId", login_id.getText().toString());
                                editor.commit();
                                //contentValues.put("msgToken",msgToken);
                                FirebaseMessaging.getInstance().subscribeToTopic("ALL");

                                SharedPreferences pref2 = getSharedPreferences("pref",MODE_PRIVATE);
                                String userid =  pref2.getString("userId",null);
                                msgToken =  pref2.getString("msgToken",null);

                           //     Log.e(TAG,"TOKEN = "+msgToken);
                                JSONObject data2 = new JSONObject();
                                try {
                                    data2.put("email", userid);
                                    data2.put("divice", msgToken);
                                }catch (Exception e){

                                }
                                String listnerCheck = "";

                           //     Log.e(TAG,"friend");
                                mSocket.on("messageAfter", Lmsg);
                                String first = getIntent().getStringExtra("first");
                                try {
                                    if (first.isEmpty()) {

                                    }
                                }catch (Exception e){
                                    mSocket.on("friendList", listener);
                                }
                            }else{
                                Toast.makeText(MainActivity.this, "로그인 실패!", Toast.LENGTH_SHORT).show();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, "서버에 문제가 있습니다", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, 500);
                break;
        }
    }
    public void getToken(String token){
        Log.e(TAG,"GETTOKEN = "+token);
    }
    private Emitter.Listener listener2 = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    pList = (JSONObject) args[0];
                    //Log.i("pList", pList.toString());
                    String setUserImg ="";
                    String setUserNickname ="";
                    String setProfileText = "";
                    String setEmail = "";

                    try {
                        setUserImg = pList.getString("u_pf_img");
                        setUserNickname = pList.getString("u_nickname");
                        setProfileText = pList.getString("u_pf_text");
                        setEmail = pList.getString("u_email");
                        // Log.i("nickName&img", setUserNickname+", "+setUserImg);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    fEmail = setEmail;
                    fNickName = setUserNickname;
                    fText = setProfileText;
                    fImg = setUserImg;
                    Log.e(TAG,"DetailFriend " + fEmail+" "+fNickName);
                    friendInsert();
                    //makeList(setUserImg, setUserNickname, setProfileText, setEmail);
                }
            });
        }
    };
    public void goIntent(){
        Intent intent2 = new Intent(MainActivity.this, ViewPagerActivity.class);
        intent2.putExtra("id", login_id.getText().toString());
        startActivity(intent2);
        Log.e(TAG,"goIntent");
        fList = new JSONArray();
        pList = new JSONObject();
    }
    Handler handler = new Handler(Looper.getMainLooper());

    private Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            handler.post(new Runnable() {
                @Override
                public void run() {

                    fList = (JSONArray) args[0];
                    Log.e(TAG,"FList" + fList);
                    userList = new ArrayList();
                    listenCount = (fList.length()-1);
                    if(countItem < fList.length()){
                        try {
                            for(int i = 0; i<fList.length(); i++){
                                JSONObject jo = fList.getJSONObject(i);
                                JSONObject data = new JSONObject();
                                //Log.i("fListArray", jo.toString());
                                userList.add(fList.getJSONObject(i).getString("f_email"));
                                //  Log.i("msg",fList.getJSONObject(i).getString("f_email"));
                                Log.e(TAG,"FriendList = "+userList.get(i).toString());
                                data.put("u_email",userList.get(i).toString());
                                data.put("index",i);
                                mSocket.emit("sendProfile",data);
                                countItem++;
                            }
                            fList = new JSONArray();
                            mSocket.on("sendProfile", listener2);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else{

                    }
                }
            });
        }
    };
    public class ServerTask extends AsyncTask<Void,Void,String> {
        private String url;
        private String str;
        public ServerTask(String url, String str){
            this.url = url;
            this.str = str;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            try {
                result = requestHttpURLConnection.request(url,str);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            result = s;
            result2 = result.substring(2,result.length()-2);
          //  Log.e(TAG, "RESULT = "+result2);

            //insert

        }
    }
    public void friendInsert(){
        db = helper.getWritableDatabase();
        Log.e(TAG,"GetProfile" + fEmail +" "+fNickName+" "+fText);
        try {
            String sql = "select friendemail, friendnick, friendimg, friendText from friend where friendemail = '" + fEmail + "'";
            Cursor fcur = db.rawQuery(sql, null);
            if (fcur.moveToFirst()) {
                Log.e(TAG, "UpdateSelect " + fcur.getString(1) + " " + fcur.getString(3));
                if (!fcur.getString(1).equals(fNickName)) {
                    db.execSQL("update friend set friendnick = '" + fNickName + "' where friendemail ='" + fEmail + "'");
                    Log.e(TAG, "UpdateFriendNick");
                }
                if (!fcur.getString(2).equals(fImg)) {
                    db.execSQL("update friend set friendimg = '" + fImg + "' where friendemail ='" + fEmail + "'");
                    Log.e(TAG, "UpdateFriendImg");
                }
                if (!fcur.getString(3).equals(fText)) {
                    db.execSQL("update friend set friendText = '" + fText + "' where friendemail ='" + fEmail + "'");
                    Log.e(TAG, "UpdateFriendText");
                }
            }
        }catch (Exception e){

        }
        ContentValues values = new ContentValues();
        String email = fEmail;
        Log.e(TAG,"CHECKID = "+email);
        String nick = fNickName;
        String img = fImg;
        String text = fText;
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤
        // 데이터의 삽입은 put을 이용한다.
        values.put("friendemail", email);
        values.put("friendnick", nick);

        values.put("friendimg", img);

        values.put("friendText", text);
        String query2 = "select loginyn from divice";
        Cursor cur3 = db.rawQuery(query2, null);
        String Yn="";
        String query3 = "select user,loginyn from divice";
        Cursor cur = db.rawQuery(query3, null);
        while (cur.moveToFirst()){
            Log.e(TAG,"SelectDivice = "+cur.getString(0)+"/"+cur.getString(1));
            break;
        }
        if(cur3.moveToFirst()){
            Log.e(TAG,"YN = "+ cur3.getString(0));
            Yn = cur3.getString(0);
        }
        if (Yn.equals("n")) {
            db.insert("friend", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
            Log.e(TAG, "LISTC = " + listenCount + " cCount " + countItem);
            if (listenCount == cCount) {
                goIntent();
            } else {
                cCount++;
            }
        }

    }

    // db 객체를 얻어온다. 쓰기 가능

    public void insert(String token) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤

        // 데이터의 삽입은 put을 이용한다.
        values.put("token", token);
        db.insert("token", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.

    }

    @Override
    protected void onDestroy() {
        JSONObject actData = new JSONObject();
        db = helper.getReadableDatabase();
        String query2 = "select user from divice";
        Cursor cur3 = db.rawQuery(query2, null);
        cur3.moveToFirst();
        String my_email = cur3.getString(0);
        try {
            actData.put("email", my_email);
            //actData.put("activity", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("outActivity", actData);
        mSocket.disconnect();
        super.onDestroy();

    }
    private Emitter.Listener Lmsg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (go==0) {
                        msg = (JSONArray) args[0];
                        go++;
                        Log.e(TAG, "LMSGLISTENER = " + msg);
                        String getMSg = "";
                        String nickName = "";
                        String userId = "";
                        String room = "";
                        String roomNick = "";
                        try {
                            for (int i = 0; i < msg.length(); i++) {
                                String get = msg.getString(i);
                                //JSONObject gets = msg.getJSONObject(i);
                                JSONObject gets = new JSONObject(get);
                                getMSg = gets.getString("message");
                                nickName = gets.getString("nickName");
                                userId = gets.getString("email");
                                room = gets.getString("room");
                                roomNick = gets.getString("roomNickName");
                          //      Log.e(TAG, "friendTab = " + getMSg + " " + nickName + " " + room);
                                msgInsert(userId, getMSg, nickName, room, roomNick);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };
    public void msgInsert(String id,String text,String nickName,String room, String roomNickName) throws Exception {
        db.close();
        String[] array = room.split("@");
        String ss = array[1];
        String[] ary2 = ss.split("\\.");
        String tableResult = array[0] + ary2[0] + ary2[1];

        try {
            db = helper.getReadableDatabase();
            String query = "select * from '" + tableResult + "'";
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

        } catch (Exception e) {
            db = helper.getWritableDatabase();
            db.execSQL("create table '" + tableResult + "'(Chatseq integer primary key autoincrement, ChatId text,ChatNickName text, ChatText text, ChatRoomNickName text,room text, type TEXT);");
            Log.e(TAG,"CREATETABLERESULT");
        }
        ContentValues values = new ContentValues();
        values.put("ChatId", id);
        values.put("ChatNickName", nickName);
        values.put("ChatText", text);
        values.put("ChatRoomNickName", roomNickName);
        values.put("type", "0");
        db = helper.getWritableDatabase();
        db.insert("'" + tableResult + "'", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
        Log.i("SaveCharInsert", "insertㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        int badge_int = Integer.parseInt(preferences.getString(room, "0"));
        badge_int++;
        SharedPreferences.Editor editor3 = preferences.edit();
        editor3.putString(room, String.valueOf(badge_int));
        editor3.apply();
        Log.e(TAG, "ㅡㅡㅡbadgeInsertㅡㅡㅡ = " + badge_int);
        try {
            db = helper.getWritableDatabase();
            String query = "select userId from oneUser where userId = '" + room + "';";
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            String result = cursor.getString(0);
            Log.e(TAG,"TRY");
            cursor.close();
        } catch (Exception e) {
            ContentValues values1 = new ContentValues();
            values1.put("userId", room);
            db.insert("oneUser", null, values1);
           Log.e(TAG,"CATCH");
        }

    }

}
