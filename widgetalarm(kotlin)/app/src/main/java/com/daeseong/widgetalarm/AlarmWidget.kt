package com.daeseong.widgetalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.*


class AlarmWidget : AppWidgetProvider() {

    private val tag = AlarmWidget::class.java.simpleName

    private var pendingIntent: PendingIntent? = null

    private val lMin = 1000 * 60 * 1 // Millisec * Second * Minute

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        Log.i(tag, "onUpdate")

        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }

        val intent = Intent(context, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), lMin.toLong(), pendingIntent)
    }

    override fun onEnabled(context: Context) {
        Log.i(tag, "onEnabled")
    }

    override fun onDisabled(context: Context) {

        Log.i(tag, "onDisabled")

        try {

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        } catch (ex: Exception) {
            Log.i(tag, ex.message.toString())
        }
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {

    val views = RemoteViews(context.packageName, R.layout.alarm_widget)
    views.setTextViewText(R.id.tv1, getTimeDate())
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun getTimeDate(): String? {
    val dateFormat = SimpleDateFormat("HH:mm:ss")
    return dateFormat.format(Date())
}