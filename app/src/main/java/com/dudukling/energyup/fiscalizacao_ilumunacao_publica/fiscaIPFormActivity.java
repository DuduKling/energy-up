package com.dudukling.energyup.fiscalizacao_ilumunacao_publica;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.energyup.R;
import com.dudukling.energyup.dao.fiscaIPDAO;
import com.dudukling.energyup.model.fiscaIPModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static com.dudukling.energyup.util.reverseGeocoding.getAddressFromLocation;

public class fiscaIPFormActivity extends AppCompatActivity {
    private static final String REQUIRED_FIELD_ERROR_MSG = "Campo obrigatório!";
    private static final int GPS_REQUEST_CODE = 999;

    private fiscaIPModel fisca;
    private String formType;

    private TextInputLayout textInputLayoutFiscaIPFormBairro;
    private TextInputLayout textInputLayoutFiscaIPFormEndereco;
    private TextInputLayout textInputLayoutFiscaIPFormPotencia;
    private TextInputLayout textInputLayoutFiscaIPFormObservacao;
    private TextInputLayout textInputLayoutFiscaIPFormPlaqueta;
    private TextInputLayout textInputLayoutFiscaIPFormLatitude;
    private TextInputLayout textInputLayoutFiscaIPFormLongitude;

    private Spinner spinnerFiscaIPFormFuncionario;
    private Spinner spinnerFiscaIPFormMunicipio;
    private Spinner spinnerFiscaIPFormTipoLuminaria;
    private ArrayAdapter<CharSequence> spinnerFiscaIPFormFuncionarioAdapter;
    private ArrayAdapter<CharSequence> spinnerFiscaIPFormMunicipioAdapter;
    private ArrayAdapter<CharSequence> spinnerFiscaIPFormTipoLuminariaAdapter;

    private RadioGroup radioGroupFiscaIPFormSinalizacaoRisco;
    private RadioGroup radioGroupFiscaIPFormAcesa24;
    private RadioGroup radioGroupFiscaIPFormAreaUrbanaRural;
    private RadioGroup radioGroupFiscaIPFormQuebrada;

