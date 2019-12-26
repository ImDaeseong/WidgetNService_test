package com.daeseong.alarmservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    private TimePicker time_picker;
    private Button button1, button2;
    private Calendar calendar;
    private AlarmManager alarmManager = null;
    private PendingIntent pendingIntent = null;
    private Intent intent;
    private int nHour, nMinute;

    private void InitTitleBar(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusbar_bg));
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    private void setFlags(){

        getWindow().addFlags(FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);

    }

    private void setTimePicker(){

        try {

            if (Build.VERSION.SDK_INT >= 23) {
                nHour = time_picker.getHour();
                nMinute = time_picker.getMinute();
            } else {
                nHour = time_picker.getCurrentHour();
                nMinute = time_picker.getCurrentMinute();
            }

        }catch (Exception ex){
            Log.i(TAG, ex.getMessage().toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //위젯에서 클릭시 이벤트 값
        Intent intentparam = getIntent();
        int nType = intentparam.getIntExtra("type", -1);
        Log.e(TAG , String.valueOf(nType));


        InitTitleBar();

        setFlags();

        setContentView(R.layout.activity_main);

        intent = new Intent(this, AlarmReceiver.class);

        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        calendar = Calendar.getInstance();

        time_picker = findViewById(R.id.time_picker);
        time_picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                setTimePicker();
            }
        });


        button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    setTimePicker();

                    calendar.set(Calendar.HOUR_OF_DAY, nHour);
                    calendar.set(Calendar.MINUTE, nMinute);
                    Toast.makeText(MainActivity.this, "Alarm 예정 " + nHour + "시 " + nMinute + "분", Toast.LENGTH_SHORT).show();

                    intent.putExtra("alarm", "on");
                    pendingIntent = null;
                    pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                }catch (Exception ex){
                    Log.i(TAG, ex.getMessage().toString());
                }

            }
        });


        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if (alarmManager != null && pendingIntent != null) {

                        Toast.makeText(MainActivity.this, "Alarm 종료", Toast.LENGTH_SHORT).show();

                        alarmManager.cancel(pendingIntent);
                        pendingIntent.cancel();

                        intent.putExtra("alarm", "off");
                        sendBroadcast(intent);

                    }

                }catch (Exception ex){
                    Log.i(TAG, ex.getMessage().toString());
                }

            }
        });
    }

}


