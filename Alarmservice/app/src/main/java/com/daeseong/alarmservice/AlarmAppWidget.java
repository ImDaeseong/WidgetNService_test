package com.daeseong.alarmservice;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class AlarmAppWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.alarm_app_widget);
        setOnClickPendingIntent(context, remoteViews, R.id.button1, 1 );
        setOnClickPendingIntent(context, remoteViews, R.id.button2, 2 );
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    private void setOnClickPendingIntent(Context context, RemoteViews remoteViews, int resID, int nType){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("type", nType);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, nType, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(resID, pendingIntent);
    }
}

