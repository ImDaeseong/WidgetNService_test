package com.daeseong.gameprocess;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Intent intent = null;
    static RestartService restartService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //사용정보 접근 허용
        checkPermissions();

        restartService = new RestartService();

        intent = new Intent(MainActivity.this, StartService.class);
        IntentFilter intentFilter = new IntentFilter("com.daeseong.gameprocess.StartService");
        registerReceiver(restartService, intentFilter);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(restartService);
    }

    private void checkPermissions() {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.PACKAGE_USAGE_STATS ) != PackageManager.PERMISSION_GRANTED) {

            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 1);

        }else {

            Log.e(TAG, "checkPermissions:" + "권한 있음");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 1){

        }
    }

}
