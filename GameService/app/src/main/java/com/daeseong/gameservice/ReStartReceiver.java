package com.daeseong.gameservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class ReStartReceiver extends BroadcastReceiver {

    private static final String TAG = ReStartReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG, "onReceive");

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context.getApplicationContext(), GameService.class));
            } else {
                context.startService(new Intent(context.getApplicationContext(), GameService.class));
            }

        }catch (Exception ex){
            Log.e(TAG, ex.getMessage().toString());
        }

    }

}
