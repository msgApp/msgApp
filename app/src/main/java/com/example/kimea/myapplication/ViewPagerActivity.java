package com.example.kimea.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.kimea.myapplication.adapter.TabPagerAdapter;
import com.example.kimea.myapplication.util.BackPressCloseHandler;
import com.example.kimea.myapplication.util.ChatApplication;
import com.example.kimea.myapplication.util.DBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ViewPagerActivity extends AppCompatActivity implements TabPagerAdapter.OnSendPop{
    private static final String TAG = "ViewPagerActivity";
    JSONObject data = new JSONObject();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    JSONObject pList;
    private Socket mSocket;
    String ids;
    String noti;
    TabPagerAdapter pagerAdapter;
    SQLiteDatabase db;
    DBHelper helper =  new DBHelper(ViewPagerActivity.this);
    public static Context CONTEXT;
    private BackPressCloseHandler backPressCloseHandler;
  //  MainActivity mainActivity = (MainActivity)MainActivity.mainac;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_viewpager);
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.connect();
        CONTEXT = this;
        helper =  new DBHelper(ViewPagerActivity.this);
        backPressCloseHandler = new BackPressCloseHandler(this);
        db = helper.getWritableDatabase();
        try {
            String sql = "select * from myprofile";
            Cursor cur = db.rawQuery(sql, null);
            Log.e(TAG,"select");
        }catch (Exception e){
            db.execSQL("create table myprofile(myimg text, mytext text);");
            Log.e(TAG,"create");
        }
        //mainActivity.finish();
        ids = getIntent().getStringExtra("id");
        noti = getIntent().getStringExtra("noti");
        Log.e(TAG,"VIEWNOTI = "+noti);
        if (noti!=null){
            Intent intent = new Intent(ViewPagerActivity.this,ChatRoomActivity.class);
            intent.putExtra("email",getIntent().getStringExtra("email"));
            intent.putExtra("roomname",getIntent().getStringExtra("roomname"));
            intent.putExtra("roomNickName",getIntent().getStringExtra("roomNickName"));
            startActivity(intent);
        }
        try {
            data.put("email", ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try{

        db = helper.getWritableDatabase();
        String query22 = "select user from divice";
        Cursor cur33 = db.rawQuery(query22, null);
        cur33.moveToNext();
        String id = cur33.getString(0);
        Log.e(TAG,"LoginCur ="+cur33.getString(0));
        db.execSQL("update divice set loginyn = 'y' where user = '"+id+"'");
        }catch (Exception e){

        }
        JSONObject data2 = new JSONObject();
        SharedPreferences preferences = getSharedPreferences("pref",Context.MODE_PRIVATE);
        String myEmail = preferences.getString("myEmail","");
        Log.e(TAG,"myEmail = "+myEmail);
        try {
            data2.put("u_email", myEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("sendMyProfile",data2);
        mSocket.on("sendMyProfile",profile2);

        SharedPreferences pref = getSharedPreferences("chatEmail",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (pref.getString("email","").isEmpty()) {
            editor.putString("email", "none");
            editor.commit();
        }

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("친구"));
        tabLayout.addTab(tabLayout.newTab().setText("대화방"));
        tabLayout.addTab(tabLayout.newTab().setText("설정"));
        //tabLayout.addTab(tabLayout.newTab().setText("설마?"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //Initializing ViewPager

        viewPager = findViewById(R.id.viewPager);

        //Creating adapter
        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //Set TabSelectedListener

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
              //  pagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //pagerAdapter.notifyDataSetChanged();
            }
        });
        SQLiteDatabase database = helper.getReadableDatabase();
        String sql = "select * from divice";
        Cursor cursor2 = database.rawQuery(sql, null);
        while(cursor2.moveToNext()){

        }

    }
    public void onClick(final View v){
        switch (v.getId()){
            case R.id.fab:
               // Toast.makeText(ViewPagerActivity.this,"dasd",Toast.LENGTH_SHORT).show();

                break;
           //case R.id.setImg:
                //Intent intent = new Intent(Intent.ACTION_PICK);
                //intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
               // intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               // startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
               // break;

        }
    }
    public void reset(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        // 해당 작업을 처리함
                        pagerAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }
    private Emitter.Listener profile2 = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    pList = (JSONObject) args[0];

                    //f      Log.i("pList", pList.toString());
                    String setUserImg ="";
                    String profileText ="";
                    try {
                        setUserImg = pList.getString("u_pf_img");
                        profileText = pList.getString("u_pf_text");
                        myProfileInsert(setUserImg,profileText);
                        // Log.i("nickName&img", setUserNickname+", "+setUserImg);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
        }
    };
    public void myProfileInsert(String img, String text){
        db = helper.getWritableDatabase();
        String sql = "select * from myprofile";
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.moveToFirst()){

        }else{
            ContentValues values = new ContentValues();
            values.put("myimg", img);
            values.put("mytext", text);
            db.insert("myprofile",null,values);
            Log.e(TAG,"ProfileInsert");
        }

    }

}
