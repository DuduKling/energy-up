package com.dudukling.enelz;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.dudukling.enelz.model.lpClandestino;

import java.util.Objects;

//public class clandestinoActivity extends AppCompatActivity implements clandestinoFrag.OnItemSelectedListener {
public class clandestinoActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clandestino);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("Pontos Clandestinos");


        fragmentManager = getSupportFragmentManager();
        FragmentTransaction tx = fragmentManager.beginTransaction();
        tx.replace(R.id.fragmentClandestino, new clandestinoFrag());
        tx.commit();
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

//    @Override
//    public void onItemSelected(lpClandestino clandest, int lpID, String type) {
//        FragmentTransaction tx = fragmentManager.beginTransaction();
//        tx.replace(R.id.fragmentClandestino, clandestinoFragFormNewInstance(clandest, lpID, type));
//        tx.commit();
//
//        setTitle("Ponto clandestino");
//    }

//    public static clandestinoFormActivity clandestinoFragFormNewInstance(lpClandestino clandest, int lpID, String type) {
//        clandestinoFormActivity f = new clandestinoFormActivity();
//        Bundle args = new Bundle();
//        args.putSerializable("clandest", clandest);
//        args.putSerializable("lpID", lpID);
//        args.putSerializable("type", type);
//        f.setArguments(args);
//        return f;
//    }
}
