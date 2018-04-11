package com.saxophone.saxopia.community;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


/**
 * Created by hyoungjukim on 2018. 4. 9..
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    private CommonUtil commonUtil = new CommonUtil();

    private String URL_LOAD = "http://www.saxopia.com";

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "Refreshed token: " + token);

        //if(commonUtil.cookieChk(URL_LOAD)) {
            // 생성등록된 토큰을 개인 앱서버에 보내 저장해 두었다가 추가 뭔가를 하고 싶으면 할 수 있도록 한다.
            String userId = commonUtil.getCookieInfo(URL_LOAD);
            //sendRegistrationToServer(token, userId);
            sendRegistrationToServer(token);
        //}
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("token", token)
                //.add("userId", userId)
                .build();

        //request
        Request request = new Request.Builder()
                .url("http://www.saxopia.com/fcm/register.php")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
