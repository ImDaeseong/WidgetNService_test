package com.daeseong.gameservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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

        //서비스 재시작
        sendBroadcast(new Intent("com.daeseong.gameservice.ReStartServcie"));

        Log.i(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        serviceIntent = intent;

        startTimer();

        Log.i(TAG, "onStartCommand");

        return START_NOT_STICKY;
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

    @SuppressLint("NewApi")
    public void startTimer(){

        try {

            timerTask = new TimerTask() {
                @Override
                public void run() {

                    count++;
                    Log.i(TAG, "count:" + count);

                    String sLog = "";
                    sLog = String.format("count:%d", count);
                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    msg.obj = sLog;
                    handler.sendMessage(msg);
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

}
