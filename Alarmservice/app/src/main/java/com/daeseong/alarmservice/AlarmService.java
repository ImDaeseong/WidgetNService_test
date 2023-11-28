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

    private static final int NOTIFICATION_ID = 1;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG, "onStartCommand");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
            Notification notification = createNotification();
            startForeground(NOTIFICATION_ID, notification);
        } else {
            stopForeground(true);
            stopSelf();
        }

        String val = intent.getStringExtra("alarm");
        Log.e(TAG, "alarm:" + val);

        if ("on".equals(val)) {
            startPlay();
        } else {
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

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "AlarmServicechannelID";
            NotificationChannel notificationChannel = new NotificationChannel(channelId, "알람", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, "AlarmServicechannelID")
                .setContentTitle("알림")
                .setContentText("기상 시간입니다.")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
    }

    private void startPlay() {
        mediaPlayer = MediaPlayer.create(this, R.raw.a);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
