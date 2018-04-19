package com.saxophone.saxopia.community;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class SaxopiaSettingActivity extends AppCompatActivity {

    private Switch totalAlarm;
    private Switch newPostAlarm;
    private Switch replyAlarm;
    private Switch messageAlarm;
    private Switch time00;
    private Switch time08;
    private Switch time10;

    private TextView alarmSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saxopia_setting);

        initSwitch();
    }

    public void initSwitch() {
        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("saxopia_setting", Context.MODE_PRIVATE);
        boolean totalAlarmYn = false;
        boolean newPostAlarmYn = false;
        boolean replyAlarmYn = false;
        boolean messageAlarmYn = false;
        boolean time00Yn = false;
        boolean time08Yn = false;
        boolean time10Yn = false;

        totalAlarmYn = sharedPreferences.getBoolean("totalAlarm", false);
        newPostAlarmYn = sharedPreferences.getBoolean("newPost", false);
        replyAlarmYn = sharedPreferences.getBoolean("replyPost", false);
        messageAlarmYn = sharedPreferences.getBoolean("message", false);
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
    }
}
