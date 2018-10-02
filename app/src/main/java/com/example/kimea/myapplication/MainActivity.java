package com.example.kimea.myapplication;

import android.content.ContentValues;
import android.content.Intent;
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

    SQLiteDatabase db;
    DBHelper helper =  new DBHelper(MainActivity.this, "token.db",null,1);
    TextView search_id;
    TextView search_pw;
    TextView register;
    TextView login_id;
    TextView login_pw,tx_view;
    String result, result2 ;
    ChatRoomActivity chatroom;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper dbHelper = new DBHelper(getApplicationContext(), "Token.db", null, 1);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build()
        );

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();


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
                Intent intent = new Intent(MainActivity.this,SearchIdActivity.class);
                startActivity(intent);
                break;
            case R.id.search_pw:

                break;

            case R.id.register:
                intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.login_btn:
                //insert
                JSONObject loginData = new JSONObject();
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
                        if(!result2.equals("false")){
                            insert(result2);
                            mSocket.connect();
                            //Intent intent = new Intent(MainActivity.this, SocketService.class);

                            Intent intent2 = new Intent(MainActivity.this,ViewPagerActivity.class);
                            intent2.putExtra("id", login_id.getText().toString());
                            startActivity(intent2);
                        }else{
                            Toast.makeText(MainActivity.this, "로그인 실패!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 500);

                break;
                /*
            case R.id.mailSend:
                try {
                    GMailSender gMailSender = new GMailSender("MyEmail@gmail.com", "password1234");
                    //GMailhSender.sendMail(제목, 본문내용, 받는사람);
                    gMailSender.sendMail("제목입니다", message.getText().toString(), textView.getText().toString());
                    Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
                } catch (SendFailedException e) {
                    Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                } catch (MessagingException e) {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
                */
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
