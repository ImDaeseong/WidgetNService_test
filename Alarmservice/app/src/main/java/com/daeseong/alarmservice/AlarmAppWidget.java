package com.daeseong.alarmservice;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class AlarmAppWidget extends AppWidgetProvider {

    private static final String TAG = AlarmAppWidget.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.e(TAG, "onUpdate");

        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.alarm_app_widget);
            setOnClickPendingIntent(context, remoteViews, R.id.button1, appWidgetId, "start");
            setOnClickPendingIntent(context, remoteViews, R.id.button2, appWidgetId, "stop");
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    private void setOnClickPendingIntent(Context context, RemoteViews remoteViews, int resID, int appWidgetId, String action) {

        Log.e(TAG, "setOnClickPendingIntent");

        Intent intent = new Intent(context, AlarmAppWidget.class);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(resID, pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction() != null && (intent.getAction().equals("start") || intent.getAction().equals("stop"))) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            handleWidgetAction(context, intent.getAction(), appWidgetId);
        }
    }

    // Widget 버튼 클릭 이벤트
    private void handleWidgetAction(Context context, String action, int appWidgetId) {

        int nType = -1;
        if (action.equals("start")) {
            nType = 1;
        } else if (action.equals("stop")) {
            nType = 0;
        }

        Intent intent = new Intent("com.daeseong.alarmservice.ACTION_WIDGET_CLICK");
        intent.putExtra("type", nType);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}

