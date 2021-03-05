package com.daeseong.alarmservice


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat


class AlarmService : Service() {

    private val tag = AlarmService::class.java.simpleName

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()

        Log.e(tag, "onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        Log.e(tag, "onStartCommand")

        if (Build.VERSION.SDK_INT >= 26) {

            val channelID = "AlarmServicechannelID"
            val notificationChannel = NotificationChannel(channelID, "알람", NotificationManager.IMPORTANCE_DEFAULT)
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(notificationChannel)

            val notification = NotificationCompat.Builder(this, channelID)
                .setContentTitle("알림")
                .setContentText("기상 시간입니다.")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
            startForeground(1, notification)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            stopForeground(true)
        } else {

            stopSelf()
        }

        val sParam = intent.extras!!.getString("alarm")
        if (sParam == "on") {

            Thread { startPlay() }.start()

            startActivity(Intent(applicationContext, MainActivity::class.java).setFlags(FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_NEW_TASK))

            /*
            Handler().postDelayed({
                val intent = Intent(this@AlarmService, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                this@AlarmService.startActivity(intent)
            }, 1000)
            */

            Log.e(tag, "on")

        } else {

            stopPlay()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.e(tag, "onDestroy")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun startPlay() {

        mediaPlayer = MediaPlayer.create(this, R.raw.a)
        mediaPlayer!!.isLooping = true
        mediaPlayer!!.start()
    }

    private fun stopPlay() {

        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
        }
    }
}