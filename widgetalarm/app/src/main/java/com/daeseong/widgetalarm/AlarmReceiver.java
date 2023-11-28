package com.daeseong.widgetalarm;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG, getTimeDate());

        updateViews(context);
    }

    private void updateViews(Context context) {

        try {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.alarm_widget);
            remoteViews.setTextViewText(R.id.tv1, getTimeDate());

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, AlarmWidget.class);
            appWidgetManager.updateAppWidget(componentName, remoteViews);

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage().toString());
        }
    }

    private static String getTimeDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date());
    }
}
