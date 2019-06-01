package com.dudukling.enelup.ligacaoes_provisorias;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dudukling.enelup.R;

import java.util.Objects;

public class ligProvImageActivity  extends AppCompatActivity {
    ImageView fullImage;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_lig_prov);
        setTitle("Image " + getIntent().getStringExtra("position"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }

        url = getIntent().getStringExtra("image_url");

        fullImage = findViewById(R.id.viewFullImage);
        Glide.with(this).load(url)
                .into(fullImage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
