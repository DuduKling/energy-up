package com.dudukling.enelup.ligacaoes_provisorias;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dudukling.enelup.R;
import com.dudukling.enelup.adapter.ligProvAlbum_recyclerAdapter;
import com.dudukling.enelup.dao.lpDAO;
import com.dudukling.enelup.model.lpModel;
import com.dudukling.enelup.util.cameraController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ligProvAlbumActivity extends AppCompatActivity {
    public static final int CAMERA_PERMISSION_CODE = 333;
    public static final int CAMERA_REQUEST_CODE = 444;

    private RecyclerView recyclerView;
    private lpModel lp;
    private cameraController cameraControl;
    public List<String> imagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_lig_prov);
        recyclerView = findViewById(R.id.lp_album_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        lp = (lpModel) intent.getSerializableExtra("lp");

        setTitle("Album " + lp.getOrdem());
    }

    @Override
    protected void onResume() {
        recyclerView.setAdapter(new ligProvAlbum_recyclerAdapter(lp, this));
        RecyclerView.LayoutManager layout = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layout);

        lpDAO dao = new lpDAO(this);
        imagesList = dao.getImagesDB(lp.getId());
        dao.close();

        TextView textViewNoFotos = this.findViewById(R.id.textViewNoFotos);
        if(imagesList.size()>0){
            textViewNoFotos.setVisibility(View.GONE);
        }else{
            textViewNoFotos.setVisibility(View.VISIBLE);
        }

        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menu_LPcamera:
                if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
                } else {
                    cameraControl = new cameraController(ligProvAlbumActivity.this, lp);
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
                lpDAO dao = new lpDAO(this);
                dao.insertImage(lp, cameraControl.getPhotoPath());
                imagesList = dao.getImagesDB(lp.getId());
                dao.close();

//                imagesList.add(cameraControl.getPhotoPath());
//                lp.setImagesList(imagesList);
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
                cameraControl = new cameraController(ligProvAlbumActivity.this, lp);
                cameraControl.startCamera();
            }
        }
    }
}
