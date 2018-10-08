package com.example.kimea.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendProfile extends AppCompatActivity implements View.OnClickListener{
    ImageView profileImg;
    TextView nickname;
    TextView profileText;
    String email;


    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        profileImg = findViewById(R.id.profileImg);
        nickname = findViewById(R.id.nickname);
        profileText = findViewById(R.id.profileText);


        Intent intent = getIntent();
        Bitmap bitmap = BitmapFactory.decodeByteArray(
                getIntent().getByteArrayExtra("img"), 0, getIntent().getByteArrayExtra("img").length);

        profileImg.setImageBitmap(bitmap);
        nickname.setText(intent.getStringExtra("nickname"));
        profileText.setText(intent.getStringExtra("profileText"));
        email = intent.getStringExtra("email");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_msg :
                Intent intent = new Intent(FriendProfile.this, ChatRoomActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
        }
    }
}
