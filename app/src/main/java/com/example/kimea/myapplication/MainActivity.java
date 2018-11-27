package com.example.kimea.myapplication;

import android.app.Activity;
import android.content.ContentValues;
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
    ArrayList userList;
    String result, result2, userId, msgToken="";
    int countItem = 0;
    private Socket mSocket;
    ArrayList<GetFriendListItem2> mainList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        mainList = new ArrayList<>();
        try {

            SQLiteDatabase database = helper.getReadableDatabase();
            String sql = "select user from divice";
            Cursor cursor = database.rawQuery(sql,null);
            boolean tf = cursor.moveToFirst();
            Log.i("tf",String.valueOf(tf));

            if(String.valueOf(tf).equals("true")){
                Log.i("idssss","is not null");

                cursor = database.rawQuery(sql,null);
                while(cursor.moveToNext()){
                    userId = cursor.getString(0);
                    //Log.i("idssss",userId);
                }

            }else if(String.valueOf(tf).equals("false")){
                userId = "";
                Log.i("idssss","is null");
            }

            if (userId.equals("")||userId=="") {

            }else{
                mSocket.connect();
                Intent intent2 = new Intent(MainActivity.this,ViewPagerActivity.class);
                intent2.putExtra("id", userId);
                startActivity(intent2);
            }

        }catch (Exception e){
            db = helper.getWritableDatabase();
            db.execSQL("create table divice(user text,token text,msgToken text);");
            Log.i("createDivice","create");
        }

        Log.i("select","select");



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
                final JSONObject loginData = new JSONObject();
                try{
                    loginData.put("login_id",login_id.getText().toString());
                    loginData.put("login_pw",login_pw.getText().toString());
                 }catch (JSONException e){
                    e.printStackTrace();
                }
                String url = "http://192.168.0.71:1300/login";
                ServerTask serverTask = new ServerTask(url,loginData.toString());
                serverTask.execute();
                Log.i("server","dasdasdas");
                db = helper.getWritableDatabase();
                db.execSQL("delete from token where token is not null");

                final Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        try{
                        if(!result2.equals("false")){
                            //insert(result2);
                            mSocket.connect();
                            final String DATABASE_TABLE_ONEUSER = "CREATE TABLE oneUser(user_seq INTEGER PRIMARY KEY, userId TEXT)";
                            db = helper.getWritableDatabase();
                            db.execSQL(DATABASE_TABLE_ONEUSER);

                            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("userId", login_id.getText().toString());
                            editor.commit();

                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this,
                                    new OnSuccessListener<InstanceIdResult>() {
                                        @Override
                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = pref.edit();
                                            editor.putString("msgToken", instanceIdResult.getToken());
                                            editor.commit();
                                            ContentValues contentValues = new ContentValues();
                                            contentValues.put("user",login_id.getText().toString());
                                            String newToken = instanceIdResult.getToken();
                                            msgToken = instanceIdResult.getToken();
                                            contentValues.put("msgToken",msgToken);
                                            db.insert("divice","null",contentValues);
                                            Log.e("DiviceInsertNewToken",newToken);
                                        }
                                    });
                            //contentValues.put("msgToken",msgToken);
                            FirebaseMessaging.getInstance().subscribeToTopic("ALL");
                            SharedPreferences pref2 = getSharedPreferences("pref",MODE_PRIVATE);
                            String userid =  pref2.getString("userId",null);
                            String msgT =  pref2.getString("msgToken",null);
                            JSONObject data2 = new JSONObject();
                            try {
                                data2.put("email", userid);
                                data2.put("divice", msgT);
                            }catch (Exception e){

                            }
                            mSocket.emit("sendUser",data2);
                            mSocket.on("friendList", listener);
                            final Handler delayHandler2 = new Handler();
                            delayHandler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    friendInsert();
                                }
                            },1000);
                            final Handler delayHandler3 = new Handler();
                            delayHandler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent intent2 = new Intent(MainActivity.this,ViewPagerActivity.class);
                                    intent2.putExtra("id", login_id.getText().toString());
                                    startActivity(intent2);
                                }
                            },1000);
                        }else{
                            Toast.makeText(MainActivity.this, "로그인 실패!", Toast.LENGTH_SHORT).show();
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
                        Log.e(TAG,"setUserNickname "+setUserNickname);
                        Log.e(TAG,"setProfileText "+setProfileText);
                        Log.e(TAG,"setEmail "+setEmail);

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    makeList(setUserImg, setUserNickname, setProfileText, setEmail);
                }
            });
        }
    };
    private Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.i("countItem",String.valueOf(countItem));
                    fList = (JSONArray)args[0];

                    userList = new ArrayList();

                    if(countItem < fList.length()){
                        try {
                            for(int i = 0; i<fList.length(); i++){
                                Log.i("fList length", String.valueOf(fList.length()));
                                Log.i("listener1",fList.toString());
                                JSONObject jo = fList.getJSONObject(i);
                                JSONObject data = new JSONObject();
                                //Log.i("fListArray", jo.toString());
                                userList.add(fList.getJSONObject(i).getString("f_email"));
                                //  Log.i("msg",fList.getJSONObject(i).getString("f_email"));
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
            Log.i("chat",result2);

            //insert

        }
    }
    public void makeList(String img, String nick, String text, String email){
        Log.e(TAG,nick+text);
       mainList.add(new GetFriendListItem2(img,nick,text,email));
    }
    public void friendInsert(){
        db = helper.getWritableDatabase();
        String query2 = "select friendemail from friend";
        Cursor cur3 = db.rawQuery(query2, null);
        Log.e(TAG,"0");
        if (!cur3.moveToFirst()){
            Log.e(TAG,"1");
            if(mainList.size()!=0) {
                Log.e(TAG,"2");
                for (int i = 0; i < mainList.size(); i++) {
                    Log.e(TAG,"3");
                    ContentValues values = new ContentValues();
                    String email = mainList.get(i).getEmail();
                    Log.e(TAG,mainList.get(i).getEmail());
                    String nick = mainList.get(i).getUserNickname();
                    String img = mainList.get(i).getUserImgI();
                    String text = mainList.get(i).getProfileText();
                    // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤
                    // 데이터의 삽입은 put을 이용한다.
                    values.put("friendemail", email);
                    values.put("friendnick", nick);
                    values.put("friendimg", img);
                    values.put("friendText", text);
                    db.insert("friend", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
                    Log.i("SaveCharInsert", "insert");
                }
            }
        }else{
            /*
            Log.e(TAG,"asdasdasda");
            while(cur3.moveToNext()){
                Log.e(TAG,"qwerqwerqewrqwe");

                String query22 = "select friendemail from friend";
                Cursor cur33 = db.rawQuery(query22, null);
                Log.e(TAG,cur3.getString(0));
                if (cur33.getString(0).equals(email)){

                }else{
                    ContentValues values = new ContentValues();
                    // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤
                    // 데이터의 삽입은 put을 이용한다.
                    values.put("friendemail", email);
                    values.put("friendnick",nick);
                    values.put("friendimg",img);
                    values.put("friendText", text);
                    db.insert("friend", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
                    Log.i("SaveCharInsert","insert");
                }
            }
            */
        }

         // db 객체를 얻어온다. 쓰기 가능
    }
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

        //mSocket.disconnect();

       // Intent intent = new Intent(getApplicationContext(),SocketService.class); // 이동할 컴포넌트

       // stopService(intent);
    }


}
