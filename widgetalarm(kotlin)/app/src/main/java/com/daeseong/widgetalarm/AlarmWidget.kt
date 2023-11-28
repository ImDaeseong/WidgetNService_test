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

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        Log.e(tag, "onUpdate")

        //1분에 한번씩 시간 업데이트
        val lMin: Long = 1000 * 60 * 1 // Millisec * Second * Minute

        try {
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }

            val intent = Intent(context, AlarmReceiver::class.java)
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), lMin, pendingIntent)

        } catch (ex: Exception) {
            Log.e(tag, ex.message.toString())
        }
    }

    override fun onEnabled(context: Context) {
        Log.e(tag, "onEnabled")
    }

    override fun onDisabled(context: Context) {

        Log.e(tag, "onDisabled")

        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            pendingIntent?.let {
                alarmManager.cancel(it)
            }
        } catch (ex: Exception) {
            Log.e(tag, ex.message.toString())
        }
    }

    fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {

        Log.e(tag, "updateAppWidget")

        try {
            val remoteViews = RemoteViews(context.packageName, R.layout.alarm_widget)
            remoteViews.setTextViewText(R.id.tv1, getTimeDate())
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        } catch (ex: Exception) {
            Log.e(tag, ex.message.toString())
        }
    }

    fun getTimeDate(): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss")
        return dateFormat.format(Date())
    }
}
