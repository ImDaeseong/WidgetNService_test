package com.daeseong.gameprocess;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

        //게임 패키지 정보 설정
        setGamePackageInfo();

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
