package com.example.kimea.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class AddFriendActivity extends AppCompatActivity implements View.OnClickListener{

    private Socket mSocket;
    TextView fEmail,fName,friendText;
    Button addFriend;
    ImageView imgview;
    JSONArray friend = new JSONArray();
    String result,friendResult;
    String ids;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        fEmail = findViewById(R.id.fEmailInput);
        friendText = findViewById(R.id.friendResult);
        fName = findViewById(R.id.friendName);
        imgview = findViewById(R.id.friendImg);
        addFriend  = findViewById(R.id.addFriend);

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();

        ids = getIntent().getStringExtra("id");

    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fSearch:
                if (fEmail.getText().toString().equals(ids)){

                    imgview.setVisibility(View.INVISIBLE);
                    friendText.setVisibility(View.INVISIBLE);
                    addFriend.setVisibility(View.INVISIBLE);
                    fName.setText("나 자신이야");
                }else{
                    //imgview.setVisibility(View.VISIBLE);
                    //addFriend.setVisibility(View.VISIBLE);
                    JSONObject data = new JSONObject();
                    try{
                        data.put("email",fEmail.getText().toString());
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    mSocket.emit("selectUser",data);

                    mSocket.on("selectFriend",friends);
                }

                break;
            case R.id.addFriend:
                JSONObject data2 = new JSONObject();
                try{
                    data2.put("u_email",fEmail.getText().toString());
                }catch (JSONException e){
                    e.printStackTrace();
                }
                mSocket.emit("addFriend",data2);
                mSocket.emit("sendProfile", data2);
                Toast.makeText(getApplicationContext(),"친구 추가 되었습니다!",Toast.LENGTH_SHORT).show();
                this.finish();
                break;
        }
    }
    private Emitter.Listener friends = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    friend = (JSONArray) args[0];
                  //  Log.i("list",friend.toString());
                    String friendName="";
                    String resultText;
                    try {
                        for(int i=0;i<friend.length();i++){
                            try{
                                JSONObject get = friend.getJSONObject(i);
                                //getByteValue(get,"img");
                                // Log.i("listener1",friend.toString());
                                result = get.getString("img");
                                // resultText = get.getString("result");
                                // Log.i("resultText",resultText);
                                Log.i("decode",result);
                                byteArrayToBitmap(result);
                                friendName =  get.getString("nickName");
                                friendResult = get.getString("f_rs");
                                Log.i("rs",friendResult);
                                checkFriend(friendResult);
                            }catch (Exception e){
                                friendName = "검색한 유저가 존재하지 않습니다.";
                                imgview.setVisibility(View.INVISIBLE);
                                friendText.setVisibility(View.INVISIBLE);
                                addFriend.setVisibility(View.INVISIBLE);
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    fName.setText(friendName);
                }
            });
        }
    };
    public void checkFriend(String result){
        if(result.equals("y")){
            Toast.makeText(getApplicationContext(),"이미 친구입니다!",Toast.LENGTH_SHORT).show();
            imgview.setVisibility(View.VISIBLE);
            addFriend.setVisibility(View.INVISIBLE);
            friendText.setVisibility(View.VISIBLE);
        }
    }
    public Bitmap byteArrayToBitmap(String jsonString) {
        Bitmap bitmap = null;
        byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imgview.setImageBitmap(bitmap);
        return bitmap;

    }

}

