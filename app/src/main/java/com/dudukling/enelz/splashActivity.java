package com.dudukling.enelz;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class splashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                showLauncher();
            }
        }, 1500);
    }

    private void showLauncher() {
        Intent intent = new Intent(splashActivity.this, collectionActivity.class);
        startActivity(intent);
        finish();
    }

}
