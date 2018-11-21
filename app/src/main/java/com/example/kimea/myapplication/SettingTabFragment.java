package com.example.kimea.myapplication;

import android.app.Activity;

import android.content.Intent;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;


import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SettingTabFragment extends Fragment{
    JSONObject data = new JSONObject();
    JSONObject data2 = new JSONObject();
    Button setImg,profileSend,logout;
    ImageView imgview,imgview2;
    TextView profile;
    JSONObject pList;
    final int REQ_CODE_SELECT_IMAGE=100;
    String encodeImg;
    private Socket mSocket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatApplication app = (ChatApplication) getActivity().getApplication();
        mSocket = app.getSocket();

        String ids = getActivity().getIntent().getStringExtra("id");
        try {
            data2.put("u_email", ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("sendMyProfile",data2);


        mSocket.on("sendMyProfile",profile2);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.i("createView3","createView");
        View view = inflater.inflate(R.layout.setting_fragment,container,false);

        imgview = view.findViewById(R.id.profileImg);
        profile = view.findViewById(R.id.profileText);
        profileSend = view.findViewById(R.id.profileSend);
        logout = view.findViewById(R.id.logout);
        profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ChangeProfileText.class);
                startActivity(intent);
            }
        });
        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                 startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            SQLiteDatabase db;
            @Override
            public void onClick(View v) {
                DBHelper helper2 =  new DBHelper(getActivity());
                getActivity().deleteDatabase("divice.db");
                /*db = helper2.getWritableDatabase();
                db.execSQL("drop table if exists divice;");*/
                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                //intent.putExtra("drop", "drop");
                startActivity(intent);
            }
        });

        profileSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Drawable d = imgview.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] photo = baos.toByteArray();
                encodeImg = Base64.encodeToString(photo, Base64.DEFAULT);
                // for (byte b:photo){
                //  Log.i("img",String.valueOf(b));
                //   }
                try {
                    //String result = new String(photo,"utf-8");
                    // Log.i("asdas",result);
                    data.put("u_pf_img", encodeImg);
                    // data.put("u_pf_img", encodeImg);
                    data.put("u_pf_text", profile.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mSocket.emit("setProfile",data);
            }
        });
        return view;
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
                        Log.i("text",profileText);
                        profile.setText(profileText);
                        // Log.i("nickName&img", setUserNickname+", "+setUserImg);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    byteArrayToBitmap(setUserImg);
                }
            });
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
//                    배치해놓은 ImageView에 이미지를 넣어봅시다.
                    imgview.setImageBitmap(bitmap);
//                    Glide.with(mContext).load(data.getData()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView); // OOM 없애기위해 그레들사용
                } catch (Exception e) {
                    Log.e("test", e.getMessage());
                }
            }
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