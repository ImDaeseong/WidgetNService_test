package com.daeseong.alarmservice


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.*
import android.widget.Button
import android.widget.TimePicker
import android.widget.TimePicker.OnTimeChangedListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val tag = MainActivity::class.java.simpleName

    private var time_picker: TimePicker? = null
    private var button1: Button? = null
    private var button2:Button? = null
    private var calendar: Calendar? = null
    private var alarmManager: AlarmManager? = null
    private var pendingIntent: PendingIntent? = null
    private var alarmIntent: Intent? = null
    private var nHour = 0
    private var nMinute:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //위젯에서 클릭시 이벤트 값
        val intentparam = intent
        val nType = intentparam.getIntExtra("type", -1)
        Log.e(tag, nType.toString())

        InitTitleBar()

        setFlags()

        setContentView(R.layout.activity_main)

        alarmIntent = Intent(this, AlarmReceiver::class.java)

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        calendar = Calendar.getInstance()

        time_picker = findViewById(R.id.time_picker)
        time_picker!!.setOnTimeChangedListener(OnTimeChangedListener { view, hourOfDay, minute ->
            setTimePicker()
        })

        button1 = findViewById(R.id.button1)
        button1!!.setOnClickListener {

            try {

                setTimePicker()

                calendar!!.set(Calendar.HOUR_OF_DAY, nHour)
                calendar!!.set(Calendar.MINUTE, nMinute)

                Toast.makeText(this,"Alarm 예정 " + nHour + "시 " + nMinute + "분", Toast.LENGTH_SHORT).show()

                alarmIntent!!.putExtra("alarm", "on")

                pendingIntent = null
                pendingIntent = PendingIntent.getBroadcast(this,0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                alarmManager!![AlarmManager.RTC_WAKEUP, calendar!!.timeInMillis] = pendingIntent
            } catch (ex: java.lang.Exception) {
                Log.i(tag, ex.message.toString())
            }
        }

        button2 = findViewById(R.id.button2)
        button2!!.setOnClickListener {

            try {

                if (alarmManager != null && pendingIntent != null) {

                    Toast.makeText(this, "Alarm 종료", Toast.LENGTH_SHORT).show()

                    alarmManager!!.cancel(pendingIntent)
                    pendingIntent!!.cancel()
                    intent.putExtra("alarm", "off")
                    sendBroadcast(intent)
                }
            } catch (ex: java.lang.Exception) {
                Log.i(tag, ex.message.toString())
            }
        }
    }

    private fun InitTitleBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar_bg)
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun setFlags() {
        window.addFlags(FLAG_TURN_SCREEN_ON)
        window.addFlags(FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(FLAG_KEEP_SCREEN_ON)
    }

    private fun setTimePicker() {

        try {

            if (Build.VERSION.SDK_INT >= 23) {
                nHour = time_picker!!.hour
                nMinute = time_picker!!.minute
            } else {
                nHour = time_picker!!.currentHour
                nMinute = time_picker!!.currentMinute
            }
        } catch (ex: Exception) {
            Log.i(tag, ex.message.toString())
        }
    }
}
