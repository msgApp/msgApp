package com.example.kimea.myapplication;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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
        Log.e(TAG,"token "+s);
        sendRegistrationToServer(s);
    }
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("badge", badge);
        editor.commit();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG ,"From:" +remoteMessage.getNotification().getBody());
       // Map<String, String > rsmg = remoteMessage.getData();

        String msgBody = remoteMessage.getNotification().getBody();
        String msgTitle = remoteMessage.getNotification().getTitle();
        String email = remoteMessage.getData().get("email");
        chatRoom = remoteMessage.getData().get("roomname");
        String groupYN = remoteMessage.getData().get("group");
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
            insert(email,msgTitle,msgBody,"0");

        }catch (Exception e){
            db = helper.getWritableDatabase();
            db.execSQL("create table '"+result+"'(Chatseq integer primary key autoincrement, ChatId text,ChatNickName text, ChatText text, type TEXT);");
            insert(email,msgTitle,msgBody,"0");
            insert2(chatRoom);
        }


        if(email!=null){
            SharedPreferences pref = getSharedPreferences(chatRoom,MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            String badge_check = pref.getString("badge_count","");
            if(badge_check.isEmpty()){
                editor.putString("badge_count","0");
                editor.commit();
            }
            //현재 상대방의 채팅방에 들어와있으면 메세지를 받지 않음
            if(!ac.get(0).topActivity.getClassName().equals("com.example.kimea.myapplication.ChatRoomActivity")&&!getEmail.getString("email","").equals(chatRoom)){
                SharedPreferences preferences = getSharedPreferences(chatRoom,MODE_PRIVATE);
                int badge_int = Integer.parseInt(preferences.getString("badge_count",""));
                badge_int++;
                SharedPreferences.Editor editor3 = preferences.edit();
                editor3.putString("badge_count",String.valueOf(badge_int));
                editor3.commit();
                sendNotification(msgBody,msgTitle);
                ((ViewPagerActivity)ViewPagerActivity.CONTEXT).reset();
                Log.e(TAG,"email_badge_commit");
            }
        }

        Log.i("Fire Service", email+"/"+msgTitle+"/"+msgBody+"/"+result);



    }
    private void sendNotification(String messageBody,String messageTitle) {
        Intent intent = new Intent(this, ChatRoomActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
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
    public void insert(String id,String nickName,String text,String type) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤

        // 데이터의 삽입은 put을 이용한다.

        values.put("ChatNickName",nickName);
        values.put("ChatId", id);
        values.put("type",type);
        values.put("ChatText",text);
        db.insert("'"+result+"'", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
        /*try{

        }catch (Exception e){
            db = helper.getWritableDatabase();
            db.execSQL("create table '"+result+"'(Chatseq integer primary key autoincrement, ChatId text,ChatNickName text, ChatText text, type TEXT);");
            db.insert("'"+result+"'", null, values);
            ChattingTabFragment ctf = new ChattingTabFragment();
            ctf.addProfile(id,text,null,"",chatRoom);
            Log.i("create","createGroup");
        }*/
        Log.i("insert","insert");
        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.
    }
    public void insert2(String id) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values2 = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤

        // 데이터의 삽입은 put을 이용한다.
        values2.put("userId", id);
        db.insert("oneUser", null, values2); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
        Log.i("insert","insert");
        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.
        SQLiteDatabase database = helper.getReadableDatabase();
        String sql = "select * from oneUser;";
        Cursor cursor2 = database.rawQuery(sql, null);
        while(cursor2.moveToNext()){
            Log.i("id1",cursor2.getString(0));
            Log.i("id1",cursor2.getString(1));
        }
    }
    public void set_badge_alarm(int badge_count){
        Log.e(TAG , "badgeCount :"+badge_count);
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", badge_count);
        intent.putExtra("badge_count_package_name", getPackageName());
        intent.putExtra("badge_count_class_name", "com.example.kimea.myapplication.LoadingActivity");
        sendBroadcast(intent);
    }
        }


