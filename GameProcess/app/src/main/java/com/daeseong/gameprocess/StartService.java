package com.daeseong.gameprocess;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class StartService extends Service {

    private static final String TAG = StartService.class.getSimpleName();

    private TimerTask timerTask = null;
    private Timer timer = null;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");

        startTimer();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");

        closeTimer();

        registerAlarm();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("NewApi")
    public void startTimer(){

        try {

            timerTask = new TimerTask() {
                @Override
                public void run() {

                    //패키지 목록
                    //getListRunPackageName();

                    //현재 액티브 패키지
                    Log.e(TAG, "PackageName:" + checkRunPackageName());

                }
            };
            timer = new Timer();
            timer.schedule(timerTask, 0, 10000);

        }catch (Exception ex){
            Log.e(TAG, "stopTimer:" + ex.getMessage().toString());
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
            Log.e(TAG, "stopTimer:" + ex.getMessage().toString());
        }

    }

    public void registerAlarm() {

        Log.d(TAG, "registerAlarm");

        Intent intent = new Intent(StartService.this, RestartService.class);
        intent.setAction("ACTION.RestartService");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(StartService.this, 0, intent, 0);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        elapsedRealtime += 1*1000;
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //알람 등록
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, elapsedRealtime, 10*1000, pendingIntent);

    }

    public void unregisterAlarm() {

        Log.d(TAG, "unregisterAlarm");

        Intent intent = new Intent(StartService.this, RestartService.class);
        intent.setAction("ACTION.RestartService");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(StartService.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        //알람 취소
        alarmManager.cancel(pendingIntent);

    }

    private String checkRunPackageName(){

        String sPackageName = "";

        UsageStatsManager usageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*1000, time);
        if (usageStats != null){

            SortedMap<Long, UsageStats> runningTask = new TreeMap<Long,UsageStats>();
            for (UsageStats item : usageStats) {
                runningTask.put(item.getLastTimeUsed(), item);
            }

            if (runningTask != null && !runningTask.isEmpty()) {
                sPackageName = runningTask.get(runningTask.lastKey()).getPackageName();
            }
        }

        return sPackageName;
    }

    private void getListRunPackageName() {

        UsageStatsManager usageStatsManager = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);
        final long endTime = System.currentTimeMillis();
        final long beginTime = endTime - 10000;
        final UsageEvents usageEvents = usageStatsManager.queryEvents(beginTime, endTime);
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            Log.e(TAG, "PackageName: " + event.getPackageName());
        }

    }

}