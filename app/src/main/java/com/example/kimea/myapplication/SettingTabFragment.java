package com.example.kimea.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import com.example.kimea.myapplication.util.ChatApplication;
import com.example.kimea.myapplication.util.DBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import io.socket.client.Socket;

public class SettingTabFragment extends Fragment{
    JSONObject data = new JSONObject();
    JSONObject data2 = new JSONObject();
    Button setImg,profileSend,logout;
    ImageView imgview,imgview2;
    TextView profile;

    final int REQ_CODE_SELECT_IMAGE=100;
    String encodeImg;
    private Socket mSocket;
    private static final String TAG = "SettingTabFragment";
    SQLiteDatabase db;
    DBHelper helper = new DBHelper(getActivity());
    String myEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatApplication app = (ChatApplication) getActivity().getApplication();
        mSocket = app.getSocket();
        helper = new DBHelper(getActivity().getApplicationContext());
        db = helper.getWritableDatabase();



        SharedPreferences preferences = getActivity().getSharedPreferences("pref",Context.MODE_PRIVATE);
        myEmail = preferences.getString("myEmail","");
        Log.e(TAG,"myEmail = "+myEmail);
        try {
            data2.put("u_email", myEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("sendMyProfile",data2);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment,container,false);
        ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> ac = am.getRunningTasks(1);
        Log.e(TAG,"Check Activity  =  "+ac.get(0).topActivity.getClassName());

        imgview = view.findViewById(R.id.profileImg);
        profile = view.findViewById(R.id.profileText);
        profileSend = view.findViewById(R.id.profileSend);
        logout = view.findViewById(R.id.logout);
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
                try{
                    DBHelper helper2 =  new DBHelper(getActivity());
                    db = helper2.getWritableDatabase();
                    String sql = "select user from divice";
                    Cursor c = db.rawQuery(sql,null);
                    c.moveToFirst();
                    String mPath = "/data/data/" + getActivity().getPackageName() + "/databases";
                    File mFile = new File(mPath);
                    mFile.delete();
                    String user = c.getString(0);
                    JSONObject logout = new JSONObject();
                    logout.put("email",user);
                    getActivity().deleteDatabase("divice.db");
                    mSocket.emit("logout",logout);
                /*db = helper2.getWritableDatabase();
                db.execSQL("drop table if exists divice;");*/

                    Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    //intent.putExtra("drop", "drop");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    SharedPreferences pref = getActivity().getSharedPreferences("pref",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear().apply();
                    db.close();
                    //mSocket.disconnect();
                   // mSocket = null;
                    getActivity().finish();
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        profileSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Drawable d = imgview.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] photo = baos.toByteArray();
                encodeImg = Base64.encodeToString(photo, Base64.DEFAULT);
                String myText = profile.getText().toString();
                db = helper.getWritableDatabase();
                db.execSQL("update myprofile set myimg = '"+encodeImg+"', mytext = '"+myText+"'");
                Log.e(TAG,"DBUpdateProfile");
                try {
                    //String result = new String(photo,"utf-8");
                    // Log.i("asdas",result);
                    data.put("u_pf_img", encodeImg);
                    // data.put("u_pf_img", encodeImg);
                    data.put("u_email",myEmail);
                    data.put("u_pf_text", profile.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mSocket.emit("setProfile",data);
                Log.e(TAG,"SendUpdateProfile");
            }
        });
        try {
            db = helper.getWritableDatabase();
            String sql = "select * from myprofile";
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            byteArrayToBitmap(cursor.getString(0));
            profile.setText(cursor.getString(1));
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
//                    배치해놓은 ImageView에 이미지를 넣어봅시다.
                    imgview.setImageBitmap(bitmap);
//                    Glide.with(mContext).load(data.getData()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView); // OOM 없애기위해 그레들사용
                } catch (Exception e) {
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