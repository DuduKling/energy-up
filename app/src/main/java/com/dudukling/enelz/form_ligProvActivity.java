package com.dudukling.enelz;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dudukling.enelz.dao.lpDAO;
import com.dudukling.enelz.model.lpModel;
import com.dudukling.enelz.util.lpFormHelper;
import com.dudukling.enelz.util.mapsController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class form_ligProvActivity extends AppCompatActivity {
    private lpModel lp;
    private lpFormHelper formHelper;
    private String formType;
    private boolean saved = false;
    public List<String> imagesList = new ArrayList<>();
    private FloatingActionButton albumButton;
    private mapsController mapsControl;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_lig_prov);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        lp = (lpModel) intent.getSerializableExtra("lp");
        formType = (String) intent.getSerializableExtra("type");

        setTitle(lp.getOrdem());
        formHelper = new lpFormHelper(this, "new", lp);


        albumButton = findViewById(R.id.buttonAlbum);
        albumButton.setVisibility(View.GONE);
        albumButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent goToAlbum = new Intent(form_ligProvActivity.this, ligProvAlbumActivity.class);
                goToAlbum.putExtra("lp", lp);
                startActivity(goToAlbum);
            }
        });

        mapsControl = new mapsController(this);
        mapsControl.startMaps();

        if (formType.equals("edit")) {setFormEdit(); return;}
        if (formType.equals("readOnly")) {setFormReadOnly();}
    }

    private void setFormEdit() {
        formHelper = new lpFormHelper(this, "edit", lp);
        albumButton.setVisibility(View.GONE);

//        cameraControl = new cameraController(formActivity.this, helperForm.getSample(imagesList));
//        cameraControl.setCameraActions();

//        setAlbumAction();
    }

    private void setFormReadOnly() {
        formHelper = new lpFormHelper(this, "readOnly", lp);
        albumButton.setVisibility(View.VISIBLE);

//        setAlbumAction();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        lpDAO dao = new lpDAO(form_ligProvActivity.this);

        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;

            case R.id.menu_saveLP_button:
                saved = true;
                if(formHelper.validateForm()){
                    lpModel lpSave = formHelper.getLPFromForm(lp, imagesList);

                    dao.updateLPInfo(lpSave);

                    finish();
                }else{
                    Toast.makeText(form_ligProvActivity.this, "Favor preencher todos os campos obrigatórios!", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.menu_edit_button:
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("lp", lp)
                        .putExtra("type", "edit");
                finish();
                overridePendingTransition(0, 0);
                //Toast.makeText(formActivity.this, "editar", Toast.LENGTH_LONG).show();

                startActivity(intent);
                overridePendingTransition(0, 0);

                break;
        }
        dao.close();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(formType.equals("readOnly")){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_form_lig_prov_read, menu);
        }else{
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_form_lig_prov, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        helperForm.gpsControl.disableGPS();
        if(isFinishing()){
//            if(!saved && cameraControl!=null){
//                helperForm.deleteImagesListFromPhoneMemory(imagesList, cameraControl);
//            }
        }
    }
}
