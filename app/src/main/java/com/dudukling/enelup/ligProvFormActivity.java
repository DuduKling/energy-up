package com.dudukling.enelup;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.enelup.dao.lpDAO;
import com.dudukling.enelup.model.lpClandestino;
import com.dudukling.enelup.model.lpModel;
import com.dudukling.enelup.util.lpFormHelper;
import com.dudukling.enelup.util.mapsController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ligProvFormActivity extends AppCompatActivity {
    private static final int GPS_REQUEST_CODE = 999;
    private lpFormHelper formHelper;
    private String formType;

    private lpModel lp;

    private mapsController mapsControl;

    private FloatingActionButton buttonAlbum;
    private FloatingActionButton buttonClandestino;

    private LocationManager locationManager;
    private LocationListener locationListener;


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


        buttonClandestino = findViewById(R.id.buttonClandestino);
        buttonAlbum = findViewById(R.id.buttonAlbum);
        prepareMaps();


        if (formType.equals("edit")) {
            setFormEdit();
            return;
        }
        if (formType.equals("readOnly")) {
            setFormReadOnly();
        }
    }

    private void prepareMaps() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        TypedValue tv = new TypedValue();
        this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);

        RelativeLayout map = findViewById(R.id.map_layout);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) map.getLayoutParams();
        params.height = height - actionBarHeight - 214;
        map.setLayoutParams(params);

        mapsControl = new mapsController(this);
        mapsControl.startMaps(lp);
    }

    private void setFormEdit() {
        formHelper = new lpFormHelper(this, "edit", lp);
        buttonAlbum.setVisibility(View.GONE);
        buttonClandestino.setVisibility(View.GONE);

        getGPSLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == GPS_REQUEST_CODE) {
            if (permissions.length == 1 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                getGPSLocation();

            }else{
                Toast.makeText(this, "Não é possível editar sem GPS!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void getGPSLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_REQUEST_CODE);

        }else{
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            final TextView textViewLegendaPosicaoTeste = findViewById(R.id.textViewLegendaPosicaoTeste);
            final ProgressBar progressBarLegendaPosicaoTeste = findViewById(R.id.progressBarLegendaPosicaoTeste);

            progressBarLegendaPosicaoTeste.setVisibility(View.VISIBLE);

            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    if(location != null){

                        final double[] longitude = {0};
                        final double[] latitude = {0};

                        latitude[0] = location.getLatitude();
                        longitude[0] = location.getLongitude();


                        textViewLegendaPosicaoTeste.setText("Lat: " + latitude[0] + "\nLong: " + longitude[0]);
                        progressBarLegendaPosicaoTeste.setVisibility(View.GONE);

                        lp.setAutoLat(String.valueOf(latitude[0]));
                        lp.setAutoLong(String.valueOf(longitude[0]));
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                    progressBarLegendaPosicaoTeste.setVisibility(View.VISIBLE);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    if (provider.equals(LocationManager.GPS_PROVIDER)) {
                        progressBarLegendaPosicaoTeste.setVisibility(View.GONE);
                        Toast.makeText(ligProvFormActivity.this, "Favor, habilitar o GPS!", Toast.LENGTH_LONG).show();
                        Intent startGPSIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        ligProvFormActivity.this.startActivity(startGPSIntent);
                    }
                }
            };

            assert locationManager != null;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        }
    }

    private void setFormReadOnly() {
        formHelper = new lpFormHelper(this, "readOnly", lp);

        buttonAlbum.setVisibility(View.VISIBLE);
        buttonClandestino.setVisibility(View.VISIBLE);

        buttonAlbum.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent goToAlbum = new Intent(ligProvFormActivity.this, ligProvAlbumActivity.class);
                goToAlbum.putExtra("lp", lp);
                startActivity(goToAlbum);
            }
        });

        buttonClandestino.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent goToClandestino = new Intent(ligProvFormActivity.this, clandestinoFormActivity.class);
                goToClandestino
                        .putExtra("clandest", new lpClandestino())
                        .putExtra("lpOrdem", lp.getOrdem())
                        .putExtra("type", "new");
                startActivity(goToClandestino);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;

            case R.id.menu_save_button:
                switch (formHelper.validateForm(lp)) {
                    case "true":
                        salvaLP();
                        break;

                    case "false":
                        Toast.makeText(ligProvFormActivity.this, "Favor preencher todos os campos obrigatórios!", Toast.LENGTH_LONG).show();
                        break;

                    case "gps":
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    salvaLP();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Não foi possível detectar sua localização pelo GPS, deseja salvar mesmo assim?")
                                .setPositiveButton("Salvar mesmo assim", dialogClickListener)
                                .setNegativeButton("Aguardar um pouco", dialogClickListener).show();
                        break;
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

        return super.onOptionsItemSelected(item);
    }

    private void salvaLP() {
        lpModel lpSave = formHelper.getLPFromForm(lp);

        lpDAO dao = new lpDAO(ligProvFormActivity.this);
        dao.updateLPInfo(lpSave);
        dao.close();

        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(formType.equals("readOnly")){
            inflater.inflate(R.menu.menu_form_edit, menu);
        }else{
            inflater.inflate(R.menu.menu_form_save, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(locationListener!=null){
            locationManager.removeUpdates(locationListener);
        }

//        locationManager.removeUpdates(locationListener);
//        if(isFinishing()){
////            if(!saved && cameraControl!=null){
////                helperForm.deleteImagesListFromPhoneMemory(imagesList, cameraControl);
////            }
//        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1123:
                if (resultCode == 2222) {
                    formHelper.updateInfoLevCarga();
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        lpModel lpsaved = formHelper.getLPFromForm(lp);
        outState.putSerializable("lp", lpsaved);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lpModel lpSaved = (lpModel) savedInstanceState.getSerializable("lp");
        formHelper.fillForm(lpSaved);
    }
}
