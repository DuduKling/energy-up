package com.dudukling.enelz;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dudukling.enelz.adapter.levCarga_recyclerAdapter;
import com.dudukling.enelz.dao.lpDAO;
import com.dudukling.enelz.model.lpPotencia;
import com.dudukling.enelz.util.lpFormHelper;

import java.util.List;
import java.util.Objects;

public class LigProvLevCargaActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLevCarga;
    private List<lpPotencia> lpPotencia;
    private levCarga_recyclerAdapter RecyclerAdapter;
    private int lpID;
    private String typeofForm;
    private int potenciaTotalLevCarga;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_form_lig_prov_lev_carga);
        setTitle("Levantamento de Carga");

        recyclerViewLevCarga = findViewById(R.id.recyclerViewLevCarga);
        registerForContextMenu(recyclerViewLevCarga);
        recyclerViewLevCarga.addItemDecoration(new DividerItemDecoration(recyclerViewLevCarga.getContext(), DividerItemDecoration.VERTICAL));

        Intent intent = getIntent();
        lpID = (int) intent.getSerializableExtra("LPid");
        typeofForm = (String) intent.getSerializableExtra("type");

        FloatingActionButton floatingActionButtonLevCargaAdd = findViewById(R.id.floatingActionButtonLevCargaAdd);
        if(!typeofForm.equals("readOnly")){
            floatingActionButtonLevCargaAdd.setVisibility(View.VISIBLE);
            floatingActionButtonLevCargaAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToLevCargaForm = new Intent(LigProvLevCargaActivity.this, LigProvLevCargaFormActivity.class);
                    goToLevCargaForm
                            .putExtra("LPid", lpID);
                    startActivity(goToLevCargaForm);
                }
            });
        }else{
            floatingActionButtonLevCargaAdd.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        lpDAO dao = new lpDAO(this);
        lpPotencia = dao.getLPPotenciaList(lpID);
        dao.close();

        RecyclerAdapter = new levCarga_recyclerAdapter(lpPotencia, this, lpID, typeofForm);

        recyclerViewLevCarga.setAdapter(RecyclerAdapter);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewLevCarga.setLayoutManager(layout);


        TextView textViewLevCargaNoRecords = findViewById(R.id.textViewLevCargaNoRecords);
        if (lpPotencia.size() > 0) {
            textViewLevCargaNoRecords.setVisibility(View.GONE);
        } else {
            textViewLevCargaNoRecords.setVisibility(View.VISIBLE);
        }

        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        setResult(2222);
        super.finish();
    }
}
