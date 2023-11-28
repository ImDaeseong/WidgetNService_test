package com.daeseong.alarmservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        try {

            String val = intent.getExtras().getString("alarm");
            Intent serviceIntent  = new Intent(context, AlarmService.class);
            serviceIntent .putExtra("alarm", val);
            startServiceCompat(context, serviceIntent);

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage().toString());
        }
    }

    private void startServiceCompat(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
