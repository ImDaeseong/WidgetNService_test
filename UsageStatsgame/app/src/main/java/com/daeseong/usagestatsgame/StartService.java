package com.daeseong.usagestatsgame;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class StartService extends Service {

    private static final String TAG = StartService.class.getSimpleName();

    private TimerTask timerTask = null;
    private Timer timer = null;

    private HashMap<String, gameinfo> gameinfoMap = new HashMap<>();

    private String lastpackagename;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");

        initData();

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

        handler.removeMessages(0);

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

                    //현재 액티브 패키지
                    getListRunPackageName();

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

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
            return true;
        }
    });

    private void initData(){

        Log.e(TAG, "initData");

        iteminfo.getInstance().setGameItem("net.gameply.android.moonlight");
        iteminfo.getInstance().setGameItem("com.kakaogames.moonlight");
        iteminfo.getInstance().setGameItem("com.ncsoft.lineagem19");
        iteminfo.getInstance().setGameItem("com.lilithgames.rok.gpkr");
        iteminfo.getInstance().setGameItem("com.netmarble.lineageII");
        iteminfo.getInstance().setGameItem("com.bluepotiongames.eosm");
        iteminfo.getInstance().setGameItem("com.qjzj4399kr.google");
        iteminfo.getInstance().setGameItem("com.netmarble.bnsmkr");
        iteminfo.getInstance().setGameItem("com.zlongame.kr.langrisser");
        iteminfo.getInstance().setGameItem("com.pearlabyss.blackdesertm");
    }

    private void getListRunPackageName() {

        UsageStatsManager usageStatsManager = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, time);
        if (usageStats != null){

            SortedMap<Long, UsageStats> runningTask = new TreeMap<Long,UsageStats>();
            for (UsageStats item : usageStats) {
                runningTask.put(item.getLastTimeUsed(), item);
            }

            if (runningTask != null && !runningTask.isEmpty()){

                //항목에 있는 패키지명 등록
                if(iteminfo.getInstance().isGameItem(runningTask.get(runningTask.lastKey()).getPackageName())) {

                    String packagename = runningTask.get(runningTask.lastKey()).getPackageName();

                    if(!gameinfoMap.containsKey(packagename)){
                        gameinfoMap.put(packagename, new gameinfo(packagename, getTimeDate(), getTimeDate()) );

                        String sLog = "";
                        sLog = String.format("시작된 앱이름:%s  시작시간:%s  끝시간:%s  ", packagename,  gameinfoMap.get(packagename).getStarttm(), gameinfoMap.get(packagename).getEndtm());
                        Log.e(TAG, sLog);

                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        msg.obj = sLog;
                        handler.sendMessage(msg);

                        lastpackagename = packagename;

                    } else {
                        String starttime =  gameinfoMap.get(packagename).getStarttm();
                        gameinfoMap.put(packagename, new gameinfo(packagename, starttime, getTimeDate()) );

                        String sLog = "";
                        sLog = String.format("업데이트된 앱이름:%s  시작시간:%s  끝시간:%s  ", packagename,  gameinfoMap.get(packagename).getStarttm(), gameinfoMap.get(packagename).getEndtm());
                        Log.e(TAG, sLog);

                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        msg.obj = sLog;
                        handler.sendMessage(msg);

                        lastpackagename = packagename;
                    }

                }else {

                    if(gameinfoMap.containsKey(lastpackagename)){
                        String sLog = "";
                        sLog = String.format("종료된 앱:%s  시작시간:%s  끝시간:%s  ", gameinfoMap.get(lastpackagename).getPackagename(),  gameinfoMap.get(lastpackagename).getStarttm(), gameinfoMap.get(lastpackagename).getEndtm());
                        Log.e(TAG, sLog);

                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        msg.obj = sLog;
                        handler.sendMessage(msg);

                        //미존재시 제거
                        gameinfoMap.remove(lastpackagename);
                        lastpackagename = "";
                    }

                }
            } else {

                if(gameinfoMap.containsKey(lastpackagename)){
                    String sLog = "";
                    sLog = String.format("종료된 앱:%s  시작시간:%s  끝시간:%s  ", gameinfoMap.get(lastpackagename).getPackagename(),  gameinfoMap.get(lastpackagename).getStarttm(), gameinfoMap.get(lastpackagename).getEndtm());
                    Log.e(TAG, sLog);

                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    msg.obj = sLog;
                    handler.sendMessage(msg);

                    //미존재시 제거
                    gameinfoMap.remove(lastpackagename);
                    lastpackagename = "";
                }

            }

        }

    }

    private static String getTimeDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date());
    }

    private static String getTimeDate(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date(date));
    }

    private static String getDiffMinute(String starttime, String endtime) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String sMinute;

        try {
            long lminute = ((dateFormat.parse(endtime).getTime() - dateFormat.parse(starttime).getTime()) / 60000 );
            sMinute = String.format("%d", lminute);
        } catch (Exception ex) {
            sMinute = String.format("0");
        }
        return sMinute;
    }

    private static String getDiffSecond(String starttime, String endtime) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String sMinute;

        try {
            long lminute = ((dateFormat.parse(endtime).getTime() - dateFormat.parse(starttime).getTime()) / 1000 );
            sMinute = String.format("%d", lminute);
        } catch (Exception ex) {
            sMinute = String.format("0");
        }
        return sMinute;
    }

}
