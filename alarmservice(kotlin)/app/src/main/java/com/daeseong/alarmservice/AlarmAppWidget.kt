package com.daeseong.alarmservice

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class AlarmAppWidget : AppWidgetProvider() {

    private val tag = AlarmAppWidget::class.java.simpleName

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        Log.e(tag, "onUpdate")

        for (appWidgetId in appWidgetIds) {
            val remoteViews = RemoteViews(context.packageName, R.layout.alarm_app_widget)
            setOnClickPendingIntent(context, remoteViews, R.id.button1, appWidgetId, "start")
            setOnClickPendingIntent(context, remoteViews, R.id.button2, appWidgetId, "stop")
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    private fun setOnClickPendingIntent(context: Context, remoteViews: RemoteViews, resID: Int, appWidgetId: Int, action: String) {

        Log.e(tag, "setOnClickPendingIntent")

        val intent = Intent(context, AlarmAppWidget::class.java)
        intent.action = action
        val pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE)
        remoteViews.setOnClickPendingIntent(resID, pendingIntent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action != null && (intent.action == "start" || intent.action == "stop")) {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            handleWidgetAction(context, intent.action!!, appWidgetId)
        }
    }

    // Widget 버튼 클릭 이벤트
    private fun handleWidgetAction(context: Context, action: String, appWidgetId: Int) {

        val nType = when (action) {
            "start" -> 1
            "stop" -> 0
            else -> -1
        }

        val intent = Intent("com.daeseong.alarmservice.ACTION_WIDGET_CLICK")
        intent.putExtra("type", nType)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}
