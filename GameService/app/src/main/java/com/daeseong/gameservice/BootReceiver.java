package com.daeseong.gameservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        try {

            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction().toString())) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent in = new Intent(context, GameService.class);
                    context.startForegroundService(in);
                } else {
                    Intent in = new Intent(context, GameService.class);
                    context.startService(in);
                }

            }

        }catch (Exception ex){
            Log.e(TAG, ex.getMessage().toString());
        }

    }

}
