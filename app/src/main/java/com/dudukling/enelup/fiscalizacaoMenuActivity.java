package com.dudukling.enelup;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Objects;

public class fiscalizacaoMenuActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("Fiscalizações");
        setContentView(R.layout.activity_menu_fiscalizacao);


        CardView cardViewFiscalizaMenuClandestino = this.findViewById(R.id.cardViewFiscalizaMenuClandestino);
        cardViewFiscalizaMenuClandestino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fiscalizacaoMenuActivity.this, fiscalizacaoClandestinoActivity.class);
                startActivity(intent);
            }
        });

        CardView cardViewFiscalizaMenuQualidade = this.findViewById(R.id.cardViewFiscalizaMenuQualidade);
        cardViewFiscalizaMenuQualidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(fiscalizacaoMenuActivity.this, "Indisponível", Toast.LENGTH_SHORT).show();
            }
        });
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
