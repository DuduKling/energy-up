package com.dudukling.enelup.fiscalizacao_clandestino;

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

import com.dudukling.enelup.R;
import com.dudukling.enelup.adapter.fiscalizacaoAlbum_recyclerAdapter;
import com.dudukling.enelup.dao.fiscalizaDAO;
import com.dudukling.enelup.model.fiscaModel;
import com.dudukling.enelup.util.cameraFiscaController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class fiscalizacaoClandestinoAlbumActivity extends AppCompatActivity {
    public static final int CAMERA_PERMISSION_CODE = 333;
    public static final int CAMERA_REQUEST_CODE = 444;

    private RecyclerView recyclerView;
    private fiscaModel fisca;
    private cameraFiscaController cameraControl;
    public List<String> imagesList = new ArrayList<>();
    
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fisc_clandest_activity_album);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recyclerViewFiscaAlbum);

        Intent intent = getIntent();
        fisca = (fiscaModel) intent.getSerializableExtra("fisca");

        setTitle("Album " + fisca.getId());
    }

    @Override
    protected void onResume() {
        recyclerView.setAdapter(new fiscalizacaoAlbum_recyclerAdapter(fisca, this));
        RecyclerView.LayoutManager layout = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layout);

        fiscalizaDAO dao = new fiscalizaDAO(this);
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
                    cameraControl = new cameraFiscaController(fiscalizacaoClandestinoAlbumActivity.this, fisca);
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
                fiscalizaDAO dao = new fiscalizaDAO(this);
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
                cameraControl = new cameraFiscaController(fiscalizacaoClandestinoAlbumActivity.this, fisca);
                cameraControl.startCamera();
            }
        }
    }
}