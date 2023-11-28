package com.daeseong.alarmservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    private val tag = AlarmReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {

        try {
            val value = intent.extras?.getString("alarm")
            val serviceIntent = Intent(context, AlarmService::class.java)
            serviceIntent.putExtra("alarm", value)
            startServiceCompat(context, serviceIntent)
        } catch (ex: Exception) {
            Log.e(tag, ex.message.toString())
        }
    }

    private fun startServiceCompat(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}
