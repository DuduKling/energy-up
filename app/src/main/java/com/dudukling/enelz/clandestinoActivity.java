package com.dudukling.enelz;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.enelz.adapter.clandestino_recyclerAdapter;
import com.dudukling.enelz.adapter.ligProv_recyclerAdapter;
import com.dudukling.enelz.dao.lpDAO;
import com.dudukling.enelz.model.lpClandestino;
import com.dudukling.enelz.model.lpModel;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;

public class clandestinoActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private clandestino_recyclerAdapter RecyclerAdapter;
    private List<lpClandestino> lpClandest;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clandestino);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("Pontos Clandestinos");
        recyclerView = findViewById(R.id.recyclerViewClandest);

        registerForContextMenu(recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onResume() {
        super.onResume();
        lpDAO dao = new lpDAO(this);
        lpClandest = dao.getClandestinoList();

        RecyclerAdapter = new clandestino_recyclerAdapter(lpClandest, this);
        recyclerView.setAdapter(RecyclerAdapter);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);

        TextView textViewNoClandestinos = this.findViewById(R.id.textViewNoClandestinos);
        if(lpClandest.size()>0){
            textViewNoClandestinos.setVisibility(View.GONE);
        }else{
            textViewNoClandestinos.setVisibility(View.VISIBLE);
        }

        dao.close();
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
}
