package com.daeseong.alarmservice

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews


class AlarmAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {

    val views = RemoteViews(context.packageName, R.layout.alarm_app_widget)

    views.setTextViewText(R.id.button1, 1.toString())
    views.setTextViewText(R.id.button2, 2.toString())
    setOnClickPendingIntent(context, views, R.id.button1, 1)
    setOnClickPendingIntent(context, views, R.id.button2, 2)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun setOnClickPendingIntent(context: Context, remoteViews: RemoteViews, resID: Int, nType: Int) {

    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra("type", nType)
    val pendingIntent = PendingIntent.getActivity(context, nType, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    remoteViews.setOnClickPendingIntent(resID, pendingIntent)
}