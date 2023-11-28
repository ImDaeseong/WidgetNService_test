package com.daeseong.timewidget;

import androidx.appcompat.app.AppCompatActivity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static RestartService restartService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //BroadcastReceiver
        restartService = new RestartService();
        IntentFilter intentFilter = new IntentFilter("com.daeseong.timewidget.TimeService");
        registerReceiver(restartService, intentFilter);

        //widget info
        getWidgetInfo();

        //Shortcut
        setWidgetShortcut(this);
    }

    private void setWidgetShortcut(Context context) {

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                AppWidgetManager appWidgetManager = context.getSystemService(AppWidgetManager.class);
                if (appWidgetManager.isRequestPinAppWidgetSupported()) {
                    ComponentName widgetProvider = new ComponentName(context, TimeWidget.class);
                    appWidgetManager.requestPinAppWidget(widgetProvider, null, null);
                }
            }

        } catch (Exception ex) {
            Log.e(TAG, "setWidgetShortcut:" + ex.getMessage().toString());
        }
    }

    private void getWidgetInfo() {

        try {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            List<AppWidgetProviderInfo> appWidgetProviderInfos = appWidgetManager.getInstalledProviders();

            for (AppWidgetProviderInfo info : appWidgetProviderInfos) {

                //Log.e(TAG, "설치된 앱위젯 정보:" + info.toString());
                Log.e(TAG, "설치된 앱위젯 정보:" + info.provider.getPackageName());
            }

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage().toString());
        }
    }
}
