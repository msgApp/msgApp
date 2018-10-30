package com.example.kimea.myapplication;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FireBaseMessagingService extends FirebaseMessagingService{
    private static final String TAG = "MyFirebaseMsgService";
    int badge_count;
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e(TAG,"token "+s);
        sendRegistrationToServer(s);
    }
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG ,"From:" +remoteMessage.getNotification().getBody());
       // Map<String, String > rsmg = remoteMessage.getData();
        String msgBody = remoteMessage.getNotification().getBody();
        String msgTitle = remoteMessage.getNotification().getTitle();
        sendNotification(msgBody,msgTitle);
        set_alarm_badge();
    }
    private void sendNotification(String messageBody,String messageTitle) {
        Intent intent = new Intent(this, ChatRoomActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String ch = getString(R.string.default_web_client_id);
        Log.e(TAG,"ch "+ch);
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

        }


