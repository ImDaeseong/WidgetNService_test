package com.daeseong.alarmservice;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON; // 화면 계속 켜짐 / 화면 꺼짐 방지
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;// 잠금시 잠금화면보다 맨 앞에 보이게
import static android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;//  화면 on

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TimePicker timePicker;
    private Button button1, button2;
    private Calendar calendar;
    private AlarmManager alarmManager = null;
    private PendingIntent pendingIntent = null;
    private Intent intent;
    private int nHour, nMinute;

    public ActivityResultLauncher<String> permissResultLauncher;

    private void initTitleBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusbar_bg));
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void setFlags() {
        getWindow().addFlags(FLAG_TURN_SCREEN_ON | FLAG_SHOW_WHEN_LOCKED | FLAG_KEEP_SCREEN_ON);
    }

    private void setTimePicker() {
        try {
            nHour = Build.VERSION.SDK_INT >= 23 ? timePicker.getHour() : timePicker.getCurrentHour();
            nMinute = Build.VERSION.SDK_INT >= 23 ? timePicker.getMinute() : timePicker.getCurrentMinute();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPermissionsLauncher();

        initTitleBar();
        setFlags();
        setContentView(R.layout.activity_main);


        intent = new Intent(this, AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        calendar = Calendar.getInstance();

        timePicker = findViewById(R.id.time_picker);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                setTimePicker();
            }
        });

        button1 = findViewById(R.id.button1);
        button1.setOnClickListener(v -> {
            try {
                setTimePicker();
                calendar.set(Calendar.HOUR_OF_DAY, nHour);
                calendar.set(Calendar.MINUTE, nMinute);
                Toast.makeText(MainActivity.this, "Alarm 예정 " + nHour + "시 " + nMinute + "분", Toast.LENGTH_SHORT).show();
                intent.putExtra("alarm", "on");
                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        });

        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(v -> {
            try {
                if (alarmManager != null && pendingIntent != null) {
                    Toast.makeText(MainActivity.this, "Alarm 종료", Toast.LENGTH_SHORT).show();
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();
                    intent.putExtra("alarm", "off");
                    sendBroadcast(intent);
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        });

        //BroadcastReceiver 등록
        LocalBroadcastManager.getInstance(this).registerReceiver(widgetClickReceiver, new IntentFilter("com.daeseong.alarmservice.ACTION_WIDGET_CLICK"));

        //권한 체크
        checkPermissions();
    }

    private final BroadcastReceiver widgetClickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() != null && intent.getAction().equals("com.daeseong.alarmservice.ACTION_WIDGET_CLICK")) {
                int nType = intent.getIntExtra("type", -1);
                Log.e(TAG, "위젯에서 클릭 이벤트가 들어 왔을 경우 nType:" + String.valueOf(nType));
            }
        }
    };

    private void setWidgetShortcut(Context context) {

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                AppWidgetManager appWidgetManager = context.getSystemService(AppWidgetManager.class);
                if (appWidgetManager.isRequestPinAppWidgetSupported()) {
                    ComponentName widgetProvider = new ComponentName(context, AlarmAppWidget.class);
                    appWidgetManager.requestPinAppWidget(widgetProvider, null, null);
                }
            }

        } catch (Exception ex) {
            Log.e(TAG, "setWidgetShortcut:" + ex.getMessage().toString());
        }
    }

    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    permissResultLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO);
                } else {
                    Log.e(TAG, "READ_MEDIA_AUDIO 권한 소유");
                }

            } else {

                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    Log.e(TAG, "READ_EXTERNAL_STORAGE 권한 소유");
                }
            }
        }
    }

    private void initPermissionsLauncher() {

        permissResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                Log.e(TAG, "권한 소유");
                setWidgetShortcut(this);
            } else {
                Log.e(TAG, "권한 미소유");
            }
        });
    }
}


