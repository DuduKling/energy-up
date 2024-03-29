package com.dudukling.energyup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

public class splashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        final ImageView imageRemoveFlicker = findViewById(R.id.imageViewSplashRemoveFlicker);
//        imageRemoveFlicker.setVisibility(View.VISIBLE);

        final VideoView mVideoView = findViewById(R.id.videoViewSplashLogo);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.energy;
        mVideoView.setVideoURI(Uri.parse(path));
        mVideoView.setZOrderOnTop(true);
        mVideoView.start();

//        Handler handle2 = new Handler();
//        handle2.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                imageRemoveFlicker.setVisibility(View.GONE);
//            }
//        }, 500);


        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                showLauncher();
            }
        }, 3000);
    }

    private void showLauncher() {
        Intent intent = new Intent(splashActivity.this, menuActivity.class);
        startActivity(intent);
        finish();
    }

}
