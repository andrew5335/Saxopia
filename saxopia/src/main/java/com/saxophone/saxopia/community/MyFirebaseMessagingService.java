package com.saxophone.saxopia.community;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.*;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage.getData().get("message"));
    }

    private void sendNotification(String messageBody) {
        String link = "";
        String message = "";
        String gu = "";
        //Intent intent = new Intent(this, MainActivity.class);

        try {
            JSONObject obj = new JSONObject(messageBody);
            link = obj.getString("link");
            message = obj.getString("message");
            gu = obj.getString("gu");
        }catch(Exception e){
            Log.e("Error", "Error : " + e.toString());
        }


        //if(null != link && !"".equalsIgnoreCase(link)) {
            Intent intent = new Intent(this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("url", link);
            intent.putExtras(bundle);
        //}

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        int icon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.mipmap.saxopia_icon2 : R.mipmap.saxopia_icon2;

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle("색소피아 알림")
                .setContentText(message)
                .setLargeIcon(bm)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        SharedPreferences sharedPreferences = this.getSharedPreferences("saxopia_setting", Context.MODE_PRIVATE);
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        int curTime = 0;

        boolean notiYn = false;
        boolean newPost = false;
        boolean replyPost = false;
        boolean messageYn = false;
        boolean time00 = false;
        boolean time08 = false;
        boolean time10 = false;

        notiYn = sharedPreferences.getBoolean("totalAlarm", false);    // 전체 알림 허용 유무
        newPost = sharedPreferences.getBoolean("newPost", false);    // 새글 알림 허용 유무
        replyPost = sharedPreferences.getBoolean("replyPost", false);    // 답글 알림 허용 유무
        messageYn = sharedPreferences.getBoolean("message", false);    // 쪽지 알림 허용 유무
        time00 = sharedPreferences.getBoolean("time00", false);    // 전 시간 허용 유무
        time08 = sharedPreferences.getBoolean("time08", false);    // 08시 ~ 20시만 알림 허용 유무 (08 ~ 20시 이외 시간에는 알림 수신 차단)
        time10 = sharedPreferences.getBoolean("time10", false);    // 10시 ~ 18시만 알림 허용 유무 (10 ~ 18시 이외 시간에는 알림 수신 차단)

        curTime = Integer.parseInt(sdf.format(today));

        Log.i("Noti Info2", "=======================Noti Info2 : " + notiYn);

        // 알림 설정이 전체 알림을 받을 수 있도록 해둔 경우 새글, 답글, 시간별 허용 유무를 확인하여 알림이 가도록 한다.
        if(notiYn) {
            if(gu.equalsIgnoreCase("noti")) {
                // 공지사항은 무조건 수신
                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            } else {
                if(!time00 && !time08 && !time10) {
                    // 시간대 설정이 되어있지 않은 경우 기본 시간대 적용 (09 ~ 20시)
                    if(curTime > 9 && curTime < 20) {
                        // 기본 시간대에서 새글, 상품관련 글이면서 새글 알림 수신이 허용되어 있을 경우
                        if(newPost && (gu.equalsIgnoreCase("noti2") || gu.equalsIgnoreCase("post") || gu.equalsIgnoreCase("goods"))) {
                            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                        }

                        // 기본 시간대에서 답글이면서 답글 알림 수신이 허용되어 있을 경우
                        if(replyPost && (gu.equalsIgnoreCase("reply") || gu.equalsIgnoreCase("comment"))) {
                            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                        }

                        if(replyPost && gu.equalsIgnoreCase("comment")) {
                            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                        }

                        if(messageYn && gu.equalsIgnoreCase("message")) {
                            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                        }
                    }
                } else {
                    // 시간대가 설정이 되어 있는 경우
                    if(time00) {
                        // 전 시간대 허용으로 되어 있을 경우 새글, 상품관련 글이면서 새글 알림 수신이 허용되어 있을 경우
                        if(newPost && (gu.equalsIgnoreCase("noti2") || gu.equalsIgnoreCase("post") || gu.equalsIgnoreCase("goods"))) {
                            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                        }

                        // 전 시간대 허용으로 되어 있을 경우, 답글이면서 답글 알림 수신이 허용되어 있을 경우
                        if(replyPost && (gu.equalsIgnoreCase("reply") || gu.equalsIgnoreCase("comment"))) {
                            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                        }

                        if(replyPost && gu.equalsIgnoreCase("comment")) {
                            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                        }

                        if(messageYn && gu.equalsIgnoreCase("message")) {
                            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                        }
                    } else {
                        if(time08 && !time10) {
                            // 08 ~ 20시가 허용되어 있는 경우
                            if(curTime > 8 && curTime < 20) {
                                if(newPost && (gu.equalsIgnoreCase("noti2") || gu.equalsIgnoreCase("post") || gu.equalsIgnoreCase("goods"))) {
                                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                                }

                                if(replyPost && (gu.equalsIgnoreCase("reply") || gu.equalsIgnoreCase("comment"))) {
                                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                                }

                                if(replyPost && gu.equalsIgnoreCase("comment")) {
                                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                                }

                                if(messageYn && gu.equalsIgnoreCase("message")) {
                                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                                }
                            }
                        } else if(!time08 && time10) {
                            if(curTime > 10 && curTime < 18) {
                                if(newPost && (gu.equalsIgnoreCase("noti2") || gu.equalsIgnoreCase("post") || gu.equalsIgnoreCase("goods"))) {
                                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                                }

                                if(replyPost && (gu.equalsIgnoreCase("reply") || gu.equalsIgnoreCase("comment"))) {
                                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                                }

                                if(replyPost && gu.equalsIgnoreCase("comment")) {
                                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                                }

                                if(messageYn && gu.equalsIgnoreCase("message")) {
                                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                                }
                            }
                        }
                    }
                }
            }

            //notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        } else {
            // 알림 설정이 전체 알림을 받을 수 없도록 되어 있더라도 관리자가 등록한 공지사항은 무조건 받아야 한다.
            if(gu == "noti") {
                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            }
        }
    }

}
