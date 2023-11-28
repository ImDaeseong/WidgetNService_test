package com.daeseong.widgetalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmWidget extends AppWidgetProvider {

    private static final String TAG = AlarmWidget.class.getSimpleName();

    private PendingIntent pendingIntent = null;

    private long lMin = 1000 * 60 * 1;// Millisec * Second * Minute

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        Log.e(TAG, "updateAppWidget");

        try {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.alarm_widget);
            remoteViews.setTextViewText(R.id.tv1, getTimeDate());
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage().toString());
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.e(TAG, "onUpdate");

        //1분에 한번씩 시간 업데이트

        try {

            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }

            Intent intent = new Intent(context, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), lMin, pendingIntent);

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage().toString());
        }
    }

    @Override
    public void onEnabled(Context context) {

        Log.e(TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {

        Log.e(TAG, "onDisabled");

        try {

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage().toString());
        }
    }

    private static String getTimeDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date());
    }
}

