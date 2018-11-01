package com.example.kimea.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;

public class FriendProfile extends AppCompatActivity implements View.OnClickListener{
    ImageView profileImg;
    TextView nickname;
    TextView profileText;
    String email;
    SQLiteDatabase db;
    private Socket mSocket;
    DBHelper helper =  new DBHelper(this);


    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        ChatApplication app = (ChatApplication)getApplication();
        mSocket = app.getSocket();

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


        db = helper.getReadableDatabase();
        String query = "select user from divice";
        Cursor cur = db.rawQuery(query, null);

        cur.moveToFirst();
        String my_email = cur.getString(0);
        JSONObject actData = new JSONObject();

        try {
            actData.put("email", my_email);
            actData.put("activity", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("intoActivity", actData);



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
