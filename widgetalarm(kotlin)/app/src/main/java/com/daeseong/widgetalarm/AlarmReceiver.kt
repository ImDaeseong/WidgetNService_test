package com.daeseong.widgetalarm


import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.*


class AlarmReceiver : BroadcastReceiver() {

    private val tag = AlarmReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {

        Log.i(tag, getTimeDate())

        updateViews(context)
    }

    private fun updateViews(context: Context) {

        try {

            val remoteViews = RemoteViews(context.packageName, R.layout.alarm_widget)
            remoteViews.setTextViewText(R.id.tv1, getTimeDate())

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, AlarmWidget::class.java)
            appWidgetManager.updateAppWidget(componentName, remoteViews)

        } catch (ex: Exception) {
            Log.i(tag, ex.message.toString())
        }
    }

    private fun getTimeDate(): String? {
        val dateFormat = SimpleDateFormat("HH:mm:ss")
        return dateFormat.format(Date())
    }
}