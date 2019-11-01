package com.daeseong.gameservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //아이콘 숨기기
        hideAppIcon();

        startService();

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startService(){

        try {

            Intent service = null;
            if (GameService.serviceIntent == null) {
                service = new Intent(this, GameService.class);
                startService(service);
            } else {
                service = GameService.serviceIntent;
                Toast.makeText(getApplicationContext(), "이미 실행중입니다.", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception ex){
            Log.e(TAG, ex.getMessage().toString());
        }

    }

    private void hideAppIcon(){

        ComponentName componentName = new ComponentName("com.daeseong.gameservice", "com.daeseong.gameservice.MainActivity");
        getPackageManager().setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

    }

}
