package com.example.kimea.myapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ProfileSetActivity extends AppCompatActivity implements View.OnClickListener {
    private Socket mSocket;
    ImageView imgview;
    TextView profile;
    JSONObject data = new JSONObject();
    final int REQ_CODE_SELECT_IMAGE=100;

    SocketService socketservice;
    boolean isService = false;

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, SocketService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
// 서비스와 연결되었을 때 호출되는 메서드
// 서비스 객체를 전역변수로 저장
            SocketService.MyBinder mb = (SocketService.MyBinder) service;
            socketservice = mb.getService(); // 서비스가 제공하는 메소드 호출하여
// 서비스쪽 객체를 전달받을수 있슴
            isService = true;
            Log.i("true",String.valueOf(isService));
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
// 서비스와 연결이 끊겼을 때 호출되는 메서드
            isService = false;
            Log.i("true",String.valueOf(isService));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileset);

        imgview = findViewById(R.id.profileImg);
        profile = findViewById(R.id.profileText);

    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setImg:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
                break;
            case R.id.profileSend:
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Drawable d = imgview.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] photo = baos.toByteArray();

                Log.i("profileImg", photo.toString());

                try {
                    data.put("u_pf_img", photo.toString());
                    data.put("u_pf_text", profile.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO
                        if (isService) {
                            socketservice.sendProfile(data);
                            Toast.makeText(ProfileSetActivity.this, "성공", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileSetActivity.this, "실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 500);
                break;
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

//                    배치해놓은 ImageView에 이미지를 넣어봅시다.
                    imgview.setImageBitmap(bitmap);
//                    Glide.with(mContext).load(data.getData()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView); // OOM 없애기위해 그레들사용

                } catch (Exception e) {
                    Log.e("test", e.getMessage());
                }
            }
        }


    }



}
