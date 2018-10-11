package com.example.kimea.myapplication;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ViewPagerActivity extends AppCompatActivity{
    JSONObject data = new JSONObject();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Socket mSocket;
    JSONArray msg;
    String ids;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_viewpager);

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();

        ids = getIntent().getStringExtra("id");
        try {
            data.put("email", ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("sendUser",data);
        mSocket.on("messageAfter",Lmsg);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("친구"));
        tabLayout.addTab(tabLayout.newTab().setText("대화방"));
        tabLayout.addTab(tabLayout.newTab().setText("설정"));
        //tabLayout.addTab(tabLayout.newTab().setText("설마?"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //Initializing ViewPager

        viewPager = findViewById(R.id.viewPager);

        //Creating adapter
        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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

    private Emitter.Listener Lmsg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msg = (JSONArray) args[0];
                   ;
                    Log.i("list",msg.toString());
                    String getMSg="";
                    String nickName="";
                    try {
                        for(int i=0;i<msg.length();i++){
                            JSONObject gets = msg.getJSONObject(i);
                            Log.i("msg",msg.getJSONObject(i).toString());

                            //getMSg = gets.getString("message");
                           // nickName = gets.getString("nickName");
                           // Log.i("msg",getMSg);
                           // Log.i("name",nickName);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    @Override
    public void onBackPressed() {
        //super.onBackPressed();

    }
}
