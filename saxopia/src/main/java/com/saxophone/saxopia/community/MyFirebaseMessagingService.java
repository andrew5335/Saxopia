package com.saxophone.saxopia.community;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.*;

import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        SharedPreferences sharedPreferences = this.getSharedPreferences("saxopia_setting", Context.MODE_PRIVATE);
        boolean notiYn = false;
        notiYn = sharedPreferences.getBoolean("saxopia_noti", false);
        Log.i("Noti Info", "=======================Noti Info : " + notiYn);
        //추가한것
        if(notiYn) {
            sendNotification(remoteMessage.getData().get("message"));
        }
    }

    private void sendNotification(String messageBody) {
        String link = "";
        String message = "";

        try {
            JSONObject obj = new JSONObject(messageBody);
            link = obj.getString("link");
            message = obj.getString("message");
        }catch(Exception e){
            Log.e("Error", "Error : " + e.toString());
        }

        Intent intent = new Intent(this, MainActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("url", link);
        intent.putExtras(bundle);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(com.saxophone.saxopia.community.R.mipmap.ic_launcher)
                .setContentTitle("색소피아 알림")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        SharedPreferences sharedPreferences = this.getSharedPreferences("saxopia_setting", Context.MODE_PRIVATE);
        boolean notiYn = false;
        notiYn = sharedPreferences.getBoolean("saxopia_noti", false);
        Log.i("Noti Info2", "=======================Noti Info2 : " + notiYn);

        if(notiYn) {
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }

}
