package com.daeseong.gameservice;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.util.Timer;
import java.util.TimerTask;

public class GameService extends Service {

    private static final String TAG = GameService.class.getSimpleName();

    public static Intent serviceIntent = null;

    private TimerTask timerTask = null;
    private Timer timer = null;

    private long count = 0;

    public GameService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        serviceIntent = null;

        closeTimer();

        handler.removeMessages(0);


        //종료시 재시작 알람
        /*
        try {
            AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent i = new Intent(this, ReStartReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
            alarm.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5000, pi);
        }catch (Exception ex){
            Log.d(TAG, ex.getMessage().toString());
        }
        */

        Log.i(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        serviceIntent = intent;

        startTimer();

        Log.i(TAG, "onStartCommand");

        return START_STICKY; //재생성과 onStartCommand() 호출
        //return START_NOT_STICKY;//서비스 재 실행하지 않음
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Log.d(TAG, "onTaskRemoved");

        /*
        Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent = PendingIntent.getService(getApplicationContext(), 1,restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(AlarmManager.ELAPSED_REALTIME,SystemClock.elapsedRealtime() + 1000, restartPendingIntent);
        */

        super.onTaskRemoved(rootIntent);
    }

    @SuppressLint("NewApi")
    public void startTimer(){

        try {

            timerTask = new TimerTask() {
                @Override
                public void run() {

                    count++;
                    Log.i(TAG, "count:" + count);
                    showMessage(count);
                }
            };
            timer = new Timer();
            timer.schedule(timerTask, 0, 10000);

        }catch (Exception ex){
            Log.i(TAG, "startTimer:" + ex.getMessage().toString());
        }
    }

    private void closeTimer(){

        try {

            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }

            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }

        }catch (Exception ex){
            Log.i(TAG, "stopTimer:" + ex.getMessage().toString());
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            try {
                Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }catch (Exception ex){
                Log.e(TAG, "Toast" + ex.getMessage().toString());
            }
            return true;
        }
    });

    private void showMessage(long count){

        try {
            String sMsg = String.format("count:%d", count);
            Message msg = handler.obtainMessage();
            msg.what = 0;
            msg.obj = sMsg;
            handler.sendMessage(msg);
        }catch (Exception ex){
            Log.e(TAG, "Toast" + ex.getMessage().toString());
        }
    }

}
