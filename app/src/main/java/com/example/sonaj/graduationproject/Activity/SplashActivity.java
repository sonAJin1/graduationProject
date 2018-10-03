package com.example.sonaj.graduationproject.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.sonaj.graduationproject.Activity.MainActivity;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(2000); // 지속시간
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
