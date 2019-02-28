package com.dudukling.enelz;

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
import com.dudukling.enelz.model.lpClandestino;
import com.dudukling.enelz.model.lpModel;
import com.dudukling.enelz.util.lpFormHelper;
import com.dudukling.enelz.util.mapsController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ligProvFormActivity extends AppCompatActivity {
    private lpFormHelper formHelper;
    private String formType;

    private lpModel lp;
    public List<String> imagesList = new ArrayList<>();

    private mapsController mapsControl;

    private FloatingActionButton buttonAlbum;
    private FloatingActionButton buttonClandestino;

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


        buttonAlbum = findViewById(R.id.buttonAlbum);
        buttonAlbum.setVisibility(View.GONE);
        buttonAlbum.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent goToAlbum = new Intent(ligProvFormActivity.this, ligProvAlbumActivity.class);
                goToAlbum.putExtra("lp", lp);
                startActivity(goToAlbum);
            }
        });

        buttonClandestino = findViewById(R.id.buttonClandestino);
        buttonClandestino.setVisibility(View.GONE);
        buttonClandestino.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent goToClandestino = new Intent(ligProvFormActivity.this, clandestinoFormActivity.class);
                goToClandestino
                        .putExtra("clandest", new lpClandestino())
                        .putExtra("lpID", lp.getId())
                        .putExtra("type", "new");
                startActivity(goToClandestino);
            }
        });

        mapsControl = new mapsController(this);
        mapsControl.startMaps();

        if (formType.equals("edit")) {setFormEdit(); return;}
        if (formType.equals("readOnly")) {setFormReadOnly();}
    }

    private void setFormEdit() {
        formHelper = new lpFormHelper(this, "edit", lp);
        buttonAlbum.setVisibility(View.GONE);
        buttonClandestino.setVisibility(View.GONE);
    }

    private void setFormReadOnly() {
        formHelper = new lpFormHelper(this, "readOnly", lp);
        buttonAlbum.setVisibility(View.VISIBLE);
        buttonClandestino.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        lpDAO dao = new lpDAO(ligProvFormActivity.this);

        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;

            case R.id.menu_saveLP_button:
                if(formHelper.validateForm()){
                    lpModel lpSave = formHelper.getLPFromForm(lp, imagesList);

                    dao.updateLPInfo(lpSave);

                    this.finish();
                }else{
                    Toast.makeText(ligProvFormActivity.this, "Favor preencher todos os campos obrigatórios!", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.menu_edit_button:
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent
                        .putExtra("lp", lp)
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
