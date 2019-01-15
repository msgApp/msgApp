package com.example.kimea.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import com.github.chrisbanes.photoview.PhotoView;

public class ViewChatImgActivity extends AppCompatActivity {
    private int showActions = 0;
    PhotoView viewChatImg;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewchatimg);

        ActionBar bar = getSupportActionBar();
        Intent intent = getIntent();

        byte[] getByte = intent.getByteArrayExtra("image");
        Bitmap image = BitmapFactory.decodeByteArray(getByte, 0, getByte.length);
        Drawable d = new BitmapDrawable(getResources(), image);

        viewChatImg = findViewById(R.id.viewChatImg);
        viewChatImg.setImageDrawable(d);
        if (bar!=null){
                bar.hide();
        }
    }

}
