package com.dudukling.energyup.fiscalizacao_clandestino;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dudukling.energyup.R;
import com.dudukling.energyup.adapter.fiscaClandAlbum_recyclerAdapter;
import com.dudukling.energyup.dao.fiscaClandDAO;
import com.dudukling.energyup.model.fiscaClandModel;
import com.dudukling.energyup.fiscalizacao_clandestino.util.fiscaClandCameraController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class fiscaClandAlbumActivity extends AppCompatActivity {
    public static final int CAMERA_PERMISSION_CODE = 333;
    public static final int CAMERA_REQUEST_CODE = 444;

    private RecyclerView recyclerView;
    private fiscaClandModel fisca;
    private fiscaClandCameraController cameraControl;
    public List<String> imagesList = new ArrayList<>();
    
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fisca_cland_activity_album);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recyclerViewFiscaAlbum);

        Intent intent = getIntent();
        fisca = (fiscaClandModel) intent.getSerializableExtra("fisca");

        setTitle("Album " + fisca.getId());
    }

    @Override
    protected void onResume() {
        recyclerView.setAdapter(new fiscaClandAlbum_recyclerAdapter(fisca, this));
        RecyclerView.LayoutManager layout = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layout);

        fiscaClandDAO dao = new fiscaClandDAO(this);
        imagesList = dao.getImagesDB(fisca.getId());
        dao.close();

        TextView textViewNoFotos = this.findViewById(R.id.textViewFiscaNoFotos);
        if(imagesList.size()>0){
            textViewNoFotos.setVisibility(View.GONE);
        }else{
            textViewNoFotos.setVisibility(View.VISIBLE);
        }

        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menu_LPcamera:
                if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
                } else {
                    cameraControl = new fiscaClandCameraController(fiscaClandAlbumActivity.this, fisca);
                    cameraControl.setCameraActions();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                fiscaClandDAO dao = new fiscaClandDAO(this);
                dao.insertImage(fisca, cameraControl.getPhotoPath());
                imagesList = dao.getImagesDB(fisca.getId());
                dao.close();
            }
        }else{
            if (requestCode == CAMERA_REQUEST_CODE) {
                cameraControl.deleteTempFromPhoneMemory(cameraControl.getPhotoFile().toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_camera_album, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_DENIED) {
                cameraControl = new fiscaClandCameraController(fiscaClandAlbumActivity.this, fisca);
                cameraControl.startCamera();
            }
        }
    }
}
