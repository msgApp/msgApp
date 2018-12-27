package com.example.kimea.myapplication;

import android.database.Cursor;
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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kimea.myapplication.util.ChatApplication;
import com.example.kimea.myapplication.util.DatePickerFragment;
import com.example.kimea.myapplication.util.DatePickerFragment2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SearchId extends AppCompatActivity{
    private static final String TAG = "SearchId";
    TextView birthDay, searchName, searchid_result;
    Socket mSocket;
    JSONObject eList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchid);

        ChatApplication application = (ChatApplication) getApplication();
        mSocket = application.getSocket();
        mSocket.connect();

        ActionBar ab = getSupportActionBar();
        ab.setTitle("아이디 찾기");
        birthDay = findViewById(R.id.search_birthDay);
        searchName = findViewById(R.id.search_name);
        searchid_result = findViewById(R.id.searchid_result);

        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String result = dateFormat.format(today);

        birthDay.setText(result);
        mSocket.on("selectEmail",listener);
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
                   Log.e(TAG, "getData = "+searchName.getText().toString()+"/"+birthDay.getText().toString());
                   data.put("name",searchName.getText().toString());
                   data.put("birthDay",birthDay.getText().toString());
               }catch (Exception e ){

               }
               mSocket.emit("selectEmail",data);
        }
        return super.onOptionsItemSelected(item);
    }
    public void onBirthdayClicked (View v) {
        android.support.v4.app.DialogFragment newFragment = new DatePickerFragment2();   //DatePickerFragment 객체 생성
        newFragment.show(getSupportFragmentManager(), "datePicker");                //프래그먼트 매니저를 이용하여 프래그먼트 보여주기
    }
    public void getDate(String date){
        birthDay.setText(date);
    }
    private Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    eList = (JSONObject) args[0];
                    Log.e(TAG, "JSON " + eList);
                    try {
                        String email = eList.getString("email");
                        if (email !=null){
                            searchid_result.setText("찾으신 아이디는 "+email+" 입니다.");
                        }else if(email.equals("false")){
                            searchid_result.setText("아이디를 찾을 수 없습니다.");
                        }
                        Log.e(TAG, "email " + email);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}




