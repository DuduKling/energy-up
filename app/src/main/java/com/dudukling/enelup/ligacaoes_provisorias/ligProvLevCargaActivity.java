package com.dudukling.enelup.ligacaoes_provisorias;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dudukling.enelup.R;
import com.dudukling.enelup.adapter.levCarga_recyclerAdapter;
import com.dudukling.enelup.dao.lpDAO;
import com.dudukling.enelup.model.lpPotenciaModel;

import java.util.List;
import java.util.Objects;

public class ligProvLevCargaActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLevCarga;
    private List<lpPotenciaModel> lpPotenciaModel;
    private levCarga_recyclerAdapter RecyclerAdapter;
    private int lpID;
    private String typeofForm;
    private int potenciaTotalLevCarga;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.lig_prov_activity_form_lev_carga_main);
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
                    Intent goToLevCargaForm = new Intent(ligProvLevCargaActivity.this, ligProvLevCargaFormActivity.class);
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
        lpPotenciaModel = dao.getLPPotenciaList(lpID);
        dao.close();

        RecyclerAdapter = new levCarga_recyclerAdapter(lpPotenciaModel, this, lpID, typeofForm);

        recyclerViewLevCarga.setAdapter(RecyclerAdapter);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewLevCarga.setLayoutManager(layout);


        TextView textViewLevCargaNoRecords = findViewById(R.id.textViewLevCargaNoRecords);
        if (lpPotenciaModel.size() > 0) {
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
