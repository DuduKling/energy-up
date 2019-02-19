package com.dudukling.enelz;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.dudukling.enelz.model.lpModel;
import com.dudukling.enelz.util.lpFormHelper;

import java.util.Objects;

public class form_ligProvActivity extends AppCompatActivity {
    private lpModel lp;
    private lpFormHelper formHelper;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_lig_prov);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        lp = (lpModel) intent.getSerializableExtra("lp");

        setTitle("Ordem: " + lp.getOrdem());
        formHelper = new lpFormHelper(this, "new", lp);

//        cameraControl = new cameraController(formActivity.this, helperForm.getSample(imagesList));
//        cameraControl.setCameraActions();
    }


}
