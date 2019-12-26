package com.daeseong.alarmservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        try {

            String val = intent.getExtras().getString("alarm");

            Intent intent_service = new Intent(context, AlarmService.class);
            intent_service.putExtra("alarm", val);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                this.context.startForegroundService(intent_service);

            } else {

                this.context.startService(intent_service);

            }

        }catch (Exception ex){
            Log.e(TAG, ex.getMessage().toString());
        }

    }
}
