package com.daeseong.alarmservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class AlarmService extends Service {

    private static final String TAG = AlarmService.class.getSimpleName();

    private MediaPlayer mediaPlayer = null;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG, "onStartCommand");

        if (Build.VERSION.SDK_INT >= 26) {

            String channelID = "AlarmServicechannelID";
            NotificationChannel notificationChannel = new NotificationChannel(channelID,"알람", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);

            Notification notification = new NotificationCompat.Builder(this, channelID)
                    .setContentTitle("알림")
                    .setContentText("기상 시간입니다.")
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            startForeground(1, notification);
            //stopForeground(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        } else {
            stopSelf();
        }

        String sParam = intent.getExtras().getString("alarm");
        if(sParam.equals("on")){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    startPlay();
                }
            }).start();

            startActivity(new Intent(AlarmService.this, MainActivity.class));

        } else  {
            stopPlay();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e(TAG, "onDestroy");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startPlay(){
        mediaPlayer = MediaPlayer.create(this, R.raw.a);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void stopPlay(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

}