    private FloatingActionButton floatingActionButtonFiscaIPFormGPS;
    private ProgressBar progressBarFiscaIPFormPS;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean get_gps;
    private TextView textViewFiscaIPFormHorario;


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.fisca_ip_activity_form);
        setTitle("Cadastramento Iluminação Pública");

        Intent intent = getIntent();
        fisca = (fiscaIPModel) intent.getSerializableExtra("fisca");
        formType = (String) intent.getSerializableExtra("type");

        setFields();

        if(formType.equals("readOnly")){
            fillForm(fisca);
            disableAll();
            floatingActionButtonFiscaIPFormGPS.setVisibility(View.GONE);
        }
        if(formType.equals("edit")){
            fillForm(fisca);
        }
        if(formType.equals("edit")||formType.equals("new")){
            floatingActionButtonFiscaIPFormGPS.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(formType.equals("readOnly")){
            inflater.inflate(R.menu.menu_edit_form, menu);
        }else{
            inflater.inflate(R.menu.menu_save_form, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menu_save_button:
                fiscaIPDAO dao = new fiscaIPDAO(this);
                if(validadeFields()){
                    fiscaIPModel fiscaToSave = getFiscaFromFields(fisca);
                    if(formType.equals("new")){
                        dao.insert(fiscaToSave);
                    }else{
                        dao.update(fiscaToSave);
                    }
                    finish();
                }
                dao.close();
                break;
            case R.id.menu_edit_button:
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent
                        .putExtra("fisca", fisca)
                        .putExtra("type", "edit");
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
                overridePendingTransition(0, 0);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == GPS_REQUEST_CODE) {
            if (permissions.length == 1 && grantResults[0]== PackageManager.PERMISSION_GRANTED){

                getGPSLocation();

            }else{
                Toast.makeText(this, "Não é possível utilizar esta função sem GPS!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fiscaIPModel fiscaSaved = getFiscaFromFields(fisca);
        outState.putSerializable("fisca", fiscaSaved);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fiscaIPModel fiscaSaved = (fiscaIPModel) savedInstanceState.getSerializable("fisca");
        fillForm(fiscaSaved);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(locationListener!=null){
            locationManager.removeUpdates(locationListener);
        }
    }



    // FUNCIONALIDADES
    private void setFields() {
        // TEXT INPUT LAYOUT
        textInputLayoutFiscaIPFormBairro = findViewById(R.id.textInputLayoutFiscaIPFormBairro);
        textInputLayoutFiscaIPFormEndereco = findViewById(R.id.textInputLayoutFiscaIPFormEndereco);
        textInputLayoutFiscaIPFormPotencia = findViewById(R.id.textInputLayoutFiscaIPFormPotencia);
        textInputLayoutFiscaIPFormObservacao = findViewById(R.id.textInputLayoutFiscaIPFormObservacao);
        textInputLayoutFiscaIPFormPlaqueta = findViewById(R.id.textInputLayoutFiscaIPFormPlaqueta);
        textInputLayoutFiscaIPFormLatitude = findViewById(R.id.textInputLayoutFiscaIPFormLatitude);
        textInputLayoutFiscaIPFormLongitude = findViewById(R.id.textInputLayoutFiscaIPFormLongitude);


        // GERAL
        textViewFiscaIPFormHorario = findViewById(R.id.textViewFiscaIPFormHorario);

        progressBarFiscaIPFormPS = findViewById(R.id.progressBarFiscaIPFormPS);
        progressBarFiscaIPFormPS.setVisibility(View.GONE);

        floatingActionButtonFiscaIPFormGPS = findViewById(R.id.floatingActionButtonFiscaIPFormGPS);
        get_gps = false;
        floatingActionButtonFiscaIPFormGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!get_gps){
                    getGPSLocation();
                    progressBarFiscaIPFormPS.setVisibility(View.VISIBLE);
                    floatingActionButtonFiscaIPFormGPS.setImageResource(R.drawable.ic_gps_edit);
                    floatingActionButtonFiscaIPFormGPS.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFAAAAAA")));

                    disableSimpleEditText(textInputLayoutFiscaIPFormLatitude.getEditText());
                    disableSimpleEditText(textInputLayoutFiscaIPFormLongitude.getEditText());
                    disableSimpleEditText(textInputLayoutFiscaIPFormEndereco.getEditText());
                    disableSimpleEditText(textInputLayoutFiscaIPFormBairro.getEditText());

                    get_gps = true;
                }else{
                    enableEditText(textInputLayoutFiscaIPFormLatitude.getEditText());
                    enableEditText(textInputLayoutFiscaIPFormLongitude.getEditText());
                    enableEditText(textInputLayoutFiscaIPFormEndereco.getEditText());
                    enableEditText(textInputLayoutFiscaIPFormBairro.getEditText());
                    if(locationListener!=null){
                        locationManager.removeUpdates(locationListener);
                    }
                    progressBarFiscaIPFormPS.setVisibility(View.GONE);
                    if(textInputLayoutFiscaIPFormLatitude.getEditText().getText().toString().equals("") || textInputLayoutFiscaIPFormLongitude.getEditText().getText().toString().equals("")){
                        floatingActionButtonFiscaIPFormGPS.setImageResource(R.drawable.ic_gps_add);
                        floatingActionButtonFiscaIPFormGPS.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5db565")));
                    }else{
                        floatingActionButtonFiscaIPFormGPS.setImageResource(R.drawable.ic_gps_edit);
                        floatingActionButtonFiscaIPFormGPS.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFAAAAAA")));
                    }
                    get_gps = false;
                }
            }
        });


        // SPINNER
        spinnerFiscaIPFormFuncionario = findViewById(R.id.spinnerFiscaIPFormFuncionario);
        spinnerFiscaIPFormFuncionarioAdapter = ArrayAdapter.createFromResource(this, R.array.funcionarios, android.R.layout.simple_spinner_item);
        spinnerFiscaIPFormFuncionarioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiscaIPFormFuncionario.setAdapter(spinnerFiscaIPFormFuncionarioAdapter);

        spinnerFiscaIPFormMunicipio = findViewById(R.id.spinnerFiscaIPFormMunicipio);
        spinnerFiscaIPFormMunicipioAdapter = ArrayAdapter.createFromResource(this, R.array.municipios, android.R.layout.simple_spinner_item);
        spinnerFiscaIPFormMunicipioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiscaIPFormMunicipio.setAdapter(spinnerFiscaIPFormMunicipioAdapter);

        spinnerFiscaIPFormTipoLuminaria = findViewById(R.id.spinnerFiscaIPFormTipoLuminaria);
        spinnerFiscaIPFormTipoLuminariaAdapter = ArrayAdapter.createFromResource(this, R.array.luminarias, android.R.layout.simple_spinner_item);
        spinnerFiscaIPFormTipoLuminariaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiscaIPFormTipoLuminaria.setAdapter(spinnerFiscaIPFormTipoLuminariaAdapter);



        // RADIO GROUP
        radioGroupFiscaIPFormSinalizacaoRisco = findViewById(R.id.radioGroupFiscaIPFormSinalizacaoRisco);
        radioGroupFiscaIPFormAcesa24 = findViewById(R.id.radioGroupFiscaIPFormAcesa24);
        radioGroupFiscaIPFormAreaUrbanaRural = findViewById(R.id.radioGroupFiscaIPFormAreaUrbanaRural);
        radioGroupFiscaIPFormQuebrada = findViewById(R.id.radioGroupFiscaIPFormQuebrada);



        // VALIDATE
        setValidateEmpty(textInputLayoutFiscaIPFormBairro);
        setValidateEmpty(textInputLayoutFiscaIPFormEndereco);
        setValidateEmpty(textInputLayoutFiscaIPFormPotencia);
    }

    private void fillForm(fiscaIPModel fisca) {
        textInputLayoutFiscaIPFormBairro.getEditText().setText(fisca.getBairro());
        textInputLayoutFiscaIPFormEndereco.getEditText().setText(fisca.getEndereco());
        textInputLayoutFiscaIPFormPotencia.getEditText().setText(fisca.getPotencia());
        textInputLayoutFiscaIPFormObservacao.getEditText().setText(fisca.getObservacao());
        textInputLayoutFiscaIPFormPlaqueta.getEditText().setText(fisca.getPlaqueta_prefeitura());
        textInputLayoutFiscaIPFormLatitude.getEditText().setText(fisca.getLatitude());
        textInputLayoutFiscaIPFormLongitude.getEditText().setText(fisca.getLongitude());

        spinnerFiscaIPFormFuncionario.setSelection(spinnerFiscaIPFormFuncionarioAdapter.getPosition(fisca.getFuncionario()));
        spinnerFiscaIPFormMunicipio.setSelection(spinnerFiscaIPFormMunicipioAdapter.getPosition(fisca.getMunicipio()));
        spinnerFiscaIPFormTipoLuminaria.setSelection(spinnerFiscaIPFormTipoLuminariaAdapter.getPosition(fisca.getTipo_luminaria()));

        textViewFiscaIPFormHorario.setText("Horário: "+fisca.getHorario());

        for(int i=0; i<radioGroupFiscaIPFormSinalizacaoRisco.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaIPFormSinalizacaoRisco.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getArea_risco())) {
                btn.setChecked(true);
                break;
            }
        }
        for(int i=0; i<radioGroupFiscaIPFormAcesa24.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaIPFormAcesa24.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getAcesa_24h())) {
                btn.setChecked(true);
                break;
            }
        }
        for(int i=0; i<radioGroupFiscaIPFormAreaUrbanaRural.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaIPFormAreaUrbanaRural.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getArea())) {
                btn.setChecked(true);
                break;
            }
        }
        for(int i=0; i<radioGroupFiscaIPFormQuebrada.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaIPFormQuebrada.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getQuebrada())) {
                btn.setChecked(true);
                break;
            }
        }


    }

    private fiscaIPModel getFiscaFromFields(fiscaIPModel fisca) {
        @SuppressLint("SimpleDateFormat") String dateFormat_hora = new SimpleDateFormat("HH:mm:ss").format(new Date());
        fisca.setHorario(dateFormat_hora);

        fisca.setBairro(textInputLayoutFiscaIPFormBairro.getEditText().getText().toString());
        fisca.setEndereco(textInputLayoutFiscaIPFormEndereco.getEditText().getText().toString());
        fisca.setPotencia(textInputLayoutFiscaIPFormPotencia.getEditText().getText().toString());
        fisca.setObservacao(textInputLayoutFiscaIPFormObservacao.getEditText().getText().toString());
        fisca.setPlaqueta_prefeitura(textInputLayoutFiscaIPFormPlaqueta.getEditText().getText().toString());
        fisca.setLatitude(textInputLayoutFiscaIPFormLatitude.getEditText().getText().toString());
        fisca.setLongitude(textInputLayoutFiscaIPFormLongitude.getEditText().getText().toString());

        fisca.setFuncionario(spinnerFiscaIPFormFuncionario.getSelectedItem().toString());
        fisca.setMunicipio(spinnerFiscaIPFormMunicipio.getSelectedItem().toString());
        fisca.setTipo_luminaria(spinnerFiscaIPFormTipoLuminaria.getSelectedItem().toString());
//        if(spinnerFiscaIPFormTipoLuminaria.getSelectedItem().toString().equals("Selecione")){
//            fisca.setFrente_trabalho("");
//        }else{
//            fisca.setFrente_trabalho(spinnerFiscaFrente.getSelectedItem().toString());
//        }

        if(radioGroupFiscaIPFormSinalizacaoRisco.getCheckedRadioButtonId() != -1) {fisca.setArea_risco(((RadioButton)findViewById(radioGroupFiscaIPFormSinalizacaoRisco.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setArea_risco("");}
        if(radioGroupFiscaIPFormAcesa24.getCheckedRadioButtonId() != -1) {fisca.setAcesa_24h(((RadioButton) findViewById(radioGroupFiscaIPFormAcesa24.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setAcesa_24h("");}
        if(radioGroupFiscaIPFormAreaUrbanaRural.getCheckedRadioButtonId() != -1) {fisca.setArea(((RadioButton)findViewById(radioGroupFiscaIPFormAreaUrbanaRural.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setArea("");}
        if(radioGroupFiscaIPFormQuebrada.getCheckedRadioButtonId() != -1) {fisca.setQuebrada(((RadioButton)findViewById(radioGroupFiscaIPFormQuebrada.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setQuebrada("");}


        return fisca;
    }



    // VALIDAÇÃO
    private boolean validadeFields() {
        String isValid = "";

        if(fieldIsEmpty(textInputLayoutFiscaIPFormBairro)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaIPFormEndereco)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaIPFormPotencia)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaIPFormLatitude)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaIPFormLongitude)){isValid = "false";}

        if(radioGroupFiscaIPFormSinalizacaoRisco.getCheckedRadioButtonId() == -1) {
            isValid = "AreaRisco";
        }
        if(radioGroupFiscaIPFormAcesa24.getCheckedRadioButtonId() == -1) {
            isValid = "Acesa24";
        }
        if(radioGroupFiscaIPFormAreaUrbanaRural.getCheckedRadioButtonId() == -1) {
            isValid = "AreaUrbanaRural";
        }
        if(radioGroupFiscaIPFormQuebrada.getCheckedRadioButtonId() == -1) {
            isValid = "Quebrada";
        }

        if(spinnerFiscaIPFormFuncionario.getSelectedItemPosition() == 0) {
            isValid = "funcionario";
        }
        if(spinnerFiscaIPFormMunicipio.getSelectedItemPosition() == 0) {
            isValid = "municipio";
        }
        if(spinnerFiscaIPFormTipoLuminaria.getSelectedItemPosition() == 0) {
            isValid = "luminaria";
        }


        switch(isValid){
            case "false":
                Toast.makeText(this, "Favor preencher todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
                break;
            case "funcionario":
                Toast.makeText(this, "Favor selecionar um funcionário!", Toast.LENGTH_SHORT).show();
                break;
            case "municipio":
                Toast.makeText(this, "Favor selecionar um município!", Toast.LENGTH_SHORT).show();
                break;
            case "luminaria":
                Toast.makeText(this, "Favor selecionar um tipo de luminária!", Toast.LENGTH_SHORT).show();
                break;
            case "AreaRisco":
                Toast.makeText(this, "Favor selecionar a opção Sinalização de Área de Risco!", Toast.LENGTH_SHORT).show();
                break;
            case "Acesa24":
                Toast.makeText(this, "Favor selecionar a opção Acesa 24h!", Toast.LENGTH_SHORT).show();
                break;
            case "AreaUrbanaRural":
                Toast.makeText(this, "Favor selecionar a opção Área Urbana/Rural!", Toast.LENGTH_SHORT).show();
                break;
            case "Quebrada":
                Toast.makeText(this, "Favor selecionar a opção Lâmpada/luminária quebrada!", Toast.LENGTH_SHORT).show();
                break;
        }

        return isValid.isEmpty();
    }

    private boolean fieldIsEmpty(TextInputLayout textInputCampo) {
        EditText campo = textInputCampo.getEditText();
        assert campo != null;
        String text = campo.getText().toString();
        if(text.isEmpty()) {
            textInputCampo.setError("Campo obrigatório!");
        }else{
            textInputCampo.setError(null);
            textInputCampo.setErrorEnabled(false);
        }
        return text.isEmpty();
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



    // GPS
    private void getGPSLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_REQUEST_CODE);

        }else{
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            if (locationManager != null) {
                Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocationGPS != null) {
                    updateLocation(lastKnownLocationGPS);
                }
            }

            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    if(location != null){

                        updateLocation(location);

                        progressBarFiscaIPFormPS.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                    progressBarFiscaIPFormPS.setVisibility(View.VISIBLE);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    if (provider.equals(LocationManager.GPS_PROVIDER)) {
                        progressBarFiscaIPFormPS.setVisibility(View.GONE);
                        Toast.makeText(fiscaIPFormActivity.this, "Favor, habilitar o GPS!", Toast.LENGTH_LONG).show();
                        Intent startGPSIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        fiscaIPFormActivity.this.startActivity(startGPSIntent);
                    }
                }
            };

            assert locationManager != null;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        }
    }

    private void updateLocation(Location location) {
        final double[] longitude = {0};
        final double[] latitude = {0};

        latitude[0] = location.getLatitude();
        longitude[0] = location.getLongitude();

        textInputLayoutFiscaIPFormLatitude.getEditText().setText(String.valueOf(latitude[0]));
        textInputLayoutFiscaIPFormLongitude.getEditText().setText(String.valueOf(longitude[0]));

        getAddressFromLocation(location, fiscaIPFormActivity.this, new GeocoderHandler());
    }



    // GEOCODER
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String result;
            String bairro = "";
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    result = bundle.getString("address");
                    bairro = bundle.getString("bairro");
                    break;
                default:
                    result = null;
            }
            // replace by what you need to do
            textInputLayoutFiscaIPFormBairro.getEditText().setText(bairro);
            textInputLayoutFiscaIPFormEndereco.getEditText().setText(result);
        }
    }



    // HELPERS
    private void disableAll() {
        disableEditText(textInputLayoutFiscaIPFormBairro.getEditText());
        disableEditText(textInputLayoutFiscaIPFormEndereco.getEditText());
        disableEditText(textInputLayoutFiscaIPFormPotencia.getEditText());
        disableEditText(textInputLayoutFiscaIPFormObservacao.getEditText());
        disableEditText(textInputLayoutFiscaIPFormPlaqueta.getEditText());
        disableEditText(textInputLayoutFiscaIPFormLatitude.getEditText());
        disableEditText(textInputLayoutFiscaIPFormLongitude.getEditText());

        disableSpinner(spinnerFiscaIPFormFuncionario);
        disableSpinner(spinnerFiscaIPFormMunicipio);
        disableSpinner(spinnerFiscaIPFormTipoLuminaria);

        disableRadioGroup(radioGroupFiscaIPFormSinalizacaoRisco);
        disableRadioGroup(radioGroupFiscaIPFormAcesa24);
        disableRadioGroup(radioGroupFiscaIPFormAreaUrbanaRural);
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setTextColor(Color.parseColor("#616161"));
    }
    private void disableSpinner(Spinner spinner) {
        spinner.setEnabled(false);
        spinner.setClickable(false);
    }
    private void disableRadioGroup(RadioGroup radioGroup) {
        for(int i=0; i<radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setClickable(false);
        }
    }

    private void disableSimpleEditText(EditText editText) {
        editText.setEnabled(false);
    }
    private void enableEditText(EditText editText) {
        editText.setEnabled(true);
    }

}
