package com.daeseong.timewidget;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimeService extends Service {

    private static final String TAG = TimeService.class.getSimpleName();

    private TimerTask timerTask = null;
    private Timer timer = null;

    public TimeService() {

        Log.e(TAG, "TimeService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e(TAG, "onCreate");

        startTimer();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG, "onStartCommand");

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        Log.e(TAG, "onBind");

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e(TAG, "onDestroy");

        stopTimer();

        registerAlarm();
    }

    private void updateWidgetView(){

        try {

            RemoteViews view = new RemoteViews(getPackageName(), R.layout.time_widget);
            view.setTextViewText(R.id.tvTime, getTimeDate());
            view.setTextViewText(R.id.tvDate, getDayDate());

            //클릭이벤트
            Intent intent = new Intent(this, TimeWidget.class);
            intent.setAction("TimeWidget.TextView.CLICK");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            view.setOnClickPendingIntent(R.id.tvTime, pendingIntent);
            view.setOnClickPendingIntent(R.id.tvDate, pendingIntent);

            ComponentName thisWidget = new ComponentName(this, TimeWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(thisWidget, view);

        } catch (Exception ex) {
            Log.e(TAG, "updateWidgetView:" + ex.getMessage().toString());
        }
    }

    private static String getTimeDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date());
    }

    private static String getDayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월dd일");
        return dateFormat.format(new Date());
    }

    public void startTimer(){

        try {

            timerTask = new TimerTask() {
                @Override
                public void run() {

                    //내용 업데이트
                    updateWidgetView();

                }
            };
            timer = new Timer();
            timer.schedule(timerTask, 0, 10000);

        } catch (Exception ex) {
            Log.e(TAG, "stopTimer:" + ex.getMessage().toString());
        }
    }

    public void stopTimer(){

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

        } catch (Exception ex) {
            Log.e(TAG, "stopTimer:" + ex.getMessage().toString());
        }
    }

    public void registerAlarm() {

        Intent intent = new Intent(TimeService.this, RestartService.class);
        intent.setAction("ACTION.RestartService");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TimeService.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        elapsedRealtime += 1 * 1000;
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //알람 등록
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, elapsedRealtime, 10 * 1000, pendingIntent);
    }

    public void unregisterAlarm() {

        Intent intent = new Intent(TimeService.this, RestartService.class);
        intent.setAction("ACTION.RestartService");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TimeService.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //알람 취소
        alarmManager.cancel(pendingIntent);
    }
}
