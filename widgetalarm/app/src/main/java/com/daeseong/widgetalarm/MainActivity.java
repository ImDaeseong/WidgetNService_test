package com.daeseong.widgetalarm;

import androidx.appcompat.app.AppCompatActivity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    ComponentName widgetProvider = new ComponentName(context, AlarmWidget.class);
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
