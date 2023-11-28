package com.daeseong.alarmservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class AlarmService : Service() {

    private val tag = AlarmService::class.java.simpleName

    private val NOTIFICATION_ID = 1
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()

        Log.e(tag, "onCreate")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.e(tag, "onStartCommand")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            val notification: Notification = createNotification()
            startForeground(NOTIFICATION_ID, notification)
        } else {
            stopForeground(STOP_FOREGROUND_DETACH)
            stopSelf()
        }

        val value: String? = intent?.getStringExtra("alarm")
        Log.e(tag, "alarm:$value")

        if ("on" == value) {
            startPlay()
        } else {
            stopPlay()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.e(tag, "onDestroy")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "AlarmServicechannelID"
            val notificationChannel = NotificationChannel(channelId, "알람", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "AlarmServicechannelID")
            .setContentTitle("알림")
            .setContentText("기상 시간입니다.")
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    private fun startPlay() {
        mediaPlayer = MediaPlayer.create(this, R.raw.a)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    private fun stopPlay() {
        mediaPlayer?.let {
            it.stop()
            it.release()
        }
        mediaPlayer = null
    }
}
