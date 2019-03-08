package com.dudukling.enelz;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

public class splashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final ImageView imageRemoveFlicker = findViewById(R.id.imageViewSplashRemoveFlicker);
        imageRemoveFlicker.setVisibility(View.VISIBLE);

        VideoView mVideoView = findViewById(R.id.videoViewSplashLogo);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.enel;
        mVideoView.setVideoURI(Uri.parse(path));
        mVideoView.start();

        Handler handle2 = new Handler();
        handle2.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageRemoveFlicker.setVisibility(View.GONE);
            }
        }, 500);



        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                showLauncher();
            }
        }, 1800);
    }

    private void showLauncher() {
        Intent intent = new Intent(splashActivity.this, menuActivity.class);
        startActivity(intent);
        finish();
    }

}
