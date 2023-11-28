package com.daeseong.alarmservice

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.*
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager.LayoutParams.*
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*

class MainActivity : AppCompatActivity() {

    private val tag = MainActivity::class.java.simpleName

    private lateinit var timePicker: TimePicker
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var calendar: Calendar
    private var nHour = 0
    private var nMinute = 0

    private var alarmManager: AlarmManager? = null
    private var pendingIntent: PendingIntent? = null

    private lateinit var permissResultLauncher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPermissionsLauncher()

        initTitleBar()
        setFlags()
        setContentView(R.layout.activity_main)

        intent = Intent(this, AlarmReceiver::class.java)
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        calendar = Calendar.getInstance()

        timePicker = findViewById(R.id.time_picker)
        timePicker.setOnTimeChangedListener { _, _, _ ->
            setTimePicker()
        }

        button1 = findViewById(R.id.button1)
        button1.setOnClickListener {
            try {
                setTimePicker()
                calendar.set(Calendar.HOUR_OF_DAY, nHour)
                calendar.set(Calendar.MINUTE, nMinute)
                Toast.makeText(this, "Alarm 예정 $nHour 시 $nMinute 분", Toast.LENGTH_SHORT).show()
                intent.putExtra("alarm", "on")
                pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                alarmManager?.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } catch (ex: Exception) {
                Log.e(tag, ex.message.toString())
            }
        }

        button2 = findViewById(R.id.button2)
        button2.setOnClickListener {
            try {
                if (alarmManager != null && pendingIntent != null) {
                    Toast.makeText(this, "Alarm 종료", Toast.LENGTH_SHORT).show()
                    alarmManager?.cancel(pendingIntent)
                    pendingIntent?.cancel()
                    intent.putExtra("alarm", "off")
                    sendBroadcast(intent)
                }
            } catch (ex: Exception) {
                Log.e(tag, ex.message.toString())
            }
        }

        // BroadcastReceiver 등록
        LocalBroadcastManager.getInstance(this).registerReceiver(widgetClickReceiver, IntentFilter("com.daeseong.alarmservice.ACTION_WIDGET_CLICK"))

        // 권한 체크
        checkPermissions()
    }

    private val widgetClickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.action != null && intent.action == "com.daeseong.alarmservice.ACTION_WIDGET_CLICK") {
                val nType = intent.getIntExtra("type", -1)
                Log.e(tag, "위젯에서 클릭 이벤트가 들어 왔을 경우 nType:$nType")
            }
        }
    }

    private fun initTitleBar() {

        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.rgb(255, 255, 255)

        try {
            //안드로이드 8.0 오레오 버전에서만 오류 발생
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        } catch (ex: Exception) {
            Log.e(tag, ex.message.toString())
        }
    }

    // 화면 플래그 설정
    private fun setFlags() {

        //화면 계속 유지하면서 동시에 잠금 기능도 허용
        window.addFlags(FLAG_KEEP_SCREEN_ON or FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
    }

    private fun setTimePicker() {
        try {
            nHour = if (Build.VERSION.SDK_INT >= 23) timePicker.hour else timePicker.currentHour
            nMinute = if (Build.VERSION.SDK_INT >= 23) timePicker.minute else timePicker.currentMinute
        } catch (ex: Exception) {
            Log.e(tag, ex.message.toString())
        }
    }

    // 위젯 바로가기 설정
    private fun setWidgetShortcut(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                if (appWidgetManager.isRequestPinAppWidgetSupported) {
                    val widgetProvider = ComponentName(context, AlarmAppWidget::class.java)
                    appWidgetManager.requestPinAppWidget(widgetProvider, null, null)
                }
            }
        } catch (ex: Exception) {
            Log.e(tag, "setWidgetShortcut:${ex.message.toString()}")
        }
    }

    private fun checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    permissResultLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                } else {
                    Log.e(tag, "READ_MEDIA_AUDIO 권한 소유")
                }
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    Log.e(tag, "READ_EXTERNAL_STORAGE 권한 소유")
                }
            }
        }
    }

    private fun initPermissionsLauncher() {

        permissResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                Log.e(tag, "권한 소유")
                setWidgetShortcut(this)
            } else {
                Log.e(tag, "권한 미소유")
            }
        }
    }
}
