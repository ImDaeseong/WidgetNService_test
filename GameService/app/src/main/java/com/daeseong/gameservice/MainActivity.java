package com.daeseong.gameservice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Intent intent = null;
    static RestartService restartService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //사용정보 접근 허용
        checkPermissions();

        //게임 패키지 정보 설정
        setGamePackageInfo();

        restartService = new RestartService();

        intent = new Intent(MainActivity.this, StartService.class);
        IntentFilter intentFilter = new IntentFilter("com.daeseong.gameservice.StartService");
        registerReceiver(restartService, intentFilter);
        startService(intent);

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(restartService);
    }

    private void checkPermissions() {

        boolean granted = false;
        AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,android.os.Process.myUid(), getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }

        if (granted == false)
        {
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 1);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 1){

        }
    }

    private void setGamePackageInfo(){
        GameInfo.getInstance().setGameItem("net.gameply.android.moonlight");
        GameInfo.getInstance().setGameItem("com.kakaogames.moonlight");
        GameInfo.getInstance().setGameItem("com.ncsoft.lineagem19");
        GameInfo.getInstance().setGameItem("com.lilithgames.rok.gpkr");
        GameInfo.getInstance().setGameItem("com.netmarble.lineageII");
        GameInfo.getInstance().setGameItem("com.bluepotiongames.eosm");
        GameInfo.getInstance().setGameItem("com.qjzj4399kr.google");
        GameInfo.getInstance().setGameItem("com.netmarble.bnsmkr");
        GameInfo.getInstance().setGameItem("com.zlongame.kr.langrisser");
        GameInfo.getInstance().setGameItem("com.pearlabyss.blackdesertm");
    }
}
