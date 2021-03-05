package com.daeseong.widgetalarm

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val tag = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //widget info
        //getWidgetInfo()

        //Shortcut
        setWidgetShortcut(this)
    }

    private fun setWidgetShortcut(context: Context) {

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appWidgetManager: AppWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                if (appWidgetManager.isRequestPinAppWidgetSupported) {
                    val widgetProvider = ComponentName(context, AlarmWidget::class.java)
                    appWidgetManager.requestPinAppWidget(widgetProvider, null, null)
                }
            }
        } catch (ex: Exception) {
            Log.e(tag, "setWidgetShortcut:" + ex.message.toString())
        }
    }

    private fun getWidgetInfo() {

        try {
            val appWidgetManager = AppWidgetManager.getInstance(this)
            val appWidgetProviderInfos = appWidgetManager.installedProviders
            for (info in appWidgetProviderInfos) {

                Log.e(tag, "설치된 앱위젯 정보:$info")
                Log.e(tag, "설치된 앱위젯 정보:" + info.provider.packageName)
            }
        } catch (ex: Exception) {
            Log.e(tag, ex.message.toString())
        }
    }
}
