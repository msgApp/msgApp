package com.example.kimea.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kimea.myapplication.util.ChatApplication;

import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SearchPw extends AppCompatActivity {
    private static final String TAG = "SearchPw";
    TextView searchpw_pw;
    Socket mSocket;
    JSONObject pass;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchpw);
        searchpw_pw = findViewById(R.id.searchpw_emaill);
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.connect();

        ActionBar ab = getSupportActionBar();
        ab.setTitle("비밀번호 찾기");
        mSocket.on("selectPassword",listener);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.resultButton:
                JSONObject data = new JSONObject();
                try{
                    Log.e(TAG, "getData = "+searchpw_pw.getText().toString());
                    data.put("email", searchpw_pw.getText().toString());
                }catch (Exception e){

                }
                mSocket.emit("selectPassword",data);
        }
        return super.onOptionsItemSelected(item);
    }
    private Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    pass = (JSONObject) args[0];
                    Log.e(TAG, "JSON = "+pass);
                    try {
                        if (pass.getBoolean("result")) {
                            Toast.makeText(SearchPw.this, "이메일로 비밀번호가 전송 되었습니다.", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(SearchPw.this,"없는 이메일입니다.",Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e){

                    }
                }
            });
        }
    };
}
