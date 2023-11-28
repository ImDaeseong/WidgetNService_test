package com.daeseong.timewidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class TimeWidget extends AppWidgetProvider {

    private static final String TAG = TimeWidget.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.e(TAG, "onUpdate");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        //Log.e(TAG, "onReceive:" + intent.getAction());

        if (intent.getAction() != null && intent.getAction().equals("TimeWidget.TextView.CLICK")) {

            try {
                Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainIntent);
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage().toString());
            }
        }
    }


    @Override
    public void onEnabled(Context context) {

        Log.e(TAG, "onEnabled");

        Intent serviceIntent = new Intent(context, TimeService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }

        // 서비스 시작
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, TimeService.class));
        } else {
            context.startService(new Intent(context, TimeService.class));
        }
    }

    @Override
    public void onDisabled(Context context) {

        Log.e(TAG, "onDisabled");

        context.stopService(new Intent(context, TimeService.class));
    }
}

