package com.example.kimea.myapplication;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.kimea.myapplication.util.ChatApplication;
import com.example.kimea.myapplication.util.DBHelper;

import org.json.JSONObject;

import io.socket.client.Socket;

public class FriendProfile extends AppCompatActivity implements View.OnClickListener{
    ImageView profileImg;
    TextView nickname;
    TextView profileText;
    String email,my_email;
    String intentPosition;
    int position;
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
        intentPosition = intent.getStringExtra("position");
        position = Integer.valueOf(intentPosition);

        db = helper.getReadableDatabase();
        String query = "select user from divice";
        Cursor cur = db.rawQuery(query, null);

        cur.moveToFirst();
        my_email = cur.getString(0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_msg :



                Intent intent = new Intent(FriendProfile.this, ChatRoomActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("roomname", email);
                intent.putExtra("roomNickName",nickname.getText());
                SharedPreferences pref = getSharedPreferences("chatEmail",MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("email",email);
                editor.commit();
                startActivity(intent);
                break;

            case R.id.block :
                JSONObject blockJson = new JSONObject();
                try{
                    blockJson.put("f_email", email);
                    blockJson.put("my_email", my_email);
                }catch (Exception e){
                    e.printStackTrace();
                }
                mSocket.emit("block", blockJson);

                //FriendTabFragment ff = new FriendTabFragment();
                //ff.removed(position);

                Fragment fragment = new FriendTabFragment();
                Bundle bundle = new Bundle(1);
                bundle.putString("position", intentPosition);
                fragment.setArguments(bundle);
                //.finish();
                db = helper.getWritableDatabase();
                db.delete("friend","friendemail = ?",new String[]{email});
                Intent backIntent = new Intent(this, ViewPagerActivity.class);
                backIntent.putExtra("id", my_email);
                startActivity(backIntent);

                break;


        }
    }
}
