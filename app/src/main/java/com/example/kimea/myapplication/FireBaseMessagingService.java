package com.example.kimea.myapplication;


import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.kimea.myapplication.util.DBHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class FireBaseMessagingService extends FirebaseMessagingService{
    private static final String TAG = "MyFirebaseMsgService";
    private int badge;
    int badge_count;
    String result, chatRoom;
    SQLiteDatabase db;
    DBHelper helper =  new DBHelper(this);

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sendRegistrationToServer(s);
    }
    private void sendRegistrationToServer(String token) {

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("badge", badge);
        editor.apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Map<String, String > rsmg = remoteMessage.getData();
        Map<String, String> data = remoteMessage.getData();
        String msgBody = data.get("body");   //메세지 데이터
        String msgTitle = data.get("title"); //닉네임
        String email = data.get("email");    //상대방 식별 이메일
        String f_email = data.get("f_email");
        String roomNick = data.get("roomNickName");
        chatRoom = data.get("roomname");
        String intentRoomName = chatRoom;

        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> ac = am.getRunningTasks(1);
        SharedPreferences getEmail = getSharedPreferences("chatEmail",MODE_PRIVATE);

        String[] array = chatRoom.split("@");
        String ss = array[1];
        String[] ary2 = ss.split("\\.");
        result = array[0] + ary2[0] + ary2[1];
        try{
            db = helper.getReadableDatabase();
            String query = "select * from '"+result+"'";
            Cursor cur = db.rawQuery(query,null);
            cur.moveToFirst();
        }catch (Exception e){
            db = helper.getWritableDatabase();
            db.execSQL("create table '"+result+"'(Chatseq integer primary key autoincrement, ChatId text,ChatNickName text, ChatText text, ChatRoomNickName text, ChatImg BLOB,type TEXT);");
            insert2(chatRoom);
            Log.i("create table","create table");

        }
        db = helper.getReadableDatabase();
        String query = "select user from divice";
        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();
        String mineCheck = cur.getString(0);

        if(email!=null){
            SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            String badge_check = pref.getString(chatRoom,"");
            if(badge_check.isEmpty()){
                editor.putString(chatRoom,"0");
                editor.commit();
            }
            String[] getMessageCheck = msgBody.split("_");
            //현재 상대방의 채팅방에 들어와있으면 메세지를 받지 않음
            if((email.equals(mineCheck)&&!getEmail.getString("email","").equals(chatRoom))||(email.equals(mineCheck)&&getEmail.getString("email","").equals(chatRoom))){
            }else if(!getEmail.getString("email","").equals(chatRoom)){
                if (!getMessageCheck[0].equals("MsgApp")){
                    sendNotification(msgBody, msgTitle,email, roomNick, intentRoomName);
                    addOneBadge();
                }
                Log.e(TAG,"activity"+ac.get(0).topActivity.getClassName());
                try {
                    ((ViewPagerActivity) ViewPagerActivity.CONTEXT).reset();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (getMessageCheck[0].equals("MsgApp")){
                    String url = "http://122.40.72.34:1300/download";
                    httpUrlDownload(url,msgBody, email, msgTitle, roomNick,intentRoomName);
                }else{
                    insert(email,msgTitle,msgBody,"0",roomNick,null);
                }
            }else if(getEmail.getString("email","").equals(chatRoom)){
                if (getMessageCheck[0].equals("MsgApp")){
                    String url = "http://122.40.72.34:1300/download";
                    httpUrlDownload(url, msgBody, email, msgTitle, roomNick,intentRoomName);
                }else{
                    insert(email,msgTitle,msgBody,"0",roomNick,null);
                }
            }

        }
        Log.i("Fire Service", email+"/"+msgTitle+"/"+msgBody+"/"+result);
        db.close();
    }
    public void addOneBadge(){
        SharedPreferences preferences = getSharedPreferences("pref",MODE_PRIVATE);
        int badge_int = Integer.parseInt(preferences.getString(chatRoom,""));
        badge_int++;
        SharedPreferences.Editor editor3 = preferences.edit();
        editor3.putString(chatRoom,String.valueOf(badge_int));
        editor3.apply();
    }
    private void sendNotification(String messageBody,String messageTitle, String intentEmail, String intentRoomNick, String intentRoomName) {
        Intent chatroomIntent = new Intent(this, MainActivity.class);
        Log.e(TAG,"FCMSERVICE-rooomnickname-check "+intentRoomNick);
        chatroomIntent.putExtra("email",intentEmail);
        chatroomIntent.putExtra("roomname",intentRoomName);
        chatroomIntent.putExtra("roomNickName",intentRoomNick);
        chatroomIntent.putExtra("noti","1");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, chatroomIntent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000})
                .setLights(Color.WHITE, 1500, 1500)
                .setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(0 /* ID of notification */, nBuilder.build());

    }

    //데이터 삽입
    public void insert(String id,String nickName,String text,String type,String roomNickName, byte[] img) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤

        // 데이터의 삽입은 put을 이용한다.

        values.put("ChatNickName",nickName);
        values.put("ChatId", id);
        values.put("type",type);
        values.put("ChatRoomNickName", roomNickName);
        values.put("ChatImg",img);
        values.put("ChatText",text);
        db.insert("'"+result+"'", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
        db.close();
        /*try{

        }catch (Exception e){
            db = helper.getWritableDatabase();
            db.execSQL("create table '"+result+"'(Chatseq integer primary key autoincrement, ChatId text,ChatNickName text, ChatText text, type TEXT);");
            db.insert("'"+result+"'", null, values);
            ChattingTabFragment ctf = new ChattingTabFragment();
            ctf.addProfile(id,text,null,"",chatRoom);
            Log.i("create","createGroup");
        }*/
        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.
    }
    private File downloadImg(){
        File storageDir = new File(Environment.getExternalStorageDirectory()+ "/Pictures", "MsgApp");
        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }
        return storageDir;
    }
    public void httpUrlDownload(String url, String Data, String email, String msgTitle, String roomNick, String intentRoomName) {
            try{
                URL connectUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Cache-Control", "no-cache");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                OutputStream out = conn.getOutputStream();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("filename",Data);
                }catch (Exception e){

                }
                out.write(jsonObject.toString().getBytes()); // 출력 스트림에 출력.
                Log.e("ASDASDAS"," "+jsonObject.getString("filename"));
                out.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
                out.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.

                InputStream tmp = conn.getInputStream();
                File file = new File(downloadImg(),Data);
                OutputStream outputStream = new FileOutputStream(file);
                byte data[] = new byte[1024*1024];
                int res = 1;
                // write to file's outputStream
                while ((res = tmp.read(data)) > 0) {
                // Don't use outputStream.write(data) !!!
                    outputStream.write(data, 0, res);
                }
                // clean
                outputStream.flush();
                outputStream.close();
                tmp.close();
                conn.disconnect();
                if (file.exists()){
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize= 3;
                    Bitmap bitmap  = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
                    byte[] getBytes =  getByteArrayFromDrawable(bitmap);
                    Log.e(TAG, "GetPicture to Bitmap = " +bitmap);

                    insert(email,msgTitle,"사진","3",roomNick,getBytes);
                    SharedPreferences getEmail = getSharedPreferences("chatEmail",MODE_PRIVATE);
                    if(!getEmail.getString("email","").equals(chatRoom)){
                        sendNotification("사진", msgTitle, email, roomNick, intentRoomName);
                        addOneBadge();
                        try {
                            ((ViewPagerActivity) ViewPagerActivity.CONTEXT).reset();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    try {
                        ((ChatRoomActivity) ChatRoomActivity.CONTEXT).reset(msgTitle,bitmap);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    public byte[] getByteArrayFromDrawable(Bitmap d) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        d.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();
        return data;
    }
    public void insert2(String id) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values2 = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤

        // 데이터의 삽입은 put을 이용한다.
        values2.put("userId", id);
        db.insert("oneUser", null, values2); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
        db.close();
        Log.i("insert","insert");
        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.
        SQLiteDatabase database = helper.getReadableDatabase();
        String sql = "select * from oneUser;";
        Cursor cursor2 = database.rawQuery(sql, null);
        while(cursor2.moveToNext()){
        }
    }
    public void set_badge_alarm(int badge_count){
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", badge_count);
        intent.putExtra("badge_count_package_name", getPackageName());
        intent.putExtra("badge_count_class_name", "com.example.kimea.myapplication.LoadingActivity");
        sendBroadcast(intent);
    }
}

