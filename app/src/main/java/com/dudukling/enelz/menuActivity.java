package com.dudukling.enelz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

public class menuActivity extends AppCompatActivity {
    private CardView cardViewLP;
    private CardView cardViewImportar;
    private CardView cardViewFiscal;
    private CardView cardViewMenuClandestino;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Menu");
        setContentView(R.layout.activity_menu);


        cardViewLP = this.findViewById(R.id.cardViewMenuLP);
        cardViewLP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menuActivity.this, ligProvActivity.class);
                startActivity(intent);
            }
        });

        cardViewImportar = this.findViewById(R.id.cardViewMenuImportar);
        cardViewImportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menuActivity.this, importActivity.class);
                startActivity(intent);
            }
        });

        cardViewMenuClandestino = this.findViewById(R.id.cardViewMenuClandestino);
        cardViewMenuClandestino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menuActivity.this, clandestinoActivity.class);
                startActivity(intent);
            }
        });



        cardViewFiscal = this.findViewById(R.id.cardViewMenuFiscal);
        cardViewFiscal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(menuActivity.this, "Indisponível", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
