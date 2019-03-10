package com.dudukling.enelz;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.enelz.dao.lpDAO;
import com.dudukling.enelz.model.lpClandestino;

import java.util.Objects;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

public class clandestinoFormActivity extends AppCompatActivity {
    private static final String REQUIRED_FIELD_ERROR_MSG = "Campo obrigatório!";
    private static final int GPS_REQUEST_CODE = 999;

    private int LPid;
    private lpClandestino lpClandest = new lpClandestino();
    private String tipoForm;

    private TextInputLayout textInputLayoutClandestEndereco;
    private TextInputLayout textInputLayoutClandestTransformador;
    private TextInputLayout textInputLayoutClandestTensao;
    private TextInputLayout textInputLayoutClandestCorrente;
    private TextInputLayout textInputLayoutClandestProtecao;
    private TextInputLayout textInputLayoutClandestFatorPotencia;
    private TextInputLayout textInputLayoutClandestCarga;
    private TextInputLayout textInputLayoutClandestDescricao;
    private TextView textViewClandestNumero;
    private TextView textViewClandestLatLong;
    private TextView textViewPotCalculada;

    private LocationManager locationManager;
    private LocationListener locationListener;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_clandestino_form);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setTitle("Ponto clandestino");

        Intent intent = getIntent();
        LPid = (int) intent.getSerializableExtra("lpID");
        lpClandest = (lpClandestino) intent.getSerializableExtra("clandest");
        tipoForm = (String) intent.getSerializableExtra("type");

        textViewClandestNumero = this.findViewById(R.id.textViewClandestNumero);
        setFields();
        if(tipoForm.equals("readOnly")){
            fillForm();
            disableFields();
        }else if(tipoForm.equals("edit")){
            fillForm();
            setValidation();
            setCalculus();
        }else if(tipoForm.equals("new")){
            setValidation();

            lpDAO dao = new lpDAO(this);
            int nextID = dao.getClandestinoLastID() + 1;
            dao.close();

            textViewClandestNumero.setText("Clandestino #"+nextID);
            getGPSLocation();
            setCalculus();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == GPS_REQUEST_CODE) {
            if (permissions.length == 1 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                getGPSLocation();

            }else{
                Toast.makeText(this, "Não é possível cadastrar sem GPS!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void getGPSLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_REQUEST_CODE);

        }else{
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            final TextView textViewClandestLatLong = findViewById(R.id.textViewClandestLatLong);
            final ProgressBar progressBarClandestLatLong = findViewById(R.id.progressBarClandestLatLong);

            progressBarClandestLatLong.setVisibility(View.VISIBLE);

            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    if(location != null){

                        final double[] longitude = {0};
                        final double[] latitude = {0};

                        latitude[0] = location.getLatitude();
                        longitude[0] = location.getLongitude();


                        textViewClandestLatLong.setText("Lat: " + latitude[0] + "| Long: " + longitude[0]);
                        progressBarClandestLatLong.setVisibility(View.GONE);

                        lpClandest.setAutoLat(String.valueOf(latitude[0]));
                        lpClandest.setAutoLong(String.valueOf(longitude[0]));
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                    progressBarClandestLatLong.setVisibility(View.VISIBLE);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    if (provider.equals(LocationManager.GPS_PROVIDER)) {
                        progressBarClandestLatLong.setVisibility(View.GONE);
                        Toast.makeText(clandestinoFormActivity.this, "Favor, habilitar o GPS!", Toast.LENGTH_LONG).show();
                        Intent startGPSIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        clandestinoFormActivity.this.startActivity(startGPSIntent);
                    }
                }
            };

            assert locationManager != null;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        }
    }

    private void disableFields() {
        disable(textInputLayoutClandestEndereco);
        disable(textInputLayoutClandestTransformador);
        disable(textInputLayoutClandestTensao);
        disable(textInputLayoutClandestCorrente);
        disable(textInputLayoutClandestProtecao);
        disable(textInputLayoutClandestFatorPotencia);
        disable(textInputLayoutClandestCarga);
        disable(textInputLayoutClandestDescricao);
    }

    private void disable(TextInputLayout textInputCampo) {
        EditText editText = textInputCampo.getEditText();

        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setTextColor(Color.parseColor("#616161"));
    }

    private void fillForm() {
        textInputLayoutClandestEndereco.getEditText().setText(lpClandest.getEndereco());
        textInputLayoutClandestTransformador.getEditText().setText(lpClandest.getTransformador());
        textInputLayoutClandestTensao.getEditText().setText(lpClandest.getTensao());
        textInputLayoutClandestCorrente.getEditText().setText(lpClandest.getCorrente());
        textInputLayoutClandestProtecao.getEditText().setText(lpClandest.getProtecao());
        textInputLayoutClandestFatorPotencia.getEditText().setText(lpClandest.getFatorPotencia());
        textInputLayoutClandestCarga.getEditText().setText(lpClandest.getCarga());
        textInputLayoutClandestDescricao.getEditText().setText(lpClandest.getDescricao());

        textViewClandestLatLong.setText("Lat: "+lpClandest.getAutoLat()+" | Long: "+lpClandest.getAutoLong());

        textViewClandestNumero.setText("Clandestino #"+lpClandest.getId());

        calculaPotEncontrada();
    }

    private void calculaPotEncontrada() {
        // Potência (W) = Tensão (V) * Corrente (A)* fator de potência (entre 0 e 1)

        int tensao = 0;
        if(lpClandest.getTensao()!=null){
            if(!lpClandest.getTensao().equals("")){
                tensao = Integer.valueOf(lpClandest.getTensao());
            }
        }

        int corrente = 0;
        if(lpClandest.getCorrente()!=null){
            if(!lpClandest.getCorrente().equals("")){
                corrente = Integer.parseInt(lpClandest.getCorrente());
            }
        }

        float fatorPotencia = 0;
        if(lpClandest.getFatorPotencia()!=null){
            if(!lpClandest.getFatorPotencia().equals("")){
                fatorPotencia = Float.parseFloat(lpClandest.getFatorPotencia());
            }
        }


        float potEncontrada = tensao * corrente * fatorPotencia;
        if(potEncontrada==0){
            textViewPotCalculada.setText("Potência encontrada: ---");
        }else{
            textViewPotCalculada.setText("Potência encontrada: "+(potEncontrada/1000)+" KWh");
        }

    }

    private void setCalculus() {
        textInputLayoutClandestTensao.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null || !String.valueOf(s).equals("")){
                    lpClandest.setTensao(String.valueOf(s));
                }else{
                    lpClandest.setTensao(null);
                }
                calculaPotEncontrada();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        textInputLayoutClandestCorrente.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null || !String.valueOf(s).equals("")){
                    lpClandest.setCorrente(s.toString());
                }else{
                    lpClandest.setCorrente(null);
                }

                calculaPotEncontrada();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        textInputLayoutClandestFatorPotencia.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null || !String.valueOf(s).equals("")){
                    lpClandest.setFatorPotencia(s.toString());
                }else{
                    lpClandest.setFatorPotencia(null);
                }
                calculaPotEncontrada();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void setFields() {
        textInputLayoutClandestEndereco = this.findViewById(R.id.textInputLayoutClandestEndereco);
        textInputLayoutClandestTransformador = this.findViewById(R.id.textInputLayoutClandestTransformador);
        textInputLayoutClandestTensao = this.findViewById(R.id.textInputLayoutClandestTensao);
        textInputLayoutClandestCorrente = this.findViewById(R.id.textInputLayoutClandestCorrente);
        textInputLayoutClandestProtecao = this.findViewById(R.id.textInputLayoutClandestProtecao);
        textInputLayoutClandestFatorPotencia = this.findViewById(R.id.textInputLayoutClandestFatorPotencia);
        textInputLayoutClandestCarga = this.findViewById(R.id.textInputLayoutClandestCarga);
        textInputLayoutClandestDescricao = this.findViewById(R.id.textInputLayoutClandesDescricao);

        textViewClandestLatLong = this.findViewById(R.id.textViewClandestLatLong);
        textViewPotCalculada = this.findViewById(R.id.textViewPotCalculada);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(tipoForm.equals("readOnly")){
            inflater.inflate(R.menu.menu_form_lig_prov_read, menu);
        }else{
            inflater.inflate(R.menu.menu_form_lig_prov, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;

            case R.id.menu_saveLP_button:
                switch (validateForm()) {
                    case "true":
                        salvaClandest();
                        break;
                    case "false":
                        Toast.makeText(clandestinoFormActivity.this, "Favor preencher todos os campos obrigatórios!", Toast.LENGTH_LONG).show();
                        break;
                    case "gps":
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        salvaClandest();
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
                        .putExtra("clandest", lpClandest)
                        .putExtra("lpID", 0)
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

    private void salvaClandest() {
        lpDAO dao = new lpDAO(this);

        lpClandestino clandestSave = getLPFromForm();
        if(tipoForm.equals("new")){
            dao.insertClandestino(clandestSave, LPid);
        }else{
            dao.updateClandestino(clandestSave);
        }

        dao.close();
        finish();
    }

    public void setValidation(){
        setValidateEmpty(textInputLayoutClandestEndereco);
        setValidateEmpty(textInputLayoutClandestTransformador);
        setValidateEmpty(textInputLayoutClandestTensao);
        setValidateEmpty(textInputLayoutClandestCorrente);
        setValidateEmpty(textInputLayoutClandestProtecao);
//        setValidateEmpty(textInputLayoutClandestFatorPotencia);
        setValidateEmpty(textInputLayoutClandestCarga);
        setValidateEmpty(textInputLayoutClandestDescricao);

        setValidateFatPotencia(textInputLayoutClandestFatorPotencia);
    }

    private boolean setValidateFatPotencia(final TextInputLayout textInputLayout) {
        final EditText campo = textInputLayout.getEditText();
        assert campo != null;
        campo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
                if(!hasFocus){
                    String text = campo.getText().toString();
                    if(text.isEmpty()){
                        textInputLayout.setError(REQUIRED_FIELD_ERROR_MSG);
                    }else{
                        if(Float.parseFloat(text) < 0 || Float.parseFloat(text) > 1){
                            textInputLayout.setError("Valor deve ser entre 0 e 1");
                        }
                    }
                }else{
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                }
            }
        });
        return false;
    }


    private void setValidateEmpty(final TextInputLayout textInputCampo){
        final EditText campo = textInputCampo.getEditText();
        assert campo != null;
        campo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                textInputCampo.setError(null);
                textInputCampo.setErrorEnabled(false);
                if(!hasFocus){
                    String text = campo.getText().toString();
                    if(text.isEmpty()){
                        textInputCampo.setError(REQUIRED_FIELD_ERROR_MSG);
                    }
                }else{
                    textInputCampo.setError(null);
                    textInputCampo.setErrorEnabled(false);
                }
            }
        });
    }

    public String validateForm() {
        if(fieldIsEmpty(textInputLayoutClandestEndereco)){return "false";}
        if(fieldIsEmpty(textInputLayoutClandestTransformador)){return "false";}
        if(fieldIsEmpty(textInputLayoutClandestTensao)){return "false";}
        if(fieldIsEmpty(textInputLayoutClandestCorrente)){return "false";}
        if(fieldIsEmpty(textInputLayoutClandestProtecao)){return "false";}
        if(fieldIsEmpty(textInputLayoutClandestFatorPotencia)){return "false";}
        if(fieldIsEmpty(textInputLayoutClandestCarga)){return "false";}
        if(fieldIsEmpty(textInputLayoutClandestDescricao)){return "false";}

        if(setValidateFatPotencia(textInputLayoutClandestFatorPotencia)){return "false";}

        if(lpClandest.getAutoLat().isEmpty() || lpClandest.getAutoLong().isEmpty()){return "gps";}

        return "true";
    }

    private boolean fieldIsEmpty(TextInputLayout textInputCampo) {
        EditText campo = textInputCampo.getEditText();
        assert campo != null;
        String text = campo.getText().toString();
        if(text.isEmpty()) {
            textInputCampo.setError(REQUIRED_FIELD_ERROR_MSG);
        }else{
            textInputCampo.setError(null);
            textInputCampo.setErrorEnabled(false);
        }
        return text.isEmpty();
    }

    public lpClandestino getLPFromForm() {

        lpClandest.setEndereco(textInputLayoutClandestEndereco.getEditText().getText().toString());
        lpClandest.setTransformador(textInputLayoutClandestTransformador.getEditText().getText().toString());
        lpClandest.setTensao(textInputLayoutClandestTensao.getEditText().getText().toString());
        lpClandest.setCorrente(textInputLayoutClandestCorrente.getEditText().getText().toString());
        lpClandest.setProtecao(textInputLayoutClandestProtecao.getEditText().getText().toString());
        lpClandest.setFatorPotencia(textInputLayoutClandestFatorPotencia.getEditText().getText().toString());
        lpClandest.setCarga(textInputLayoutClandestCarga.getEditText().getText().toString());
        lpClandest.setDescricao(textInputLayoutClandestDescricao.getEditText().getText().toString());

        return lpClandest;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(locationListener!=null){
            locationManager.removeUpdates(locationListener);
        }
    }
}
