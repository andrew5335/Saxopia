package com.saxophone.saxopia.community;

import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
            String phoneNo = "";

            try {
                TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                phoneNo = telManager.getLine1Number();
                if (phoneNo.startsWith("+82")) {
                    phoneNo = phoneNo.replace("+82", "0");
                }

                phoneNo = AES256Chiper.AES_Encode(phoneNo);
                Log.i("phoneno", "===============Phone No : " + phoneNo);
            } catch(SecurityException e) {
                Log.d("Error", "Error : " + e.toString());
            } catch(UnsupportedEncodingException ue) {
                Log.d("Error", "Error : " + ue.toString());
            } catch(NoSuchPaddingException nspe) {
                Log.d("Error", "Error : " + nspe.toString());
            } catch(NoSuchAlgorithmException nsae) {
                Log.d("Error", "Error : " + nsae.toString());
            } catch(InvalidAlgorithmParameterException iape) {
                Log.d("Error", "Error : " + iape.toString());
            } catch(InvalidKeyException ike) {
                Log.d("Error", "Error : " + ike.toString());
            } catch(IllegalBlockSizeException ibe) {
                Log.d("Error", "Error : " + ibe.toString());
            } catch(BadPaddingException be) {
                Log.d("Error", "Error : " + be.toString());
            }

            //sendRegistrationToServer(token);
            sendRegistrationToServer(token, phoneNo);
        //}
    }

    public void sendRegistrationToServer(String token, String phoneNo) {
        // Add custom implementation, as needed.

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("token", token)
                .add("phoneNo", phoneNo)
                .build();

        //request
        Request request = new Request.Builder()
                .url("http://www.saxopia.com/fcm/register2.php")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
