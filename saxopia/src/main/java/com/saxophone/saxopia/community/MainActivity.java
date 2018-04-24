package com.saxophone.saxopia.community;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {
    private static String URL_LOAD = "http://www.saxopia.com";
    private static final String TYPE_IMAGE = "*/*";
    private static final int INPUT_FILE_REQUEST_CODE = 1;

    private static final int requestReadPhoneState = 999;

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    private BackPressCloseHandler backPressCloseHandler;

    private CommonUtil commonUtil = new CommonUtil();

    private WebViewInterface webViewInterface;

    private LinearLayout customViewContainer;

    private View customView;

    private WebChromeClient.CustomViewCallback customViewCallback;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        customViewContainer = (LinearLayout) findViewById(R.id.customViewContainer);
        customViewContainer.setOrientation(LinearLayout.VERTICAL);

        webView  = (WebView) findViewById(R.id.webview_);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webViewInterface = new WebViewInterface(MainActivity.this, webView);
        webView.addJavascriptInterface(webViewInterface, "saxopia");

        // Enable pinch to zoom without the zoom buttons
        webView.getSettings().setBuiltInZoomControls(true);
        // Enable pinch to zoom without the zoom buttons
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            webView.getSettings().setDisplayZoomControls(false);
        }
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            webView.getSettings().setTextZoom(100);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_PHONE_STATE)) {

                    dialog.setTitle("권한 요청");
                    dialog.setMessage("색소피아 밴드스토리 알림설정을 위한 전화 권한 허용이 필요합니다. 확인해주세요.");

                    // 확인 버튼 설정
                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                            startActivityForResult(intent, 0);

                            //dialog.dismiss();
                        }
                    });

                    // 취소 버튼 설정
                    dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                } else {

                    dialog.setTitle("권한 허용 요청");
                    dialog.setMessage("색소피아 밴드스토리 알림설정을 위한 전화 권한 허용이 필요합니다. 확인해주세요.");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.READ_PHONE_STATE}, requestReadPhoneState);
                        }
                    });


                    //dialog.setNegativeButton("거부", new DialogInterface.OnClickListener() {
                    //    @Override
                    //    public void onClick(DialogInterface dialog, int which) {
                    //        //setResult(1);
                    //        //dialog.dismiss();
                    //        ActivityCompat.requestPermissions(MainActivity.this,
                    //                new String[]{android.Manifest.permission.READ_PHONE_STATE}, 0);
                    //    }
                    //});


                    dialog.show();

                }

            }
        }

        final Activity activity = this;

        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onCloseWindow(WebView w) {
                super.onCloseWindow(w);
                finish();
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
                final WebSettings settings = view.getSettings();
                settings.setDomStorageEnabled(true);
                settings.setJavaScriptEnabled(true);
                settings.setAllowFileAccess(true);
                settings.setAllowContentAccess(true);
                view.setWebChromeClient(this);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(view);
                resultMsg.sendToTarget();
                return false;
            }

            // For Android Version < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                //System.out.println("WebViewActivity OS Version : " + Build.VERSION.SDK_INT + "\t openFC(VCU), n=1");
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(TYPE_IMAGE);
                startActivityForResult(intent, INPUT_FILE_REQUEST_CODE);
            }

            // For 3.0 <= Android Version < 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                //System.out.println("WebViewActivity 3<A<4.1, OS Version : " + Build.VERSION.SDK_INT + "\t openFC(VCU,aT), n=2");
                openFileChooser(uploadMsg, acceptType, "");
            }

            // For 4.1 <= Android Version < 5.0
            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                Log.d(getClass().getName(), "openFileChooser : "+acceptType+"/"+capture);
                mUploadMessage = uploadFile;
                imageChooser();
            }

            // For Android Version 5.0+
            // Ref: https://github.com/GoogleChrome/chromium-webview-samples/blob/master/input-file-example/app/src/main/java/inputfilesample/android/chrome/google/com/inputfilesample/MainFragment.java
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                System.out.println("WebViewActivity A>5, OS Version : " + Build.VERSION.SDK_INT + "\t onSFC(WV,VCUB,FCP), n=3");
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;
                imageChooser();
                return true;
            }

            private void imageChooser() {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(getClass().getName(), "Unable to create Image File", ex);
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:"+photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType(TYPE_IMAGE);

                Intent[] intentArray;
                if(takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
            }

            @Override
            public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
                //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                super.onShowCustomView(view, callback);
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                    if (customView != null) {
                        callback.onCustomViewHidden();
                        return;
                    }

                    customView = view;
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    //activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    webView.setVisibility(View.GONE);
                    customViewContainer.setVisibility(View.VISIBLE);
                    customViewContainer.setBackgroundColor(000000);
                    customViewContainer.addView(view);
                    customViewCallback = callback;
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();

                if(customView == null) {
                    return;
                }

                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                webView.setVisibility(View.VISIBLE);
                customViewContainer.setVisibility(View.GONE);
                customView.setVisibility(View.GONE);
                customViewContainer.removeView(customView);
                customViewCallback.onCustomViewHidden();

                customView = null;
            }
        });
        //webView.setWebViewClient(new SaxopiaWebViewClient());
        //webView.setWebViewClient(new WebViewClient());
        webView.setWebViewClient(new SaxopiaClient());

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String targetUrl = "";

        if(bundle != null) {
            if(bundle.getString("url") != null && !bundle.getString("url").equalsIgnoreCase("")) {
                targetUrl = bundle.getString("url");
            }
        }

        if(null != targetUrl && !"".equalsIgnoreCase(targetUrl)) {
          URL_LOAD = URL_LOAD + targetUrl;
        }
        webView.loadUrl(URL_LOAD);

        //if(commonUtil.cookieChk(URL_LOAD)) {
        // Firebase FCM Message 수신 설정
            FirebaseMessaging.getInstance().subscribeToTopic("news");
            //FirebaseMessaging.getInstance().subscribeToTopic("test");
            FirebaseInstanceId.getInstance().getToken();

        //}

        this.backPressCloseHandler = new BackPressCloseHandler(this);

        /**
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                getPhoneNoAndSaveToken();
            }
        }
         **/

    }

    /**
     * More info this method can be found at
     * http://developer.android.com/training/camera/photobasics.html
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INPUT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (mFilePathCallback == null) {
                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                }
                Uri[] results = new Uri[]{getResultUri(data)};

                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;
            } else {
                if (mUploadMessage == null) {
                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                }
                Uri result = getResultUri(data);

                Log.d(getClass().getName(), "openFileChooser : "+result);
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        } else {
            if (mFilePathCallback != null) mFilePathCallback.onReceiveValue(null);
            if (mUploadMessage != null) mUploadMessage.onReceiveValue(null);
            mFilePathCallback = null;
            mUploadMessage = null;
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private Uri getResultUri(Intent data) {
        Uri result = null;
        if(data == null || TextUtils.isEmpty(data.getDataString())) {
            // If there is not data, then we may have taken a photo
            if(mCameraPhotoPath != null) {
                result = Uri.parse(mCameraPhotoPath);
            }
        } else {
            String filePath = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                filePath = data.getDataString();
            } else {
                filePath = "file:" + RealPathUtil.getRealPath(this, data.getData());
            }
            result = Uri.parse(filePath);
        }

        return result;
    }

    public void onNav001Clicked(View v) {
        //WebView mainWebView = (WebView) findViewById(R.id.webview_);
        //Toast.makeText(getApplicationContext(), "nav001", Toast.LENGTH_SHORT).show();
        //mainWebView.loadUrl("http://www.saxopia.com");

        /**
        String curUrl = webView.getUrl();
        if(curUrl.contains("bbs_index.php?file1=member_list.html&file2=member_list.html") ||
                curUrl.contains("happy_message.php")) {
            webView.loadUrl("http://www.saxopia.com");
        } else {
            webView.loadUrl("javascript:showLayer('all_layer')");
        }
         **/

        /** 안드로이드 제공 기본 SettingsActivity 연결
        Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
         **/
        Intent settingIntent = new Intent(MainActivity.this, SaxopiaSettingActivity.class);
        startActivity(settingIntent);

    }

    // 내글 버튼 - 내글 페이지 연결
    public void onNav002Clicked(View v) {
        //WebView mainWebView = (WebView) findViewById(R.id.webview_);
        //Toast.makeText(getApplicationContext(), "준비중입니다", Toast.LENGTH_LONG).show();
        //if(commonUtil.cookieChk(URL_LOAD)) {
            webView.loadUrl("http://www.saxopia.com/html_file.php?file=my_write_list.html");
        //} else {
        //    Toast.makeText(getApplicationContext(), "로그인 후 사용이 가능합니다.", Toast.LENGTH_LONG).show();
        //    webView.loadUrl("http://www.saxopia.com/happy_member_login.php");
        //}
    }

    // 받은 댓글 버튼 - 받은 댓글 페이지 연결
    public void onNav003Clicked(View v) {
        //WebView mainWebView = (WebView) findViewById(R.id.webview_);
        //if(commonUtil.cookieChk(URL_LOAD)) {
            webView.loadUrl("http://www.saxopia.com/html_file.php?file=my_board_alarm_list.html");
        //} else {
        //    Toast.makeText(getApplicationContext(), "로그인 후 사용이 가능합니다.", Toast.LENGTH_LONG).show();
        //    webView.loadUrl("http://www.saxopia.com/happy_member_login.php");
        //}
    }

    // 방송 버튼 - 현재 준비중 텍스트 표시
    public void onNav004Clicked(View v) {
        Toast.makeText(getApplicationContext(), "준비중입니다.", Toast.LENGTH_LONG).show();
    }

    // 쪽지함 버튼 - 쪽지함 연결
    public void onNav005Clicked(View v) {
        //WebView mainWebView = (WebView) findViewById(R.id.webview_);
        //if(commonUtil.cookieChk(URL_LOAD)) {
            webView.loadUrl("http://www.saxopia.com/happy_message.php");
        //} else {
        //    Toast.makeText(getApplicationContext(), "로그인 후 사용이 가능합니다.", Toast.LENGTH_LONG).show();
        //    webView.loadUrl("http://www.saxopia.com/happy_member_login.php");
        //}
    }

    // 스크랩 버튼 -
    public void onNav006Clicked(View v) {
        //WebView mainWebView = (WebView) findViewById(R.id.webview_);
        //Log.i("=========cookieCheck : ", String.valueOf(cookieChk()));
        //if(commonUtil.cookieChk(URL_LOAD)) {
            webView.loadUrl("http://www.saxopia.com/my_zzim.php");
        //} else {
        //    Toast.makeText(getApplicationContext(), "로그인 후 사용이 가능합니다.", Toast.LENGTH_LONG).show();
        //    webView.loadUrl("http://www.saxopia.com/happy_member_login.php");
        //}
    }

    // 종료버튼 - 앱을 종료한다
    public void onNav007Clicked(View v) {
        Toast.makeText(getApplicationContext(), "앱을 종료합니다.", Toast.LENGTH_LONG).show();
        ActivityCompat.finishAffinity(this);
        System.runFinalizersOnExit(true);
        System.exit(0);
    }

    // 로그인 유무 확인
    /**
    @SuppressLint("LongLogTag")
    public boolean cookieChk() {
        boolean result = false;

        String cookieStr = "";

        CookieManager cookieManager = null;
        cookieManager = CookieManager.getInstance();

        cookieStr = cookieManager.getCookie(URL_LOAD);

        String[] cookieArr = cookieStr.split(";");

        if(cookieArr != null &  0 < cookieArr.length) {
            for(int i=0; i < cookieArr.length; i++) {
                if(cookieArr[i].contains("appCookie")) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }
    **/

    public void onBackPressed() {
        WebView mainWebView = (WebView) findViewById(R.id.webview_);
        Log.d("log", "onBackPressed: " + mainWebView.getUrl
                ());
        if (mainWebView.canGoBack()) {
            if(mainWebView.getUrl().equalsIgnoreCase(URL_LOAD)) {
                Log.d("log", "onBackPressed2: " + mainWebView.getUrl());
                this.backPressCloseHandler.onBackPressed();
            } else {
                mainWebView.goBack();
            }
        } else {
            this.backPressCloseHandler.onBackPressed();
        }
    }

    public class SaxopiaClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            //boolean override = false;
            //view.loadUrl(url);
            Log.i("url info", url);
            if(url.startsWith("http://")) {
                view.loadUrl(url);
                return true;
            } else if(url.startsWith("https://")){
                view.loadUrl(url);
                return true;
            } else {
                boolean override = false;
                url = url.replace("videohttp", "http");

//            	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            	intent.addCategory(Intent.CATEGORY_BROWSABLE);
//            	intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());

                try {
                    //Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent = intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    ex.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                override = true;

                return override;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case requestReadPhoneState: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "권한이 허용되었습니다.", Toast.LENGTH_LONG).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    // 권한이 허용되면 토큰 및 전화번호를 저장한다.
                    getPhoneNoAndSaveToken();

                } else {
                    Toast.makeText(getApplicationContext(), "권한이 차단되었습니다.", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    public void getPhoneNoAndSaveToken() {
        String phoneNo = "";

        try {
            TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            phoneNo = telManager.getLine1Number();
            Log.i("phone No", "=====================Phone No : " + phoneNo);
            if (phoneNo.startsWith("+82")) {
                phoneNo = phoneNo.replace("+82", "0");
            }
        } catch(SecurityException e) {
            Log.d("Error", "Error : " + e.toString());
        }

        String token = "";
        if(phoneNo != null && !"".equalsIgnoreCase(phoneNo)) {
            token = FirebaseInstanceId.getInstance().getToken();
        }
        //sendRegistrationToServer(token);
        sendRegistrationToServer(token, phoneNo);
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
