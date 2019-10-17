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
        List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 10000, time);
        if (usageStats != null){

            SortedMap<Long, UsageStats> runningTask = new TreeMap<Long,UsageStats>();
            for (UsageStats item : usageStats) {
                runningTask.put(item.getLastTimeUsed(), item);
            }

            if (runningTask != null && !runningTask.isEmpty()){

                //항목에 있는 패키지명 등록
                if(iteminfo.getInstance().isGameItem(runningTask.get(runningTask.lastKey()).getPackageName())) {

                    Log.e(TAG, runningTask.get(runningTask.lastKey()).getPackageName());

                    String packagename = runningTask.get(runningTask.lastKey()).getPackageName();
                    String lasttime = getTimeDate(runningTask.get(runningTask.lastKey()).getLastTimeUsed());

                    if(!gameinfoMap.containsKey(packagename)){
                        gameinfoMap.put(packagename, new gameinfo(packagename, lasttime, getTimeDate()) );
                    }

                }
            }

            //전체 실행 항목에서 등록된 패키지가 현재 실행 중인지 체크
            for (String sKey : gameinfoMap.keySet()) {

                boolean bpackagename = false;
                String packagename =  gameinfoMap.get(sKey).getPackagename();
                String starttime =  gameinfoMap.get(sKey).getStarttm();
                String endtime = gameinfoMap.get(sKey).getEndtm();

                for (UsageStats item : usageStats){

                    Log.e(TAG, item.getPackageName());

                    if( item.getPackageName().equals(packagename) ) {
                        bpackagename = true;
                        break;
                    }
                }


                if(bpackagename){

                    //존재시 시간 업데이트
                    gameinfoMap.put(sKey, new gameinfo(packagename, starttime, getTimeDate()) );

                    String sLog = "";
                    sLog = String.format("업데이트된 앱:%s  시작시간:%s  끝시간:%s", packagename,  starttime, endtime);
                    Log.e(TAG, sLog);

                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    msg.obj = sLog;
                    handler.sendMessage(msg);

                } else {

                    String sLog = "";
                    sLog = String.format("제거된 앱:%s  시작시간:%s  끝시간:%s", packagename,  starttime, endtime);
                    Log.e(TAG, sLog);

                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    msg.obj = sLog;
                    handler.sendMessage(msg);

                    //미존재시 제거
                    gameinfoMap.remove(sKey);
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

}
