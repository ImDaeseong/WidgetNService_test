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

            val param = intent.extras!!.getString("alarm")
            //Log.e(tag, "param:$param")

            val service = Intent(context, AlarmService::class.java)
            service.putExtra("alarm", param)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service)
            } else {
                context.startService(service)
            }
        } catch (ex: Exception) {
            Log.e(tag, ex.message.toString())
        }
    }

}