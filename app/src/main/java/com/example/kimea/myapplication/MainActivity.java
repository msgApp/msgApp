package com.example.kimea.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    JSONObject data = new JSONObject();
    SQLiteDatabase db;
    DBHelper helper =  new DBHelper(MainActivity.this, "token.db",null,1);
    DBHelper helper2 =  new DBHelper(MainActivity.this, "divice.db",null,1);
    TextView search_id;
    TextView search_pw;
    TextView register;
    TextView login_id;
    TextView login_pw,tx_view;
    String result, result2,userId ;
    private Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        //String user = helper.getUser();

       // if(!user.equals(null)){
           // Intent intent2 = new Intent(MainActivity.this,ViewPagerActivity.class);
           // intent2.putExtra("id", user);
      //  }

        try {
            SQLiteDatabase database = helper.getReadableDatabase();
            String sql = "select user from divice";
            Cursor cursor = database.rawQuery(sql,null);
            while(cursor.moveToNext()){
                userId = cursor.getString(0);
              //  Log.i("idssss",userId);
            }
            if (cursor.equals(null)||cursor==null){

            }else{
                mSocket.connect();
                Intent intent2 = new Intent(MainActivity.this,ViewPagerActivity.class);
                intent2.putExtra("id", userId);
                startActivity(intent2);
            }
        }catch (Exception e){
            db = helper.getWritableDatabase();
            db.execSQL("create table divice(user text primary key,token text);");
            Log.i("create","create");
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


                String url = "http://192.168.0.53:1300/login";
                ServerTask serverTask = new ServerTask(url,loginData.toString());
                serverTask.execute();

                db = helper.getWritableDatabase();
                db.execSQL("delete from token where token is not null");

                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO
                        try{


                        if(!result2.equals("false")){
                            insert(result2);
                            mSocket.connect();

                            db = helper.getWritableDatabase();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("user",login_id.getText().toString());
                            db.insert("divice","null",contentValues);

                            Intent intent2 = new Intent(MainActivity.this,ViewPagerActivity.class);
                            intent2.putExtra("id", login_id.getText().toString());

                            startActivity(intent2);
                        }else{
                            Toast.makeText(MainActivity.this, "로그인 실패!", Toast.LENGTH_SHORT).show();
                        }
                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, "서버에 문제가 있습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 500);

                break;

        }

    }
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
        mSocket.disconnect();
        super.onDestroy();
        //mSocket.emit("disconnet", data);
        //mSocket.disconnect();

       // Intent intent = new Intent(getApplicationContext(),SocketService.class); // 이동할 컴포넌트

       // stopService(intent);
    }
}
