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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import io.socket.client.Socket;

public class ViewPagerActivity extends AppCompatActivity{
    JSONObject data = new JSONObject();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Socket mSocket;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    ImageView imgview,imgview2;
    final int REQ_CODE_SELECT_IMAGE=100;
    String encodeImg;
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_viewpager);

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);

        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);

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
                if (!isFabOpen) {
                    //fab1.startAnimation(fab_close);
                    //fab2.startAnimation(fab_close);
               //     fab1.setClickable(true);
                 //   fab2.setClickable(true);
                    isFabOpen = true;

                } else {
                    // fab1.startAnimation(fab_open);
                    // fab2.startAnimation(fab_open);
                    fab1.setClickable(false);
                    fab2.setClickable(false);
                    isFabOpen = false;
                }
                break;
           //case R.id.setImg:
                //Intent intent = new Intent(Intent.ACTION_PICK);
                //intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
               // intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               // startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
               // break;

        }
    }

}
