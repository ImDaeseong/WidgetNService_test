package com.daeseong.gameservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class RestartService extends BroadcastReceiver {

    private static final String TAG = RestartService.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("ACTION.RestartService")){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, StartService.class));
            } else {
                context.startService(new Intent(context, StartService.class));
            }

        } else  if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, StartService.class));
            } else {
                context.startService(new Intent(context, StartService.class));
            }

        }

    }
}