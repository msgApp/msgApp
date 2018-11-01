package com.example.kimea.myapplication;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

public class FireBaseMessagingService extends FirebaseMessagingService{
    private static final String TAG = "MyFirebaseMsgService";
    int badge_count;
    String result;
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
        editor.putString("token", token);
        editor.commit();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG ,"From:" +remoteMessage.getNotification().getBody());
       // Map<String, String > rsmg = remoteMessage.getData();

        String msgBody = remoteMessage.getNotification().getBody();
        String email = remoteMessage.getData().get("email");
        String msgTitle = remoteMessage.getNotification().getTitle();
        String[] array = email.split("@");
        String ss = array[1];
        String[] ary2 = ss.split("\\.");
        // String result = array[0]+array2[0]+array2[1];
        // Log.i("result3",ary2[0]);
        result = array[0]+ary2[0]+ary2[1];
        insert(email,msgTitle,msgBody,"0");
        sendNotification(msgTitle,msgBody);
        set_alarm_badge();
    }
    private void sendNotification(String messageBody,String messageTitle) {

        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> ac = am.getRunningTasks(1);

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
    public void set_alarm_badge(){
    Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
    badge_count =1;
    intent.putExtra("badge_count", badge_count);
    intent.putExtra("badge_count_package_name", getApplicationContext().getPackageName());
    intent.putExtra("badge_count_class_name", MainActivity.class.getName());
    sendBroadcast(intent);

    }
    //데이터 삽입
    public void insert(String id,String nickName,String text,String type) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤

        // 데이터의 삽입은 put을 이용한다.
        values.put("ChatId", id);
        values.put("ChatNickName",nickName);
        values.put("ChatText",text);
        values.put("type",type);
        db.insert("'"+result+"'", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
        Log.i("insert","insert");
        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.
    }

        }


