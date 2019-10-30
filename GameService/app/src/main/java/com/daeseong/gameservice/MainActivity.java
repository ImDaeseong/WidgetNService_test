package com.daeseong.gameservice;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

}
