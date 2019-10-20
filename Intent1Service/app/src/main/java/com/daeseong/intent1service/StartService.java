package com.daeseong.intent1service;

import android.app.IntentService;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class StartService extends IntentService {

    private static final String TAG = StartService.class.getSimpleName();

    private HashMap<String, gameinfo> gameinfoMap = new HashMap<>();

    public StartService() {

        super("StartService");

        initData();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.e(TAG, "onHandleIntent start");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getListRunPackageName();
            }
        }, 10000, 10000);

        //서비스 종료
        stopSelf();

        Log.d(TAG, "onHandleIntent end");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        handler.removeMessages(0);

        Log.e(TAG, "onDestroy");

    }

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

        try {

            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, time);
            if (usageStats != null) {

                SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
                for (UsageStats item : usageStats) {
                    runningTask.put(item.getLastTimeUsed(), item);
                }

                if (runningTask != null && !runningTask.isEmpty()){

                    //항목에 있는 패키지명 등록
                    if(iteminfo.getInstance().isGameItem(runningTask.get(runningTask.lastKey()).getPackageName())) {

                        String packagename = runningTask.get(runningTask.lastKey()).getPackageName();

                        String sLog = "";

                        if(!gameinfoMap.containsKey(packagename)){

                            gameinfoMap.put(packagename, new gameinfo(packagename, getTimeDate(), getTimeDate()) );

                            sLog = String.format("시작된 앱이름:%s  시작시간:%s  끝시간:%s  ", packagename,  gameinfoMap.get(packagename).getStarttm(), gameinfoMap.get(packagename).getEndtm());
                            Log.e(TAG, sLog);

                            Message msg = handler.obtainMessage();
                            msg.what = 0;
                            msg.obj = sLog;
                            handler.sendMessage(msg);

                        } else {

                            String starttime =  gameinfoMap.get(packagename).getStarttm();
                            gameinfoMap.put(packagename, new gameinfo(packagename, starttime, getTimeDate()) );

                            sLog = String.format("업데이트된 앱이름:%s  시작시간:%s  끝시간:%s  ", packagename,  gameinfoMap.get(packagename).getStarttm(), gameinfoMap.get(packagename).getEndtm());
                            Log.e(TAG, sLog);

                            Message msg = handler.obtainMessage();
                            msg.what = 0;
                            msg.obj = sLog;
                            handler.sendMessage(msg);

                        }
                    } else {

                        String sLog = "";

                        Iterator<String> keys = gameinfoMap.keySet().iterator();
                        while (keys.hasNext()){

                            String key = keys.next();

                            sLog = String.format("종료된 앱이름:%s  시작시간:%s  끝시간:%s  ", gameinfoMap.get(key).getPackagename(),  gameinfoMap.get(key).getStarttm(), gameinfoMap.get(key).getEndtm());
                            Log.e(TAG, sLog);

                            gameinfoMap.remove(key);

                            Message msg = handler.obtainMessage();
                            msg.what = 0;
                            msg.obj = sLog;
                            handler.sendMessage(msg);
                        }

                    }
                }
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage().toString());
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
