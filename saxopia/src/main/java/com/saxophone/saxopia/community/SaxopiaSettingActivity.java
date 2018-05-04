package com.saxophone.saxopia.community;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaxopiaSettingActivity extends AppCompatActivity {

    private Switch totalAlarm;
    private Switch newPostAlarm;
    private Switch replyAlarm;
    private Switch messageAlarm;
    private Switch time00;
    private Switch time08;
    private Switch time10;

    private TextView alarmSetting;
    private WebView webView;

    private BackPressCloseHandler backPressCloseHandler;

    private static final String TYPE_IMAGE = "*/*";
    private static final int INPUT_FILE_REQUEST_CODE = 1;

    private static final int requestReadPhoneState = 999;

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saxopia_setting);
        webView = (WebView) findViewById(R.id.webview2);

        initSwitch();
    }

    public void initSwitch() {
        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("saxopia_setting", Context.MODE_PRIVATE);
        boolean totalAlarmYn = true;
        boolean newPostAlarmYn = true;
        boolean replyAlarmYn = true;
        boolean messageAlarmYn = true;
        boolean time00Yn = true;
        boolean time08Yn = true;
        boolean time10Yn = true;

        boolean isFirst = sharedPreferences.getBoolean("isFirst", false);
        if(!isFirst) {
            // 최초 실행인 경우 최초 실행 유무값을 true로 저장한다. 이렇게 하면 이후부터는 이 부분을 확인하지 않으므로...
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirst", true);

            // 최초 실행인 경우 앱을 알림을 받을 수 있는 상태로 만들어준다.
            editor.putBoolean("totalAlarm", true);
            editor.putBoolean("newPost", true);
            editor.putBoolean("replyPost", true);
            editor.putBoolean("message", true);
            editor.putBoolean("time00", false);
            editor.putBoolean("time08", false);
            editor.putBoolean("time10", false);

            editor.commit();
        }

        totalAlarmYn = sharedPreferences.getBoolean("totalAlarm", true);
        newPostAlarmYn = sharedPreferences.getBoolean("newPost", true);
        replyAlarmYn = sharedPreferences.getBoolean("replyPost", true);
        messageAlarmYn = sharedPreferences.getBoolean("message", true);
        time00Yn = sharedPreferences.getBoolean("time00", false);
        time08Yn = sharedPreferences.getBoolean("time08", false);
        time10Yn = sharedPreferences.getBoolean("time10", false);

        totalAlarm = (Switch) findViewById(R.id.totalAlarm);
        newPostAlarm = (Switch) findViewById(R.id.newPostAlarm);
        replyAlarm = (Switch) findViewById(R.id.replyAlarm);
        messageAlarm = (Switch) findViewById(R.id.messageAlarm);

        time00 = (Switch) findViewById(R.id.time00);
        time08 = (Switch) findViewById(R.id.time08);
        time10 = (Switch) findViewById(R.id.time10);

        // 만약 sharedPreference에 저장된 값이 true 이면 switch 켜진 상태로...
        if(totalAlarmYn) {
            totalAlarm.setChecked(true);
            if(newPostAlarmYn) {
                newPostAlarm.setChecked(true);
            }
            newPostAlarm.setEnabled(true);

            if(replyAlarmYn) {
                replyAlarm.setChecked(true);
            }
            replyAlarm.setEnabled(true);

            if(messageAlarmYn) {
                messageAlarm.setChecked(true);
            }
            messageAlarm.setEnabled(true);

            if(time00Yn) {
                time00.setChecked(true);
            }
            time00.setEnabled(true);

            if(time08Yn) {
                time08.setChecked(true);
            }
            time08.setEnabled(true);

            if(time10Yn) {
                time10.setChecked(true);
            }
            time10.setEnabled(true);
        } else {
            totalAlarm.setChecked(false);
            newPostAlarm.setChecked(false);
            newPostAlarm.setEnabled(false);
            replyAlarm.setChecked(false);
            replyAlarm.setEnabled(false);
            messageAlarm.setChecked(false);
            messageAlarm.setEnabled(false);
            time00.setChecked(false);
            time00.setEnabled(false);
            time08.setChecked(false);
            time08.setEnabled(false);
            time10.setChecked(false);
            time10.setEnabled(false);
        }

        if(newPostAlarmYn) {
            if(!totalAlarmYn) {
                newPostAlarm.setChecked(false);
                newPostAlarm.setEnabled(false);
            } else {
                newPostAlarm.setChecked(true);
                newPostAlarm.setEnabled(true);
            }
        }

        if(replyAlarmYn) {
            if(!totalAlarmYn) {
                replyAlarm.setChecked(false);
                replyAlarm.setEnabled(false);
            } else {
                replyAlarm.setChecked(true);
                replyAlarm.setEnabled(true);
            }
        }

        if(messageAlarmYn) {
            if(!totalAlarmYn) {
                messageAlarm.setChecked(false);
                messageAlarm.setEnabled(false);
            } else {
                messageAlarm.setChecked(true);
                messageAlarm.setEnabled(true);
            }
        }

        if(time00Yn) {
            if(!totalAlarmYn) {
                time00.setChecked(false);
                time00.setEnabled(false);
            } else {
                time00.setChecked(true);
                time00.setEnabled(true);
            }
        }

        if(time08Yn) {
            if(!totalAlarmYn) {
                time08.setChecked(false);
                time08.setEnabled(false);
            } else {
                time08.setChecked(true);
                time08.setEnabled(true);
            }
        }

        if(time10Yn) {
            if(!totalAlarmYn) {
                time10.setChecked(false);
                time10.setEnabled(false);
            } else {
                time10.setChecked(true);
                time10.setEnabled(true);
            }
        }

        totalAlarm.setOnCheckedChangeListener(new myCheckedChangedListener());
        newPostAlarm.setOnCheckedChangeListener(new myCheckedChangedListener());
        replyAlarm.setOnCheckedChangeListener(new myCheckedChangedListener());
        messageAlarm.setOnCheckedChangeListener(new myCheckedChangedListener());
        time00.setOnCheckedChangeListener(new myCheckedChangedListener());
        time08.setOnCheckedChangeListener(new myCheckedChangedListener());
        time10.setOnCheckedChangeListener(new myCheckedChangedListener());

        alarmSetting = (TextView) findViewById(R.id.ringtoneChange);
        Uri uri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
        String ringTitle = ringtone.getTitle(getApplicationContext());
        Log.i("Ring Title", "============Ring Ttile : " + ringTitle);
        alarmSetting.setText("   알림음 : " + ringTitle);

    }

    class myCheckedChangedListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("saxopia_setting", Context.MODE_PRIVATE);

            if(isChecked) {
                if(buttonView == totalAlarm) {
                    showToast("전체 알림 수신 허용");
                    //totalAlarm.setChecked(false);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("totalAlarm", true);
                    editor.apply();

                    newPostAlarm.setChecked(true);
                    newPostAlarm.setEnabled(true);
                    replyAlarm.setChecked(true);
                    replyAlarm.setEnabled(true);
                    messageAlarm.setChecked(true);
                    messageAlarm.setEnabled(true);
                    time00.setChecked(false);
                    time00.setEnabled(true);
                    time08.setChecked(false);
                    time08.setEnabled(true);
                    time10.setChecked(false);
                    time10.setEnabled(true);
                }

                if(buttonView == newPostAlarm) {
                    showToast("새글 알림 수신 허용");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("newPost", true);
                    editor.apply();
                }

                if(buttonView == replyAlarm) {
                    showToast("답글 알림 수신 허용");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("replyPost", true);
                    editor.apply();
                }

                if(buttonView == messageAlarm) {
                    showToast("답글 알림 수신 허용");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("message", true);
                    editor.apply();
                }

                if(buttonView == time00) {
                    showToast("00시 ~ 24시 알림 수신 허용");
                    time08.setChecked(false);
                    time10.setChecked(false);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("time00", true);
                    editor.apply();
                }

                if(buttonView == time08) {
                    showToast("08시 ~ 20시 알림 수신 허용");
                    time00.setChecked(false);
                    time10.setChecked(false);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("time08", true);
                    editor.apply();
                }

                if(buttonView == time10) {
                    showToast("10시 ~ 18시 알림 수신 허용");
                    time00.setChecked(false);
                    time08.setChecked(false);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("time10", true);
                    editor.apply();
                }
            } else {
                if(buttonView == totalAlarm) {
                    showToast("전체 알림 수신 거부");
                    //totalAlarm.setChecked(true);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("totalAlarm", false);
                    editor.apply();

                    newPostAlarm.setChecked(false);
                    newPostAlarm.setEnabled(false);
                    replyAlarm.setChecked(false);
                    replyAlarm.setEnabled(false);
                    messageAlarm.setChecked(false);
                    messageAlarm.setEnabled(false);
                    time00.setChecked(false);
                    time00.setEnabled(false);
                    time08.setChecked(false);
                    time08.setEnabled(false);
                    time10.setChecked(false);
                    time10.setEnabled(false);
                }

                if(buttonView ==  newPostAlarm) {
                    showToast("새글 알림 수신 거부");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("newPost", false);
                    editor.apply();
                }

                if(buttonView == replyAlarm) {
                    showToast("답글 알림 수신 거부");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("replyPost", false);
                    editor.apply();
                }

                if(buttonView == messageAlarm) {
                    showToast("답글 알림 수신 거부");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("message", false);
                    editor.apply();
                }

                if(buttonView == time00) {
                    showToast("00시 ~ 24시 알림 수신 거부");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("time00", false);
                    editor.apply();
                }

                if(buttonView == time08) {
                    showToast("08시 ~ 20시 알림 수신 거부");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("time08", false);
                    editor.apply();
                }

                if(buttonView == time10) {
                    showToast("10시 ~ 18시 알림 수신 거부");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("time10", false);
                    editor.apply();
                }
            }

        }

    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void onRingtoneChange(View v) {
        //showToast("ringtone change click!!!");
        //Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        boolean permission = false;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(this);
        } else {
            permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }

        Intent ringtoneIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        Uri uri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION);
        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "알림음 설정");
        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);


        if(permission) {
            startActivityForResult(ringtoneIntent, 0);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Toast.makeText(getApplicationContext(), "알림음 변경을 위해서는 시스템 권한 설정이 필요합니다.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                this.startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("request code", "==========RequestCode : " + requestCode);
        switch(requestCode) {
            case 0 :
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION, uri);
                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
                String ringTitle = ringtone.getTitle(getApplicationContext());
                Log.i("Ring Title", "============Ring Ttile : " + ringTitle);
                alarmSetting.setText("   알림음 : " + ringTitle);

                break;
        }

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

    // 출석체크 버튼 클릭
    public void onAttendClicked(View v) {
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.content);
        String attendUrl = "http://www.saxopia.com/calendar_view.php";

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        WebViewInterface webViewInterface = new WebViewInterface(this, webView);
        webView.addJavascriptInterface(webViewInterface, "saxopia");

        webView.getSettings().setBuiltInZoomControls(true);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            webView.getSettings().setDisplayZoomControls(false);
        }
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            webView.getSettings().setTextZoom(100);

        webView.setWebViewClient(new WebViewClient());

        contentLayout.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        webView.setVerticalScrollBarEnabled(false);
        webView.loadUrl(attendUrl);

        this.backPressCloseHandler = new BackPressCloseHandler(this);
    }

    // 회원정보수정 버튼 클릭
    public void onMemModifyClicked(View v) {
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.content);
        String modifyUrl = "http://www.saxopia.com/happy_member.php?mode=modify";

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        WebViewInterface webViewInterface = new WebViewInterface(this, webView);
        webView.addJavascriptInterface(webViewInterface, "saxopia");

        webView.getSettings().setBuiltInZoomControls(true);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            webView.getSettings().setDisplayZoomControls(false);
        }
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            webView.getSettings().setTextZoom(100);

        webView.setWebViewClient(new WebViewClient());

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

        });

        contentLayout.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        //webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setOnTouchListener(new View.OnTouchListener() {
            float m_downX;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //return (event.getAction() == MotionEvent.ACTION_MOVE);
                //return false;
                if(event.getPointerCount() > 1) {
                    return true;
                }

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        m_downX = event.getX();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE:{}
                    case MotionEvent.ACTION_CANCEL:{}
                    case MotionEvent.ACTION_UP: {
                        event.setLocation(m_downX, event.getY());
                        break;
                    }
                }

                return false;
            }
        });
        webView.loadUrl(modifyUrl);

        this.backPressCloseHandler = new BackPressCloseHandler(this);
    }

    // 설정버튼 클릭
    public void onSettingClicked(View v) {
        //Intent settingIntent = new Intent(getApplicationContext(), SaxopiaSettingActivity.class);
        //startActivity(settingIntent);
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.content);
        webView.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
    }

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
}
