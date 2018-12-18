package com.example.kimea.myapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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
    ImageView imgview,imgview2;
    TextView profile;
    JSONObject data = new JSONObject();
    final int REQ_CODE_SELECT_IMAGE=100;
    String encodeImg;
    String email;
    SQLiteDatabase db;
    DBHelper helper =  new DBHelper(ProfileSetActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileset);

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        SharedPreferences preferences = getSharedPreferences("myEmail",MODE_PRIVATE);
        email = preferences.getString("email","");
        imgview = findViewById(R.id.profileImg);
        profile = findViewById(R.id.profileText);

    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profileImg:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
                break;
            case R.id.profileSend:
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Drawable d = imgview.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] photo = baos.toByteArray();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                Bitmap src = BitmapFactory.decodeByteArray(photo, 0, photo.length, options);
                Bitmap resized = Bitmap.createScaledBitmap(src, 300, 300, true);
                imgview.setImageBitmap(resized);
                resized.compress(Bitmap.CompressFormat.JPEG,100,baos);
                byte[] resize = baos.toByteArray();
                encodeImg = Base64.encodeToString(resize, Base64.DEFAULT);
                byteArrayToBitmap(encodeImg);
              // for (byte b:photo){
                //  Log.i("img",String.valueOf(b));
           //   }
                try {
                    //String result = new String(photo,"utf-8");
                  // Log.i("asdas",result);
                    data.put("u_pf_img", encodeImg);
                    data.put("u_email",email);
                   // data.put("u_pf_img", encodeImg);
                    data.put("u_pf_text", profile.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mSocket.emit("setProfile",data);
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
                }
            }
        }

    }
    public Bitmap byteArrayToBitmap(String aa) {
        Bitmap bitmap = null;
        byte[] decodedString = Base64.decode(aa, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imgview2.setImageBitmap(bitmap);
        return bitmap;
    }


}
