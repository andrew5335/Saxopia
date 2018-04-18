package com.saxophone.saxopia.community;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.CookieManager;

/**
 * Created by hyoungjukim on 2018. 4. 9..
 */

public class CommonUtil {

    @SuppressLint("LongLogTag")
    public boolean cookieChk(String url) {
        boolean result = false;

        String cookieStr = "";

        CookieManager cookieManager = null;
        cookieManager = CookieManager.getInstance();

        cookieStr = cookieManager.getCookie(url);

        if(null != cookieStr && !"".equals(cookieStr)) {
            String[] cookieArr = cookieStr.split(";");

            if (cookieArr != null && 0 < cookieArr.length) {
                for (int i = 0; i < cookieArr.length; i++) {
                    Log.i("cookie info", cookieArr[i]);
                    if (cookieArr[i].contains("appCookie")) {
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }

    public String getCookieInfo(String url) {
        String result = "";
        String cookieStr = "";

        CookieManager cookieManager = null;
        cookieManager = CookieManager.getInstance();

        cookieStr = cookieManager.getCookie(url);

        if(null != cookieStr && !"".equals(cookieStr)) {
            String[] cookieArr = cookieStr.split(";");

            // 회원 아이디값은 appCookie에 담겨있으므로 appCookie값을 찾아 result에 담아준다
            if (cookieArr != null && 0 < cookieArr.length) {
                for (int i = 0; i < cookieArr.length; i++) {
                    if (cookieArr[i].contains("appCookie")) {
                        result = cookieArr[i].split("=")[1];
                        break;
                    }
                }
            }
        }

        return result;
    }
}
