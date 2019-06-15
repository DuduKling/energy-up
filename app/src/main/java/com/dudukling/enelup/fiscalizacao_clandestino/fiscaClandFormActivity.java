package com.dudukling.enelup.fiscalizacao_clandestino;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.enelup.R;
import com.dudukling.enelup.dao.fiscaClandDAO;
import com.dudukling.enelup.model.fiscaClandModel;

import java.io.File;
import java.util.List;
import java.util.Objects;

import br.com.sapereaude.maskedEditText.MaskedEditText;

public class fiscaClandFormActivity extends AppCompatActivity {
    private static final String REQUIRED_FIELD_ERROR_MSG = "Campo obrigatório!";
    private static final int GPS_REQUEST_CODE = 999;

    private fiscaClandModel fisca;
    private String formType;

    private TextInputLayout textInputLayoutFiscaNome;
    private TextInputLayout textInputLayoutFiscaEndereco;
    private TextInputLayout textInputLayoutFiscaBairro;
    private TextInputLayout textInputLayoutFiscaCPF;
    private TextInputLayout textInputLayoutFiscaNIS;
    private TextInputLayout textInputLayoutFiscaRG;
    private TextInputLayout textInputLayoutFiscaNascimento;
    private TextInputLayout textInputLayoutFiscaMedidor1;
    private TextInputLayout textInputLayoutFiscaMedidor2;
    private TextInputLayout textInputLayoutFiscaTelefone;
    private TextInputLayout textInputLayoutFiscaCelular;
    private TextInputLayout textInputLayoutFiscaEmail;
    private TextInputLayout textInputLayoutFiscaLatitude;
    private TextInputLayout textInputLayoutFiscaLongitude;
    private TextInputLayout textInputLayoutCPFIndicado;
    private TextInputLayout textInputLayoutFiscaOrdem;
    private TextInputLayout textInputLayoutFiscaCNPJ;

    private Spinner spinnerFiscaFuncionario;
    private Spinner spinnerFiscaMunicipio;
    private Spinner spinnerFiscaFrente;
    private ArrayAdapter<CharSequence> spinnerFiscaFuncionarioAdapter;
    private ArrayAdapter<CharSequence> spinnerFiscaMunicipioAdapter;
    private ArrayAdapter<CharSequence> spinnerFiscaFrenteAdapter;

    private MaskedEditText maskedEditTextFiscaNascimento;
    private MaskedEditText maskedEditTextFiscaTelefone;
    private MaskedEditText maskedEditTextFiscaCelular;

    private RadioGroup radioGroupFiscaCpfConsultado;
    private RadioGroup radioGroupFiscaAreaPreserva;
    private RadioGroup radioGroupFiscaInvadida;
    private RadioGroup radioGroupFiscaLigacao;
    private RadioGroup radioGroupFiscaRede;
    private RadioGroup radioGroupFiscaPadrao;
    private RadioGroup radioGroupFiscaServidao;
    private RadioGroup radioGroupFiscaIndicacao;
    private RadioGroup radioGroupFiscaOrdemServico;
    private RadioGroup radioGroupFiscaEstadoOrdem;
    private RadioGroup radioGroupFiscaServicoDirecionado;
    private RadioGroup radioGroupFiscaCPFouCNPJ;
    private RadioGroup radioGroupFiscaEnergiaEmprestada;

    private FloatingActionButton floatingActionButtonFiscaGPS;
    private ProgressBar progressBarFiscaGPS;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean get_gps;

    private FloatingActionButton floatingActionButtonFiscaAlbum;
    private Button buttonFiscaConsultaCPF;


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.fisca_cland_activity_form);
        setTitle("Cadastramento Clandestino");

        Intent intent = getIntent();
        fisca = (fiscaClandModel) intent.getSerializableExtra("fisca");
        formType = (String) intent.getSerializableExtra("type");

        setFields();

        if(formType.equals("readOnly")){
            fillForm(fisca);
            disableAll();
            floatingActionButtonFiscaGPS.setVisibility(View.GONE);
        }
        if(formType.equals("edit")){
            fillForm(fisca);
        }
        if(formType.equals("edit")||formType.equals("new")){
            buttonFiscaConsultaCPF.setVisibility(View.VISIBLE);
            floatingActionButtonFiscaGPS.setVisibility(View.VISIBLE);
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
                fiscaClandDAO dao = new fiscaClandDAO(this);
                if(validadeFields()){
                    fiscaClandModel fiscaToSave = getFiscaFromFields(fisca);
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
            if (permissions.length == 1 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

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
        fiscaClandModel fiscaSaved = getFiscaFromFields(fisca);
        outState.putSerializable("fisca", fiscaSaved);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fiscaClandModel fiscaSaved = (fiscaClandModel) savedInstanceState.getSerializable("fisca");
        fillForm(fiscaSaved);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(locationListener!=null){
            locationManager.removeUpdates(locationListener);
        }

        if(formType.equals("new")){
            fiscaClandDAO dao = new fiscaClandDAO(fiscaClandFormActivity.this);
            int fiscaID = dao.getNextID();
            fisca.setImagesList(dao.getImagesDB(fiscaID));
            deleteImagesFromPhoneMemory(fisca);
            dao.deleteImages(fiscaID);
            dao.close();
        }
    }


    // FUNCIONALIDADES
    private void setFields() {

        // GERAL
        progressBarFiscaGPS = findViewById(R.id.progressBarFiscaGPS);
        progressBarFiscaGPS.setVisibility(View.GONE);

        floatingActionButtonFiscaGPS = findViewById(R.id.floatingActionButtonFiscaGPS);
        get_gps = false;
        floatingActionButtonFiscaGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!get_gps){
                    getGPSLocation();
                    progressBarFiscaGPS.setVisibility(View.VISIBLE);
                    disableSimpleEditText(textInputLayoutFiscaLatitude.getEditText());
                    disableSimpleEditText(textInputLayoutFiscaLongitude.getEditText());
                    floatingActionButtonFiscaGPS.setImageResource(R.drawable.ic_gps_edit);
                    floatingActionButtonFiscaGPS.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFAAAAAA")));
                    get_gps = true;
                }else{
                    enableEditText(textInputLayoutFiscaLatitude.getEditText());
                    enableEditText(textInputLayoutFiscaLongitude.getEditText());
                    if(locationListener!=null){
                        locationManager.removeUpdates(locationListener);
                    }
                    progressBarFiscaGPS.setVisibility(View.GONE);
                    if(textInputLayoutFiscaLatitude.getEditText().getText().toString().equals("") || textInputLayoutFiscaLongitude.getEditText().getText().toString().equals("")){
                        floatingActionButtonFiscaGPS.setImageResource(R.drawable.ic_gps_add);
                        floatingActionButtonFiscaGPS.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5db565")));
                    }else{
                        floatingActionButtonFiscaGPS.setImageResource(R.drawable.ic_gps_edit);
                        floatingActionButtonFiscaGPS.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFAAAAAA")));
                    }
                    get_gps = false;
                }
            }
        });

        floatingActionButtonFiscaAlbum = findViewById(R.id.floatingActionButtonFiscaAlbum);
        floatingActionButtonFiscaAlbum.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent goToAlbum = new Intent(fiscaClandFormActivity.this, fiscaClandAlbumActivity.class);

                fiscaClandModel fiscaAlbum = new fiscaClandModel();
                if(formType.equals("new")){
                    fiscaClandDAO dao = new fiscaClandDAO(fiscaClandFormActivity.this);
                    int newID = dao.getNextID();
                    dao.close();
                    fiscaAlbum.setId(newID);
                    goToAlbum.putExtra("fisca", fiscaAlbum);
                }else{
                    goToAlbum.putExtra("fisca", fisca);
                }

                startActivity(goToAlbum);
            }
        });

        buttonFiscaConsultaCPF = findViewById(R.id.buttonFiscaConsultaCPF);
        buttonFiscaConsultaCPF.setVisibility(View.GONE);
        buttonFiscaConsultaCPF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CPF = textInputLayoutFiscaCPF.getEditText().getText().toString();

                if(!CPF.isEmpty()){
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("cpf copiado", CPF);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(fiscaClandFormActivity.this, "CPF "+CPF+" copiado para a área de transferência!", Toast.LENGTH_SHORT).show();

                    String url = "https://www.situacaocadastral.com.br";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }else{
                    Toast.makeText(getBaseContext(), "Nenhum CPF informado..", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // TEXT INPUT LAYOUT
        textInputLayoutFiscaNome = findViewById(R.id.textInputLayoutFiscaNome);
        textInputLayoutFiscaEndereco = findViewById(R.id.textInputLayoutFiscaEndereco);
        textInputLayoutFiscaBairro = findViewById(R.id.textInputLayoutFiscaBairro);
        textInputLayoutFiscaCPF = findViewById(R.id.textInputLayoutFiscaCPF);
        textInputLayoutFiscaNIS = findViewById(R.id.textInputLayoutFiscaNIS);
        textInputLayoutFiscaRG = findViewById(R.id.textInputLayoutFiscaRG);
        textInputLayoutFiscaNascimento = findViewById(R.id.textInputLayoutFiscaNascimento);
        textInputLayoutFiscaMedidor1 = findViewById(R.id.textInputLayoutFiscaMedidor1);
        textInputLayoutFiscaMedidor2 = findViewById(R.id.textInputLayoutFiscaMedidor2);
        textInputLayoutFiscaTelefone = findViewById(R.id.textInputLayoutFiscaTelefone);
        textInputLayoutFiscaEmail = findViewById(R.id.textInputLayoutFiscaEmail);
        textInputLayoutFiscaLatitude = findViewById(R.id.textInputLayoutFiscaLatitude);
        textInputLayoutFiscaLongitude = findViewById(R.id.textInputLayoutFiscaLongitude);
        textInputLayoutFiscaCelular = findViewById(R.id.textInputLayoutFiscaCelular);
        textInputLayoutCPFIndicado = findViewById(R.id.textInputLayoutCPFIndicado);
        textInputLayoutFiscaOrdem = findViewById(R.id.textInputLayoutFiscaOrdem);
        textInputLayoutFiscaCNPJ = findViewById(R.id.textInputLayoutFiscaCNPJ);

        textInputLayoutCPFIndicado.setVisibility(View.GONE);
        textInputLayoutFiscaOrdem.setVisibility(View.GONE);
        textInputLayoutFiscaCPF.setVisibility(View.GONE);
        textInputLayoutFiscaCNPJ.setVisibility(View.GONE);



        // SPINNER
        spinnerFiscaFuncionario = findViewById(R.id.spinnerFiscaFuncionario);
        spinnerFiscaFuncionarioAdapter = ArrayAdapter.createFromResource(this, R.array.funcionarios, android.R.layout.simple_spinner_item);
        spinnerFiscaFuncionarioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiscaFuncionario.setAdapter(spinnerFiscaFuncionarioAdapter);

        spinnerFiscaMunicipio = findViewById(R.id.spinnerFiscaMunicipio);
        spinnerFiscaMunicipioAdapter = ArrayAdapter.createFromResource(this, R.array.municipios, android.R.layout.simple_spinner_item);
        spinnerFiscaMunicipioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiscaMunicipio.setAdapter(spinnerFiscaMunicipioAdapter);

        spinnerFiscaFrente = findViewById(R.id.spinnerFiscaFrente);
        spinnerFiscaFrenteAdapter = ArrayAdapter.createFromResource(this, R.array.frentes, android.R.layout.simple_spinner_item);
        spinnerFiscaFrenteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiscaFrente.setAdapter(spinnerFiscaFrenteAdapter);
        spinnerFiscaFrente.setVisibility(View.GONE);



        // RADIO GROUP
        radioGroupFiscaCpfConsultado = findViewById(R.id.radioGroupFiscaCpfConsultado);
        radioGroupFiscaAreaPreserva = findViewById(R.id.radioGroupFiscaAreaPreserva);
        radioGroupFiscaInvadida = findViewById(R.id.radioGroupFiscaInvadida);
        radioGroupFiscaLigacao = findViewById(R.id.radioGroupFiscaLigacao);
        radioGroupFiscaRede = findViewById(R.id.radioGroupFiscaRede);
        radioGroupFiscaPadrao = findViewById(R.id.radioGroupFiscaPadrao);
        radioGroupFiscaServidao = findViewById(R.id.radioGroupFiscaServidao);
        radioGroupFiscaEnergiaEmprestada = findViewById(R.id.radioGroupFiscaEnergiaEmprestada);

        radioGroupFiscaEstadoOrdem = findViewById(R.id.radioGroupFiscaEstadoOrdem);
        radioGroupFiscaEstadoOrdem.setVisibility(View.GONE);

        radioGroupFiscaIndicacao = findViewById(R.id.radioGroupFiscaIndicacao);
        radioGroupFiscaIndicacao.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked && checkedRadioButton.getText().toString().equals("Sim")){
                    textInputLayoutCPFIndicado.setVisibility(View.VISIBLE);
                    setValidateEmpty(textInputLayoutCPFIndicado);
                }else{
                    textInputLayoutCPFIndicado.setVisibility(View.GONE);
                }
            }
        });

        final TextView textViewFiscaEstadoOrdem = findViewById(R.id.textViewFiscaEstadoOrdem);
        textViewFiscaEstadoOrdem.setVisibility(View.GONE);
        radioGroupFiscaOrdemServico = findViewById(R.id.radioGroupFiscaOrdemServico);
        radioGroupFiscaOrdemServico.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked && checkedRadioButton.getText().toString().equals("Sim")){
                    textInputLayoutFiscaOrdem.setVisibility(View.VISIBLE);
                    textViewFiscaEstadoOrdem.setVisibility(View.VISIBLE);
                    radioGroupFiscaEstadoOrdem.setVisibility(View.VISIBLE);
                    setValidateEmpty(textInputLayoutFiscaOrdem);
                }else{
                    textInputLayoutFiscaOrdem.setVisibility(View.GONE);
                    textViewFiscaEstadoOrdem.setVisibility(View.GONE);
                    radioGroupFiscaEstadoOrdem.setVisibility(View.GONE);
                }
            }
        });

        final TextView textViewFrente = findViewById(R.id.textView61);
        textViewFrente.setVisibility(View.GONE);
        radioGroupFiscaServicoDirecionado = findViewById(R.id.radioGroupFiscaServicoDirecionado);
        radioGroupFiscaServicoDirecionado.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked && checkedRadioButton.getText().toString().equals("Sim")){
                    spinnerFiscaFrente.setVisibility(View.VISIBLE);
                    textViewFrente.setVisibility(View.VISIBLE);
                }else{
                    spinnerFiscaFrente.setVisibility(View.GONE);
                    spinnerFiscaFrente.setSelection(0);
                    textViewFrente.setVisibility(View.GONE);
                }
            }
        });

        final TextView textViewCPF = findViewById(R.id.textView29);
        textViewCPF.setVisibility(View.GONE);
        radioGroupFiscaCpfConsultado.setVisibility(View.GONE);
        radioGroupFiscaCPFouCNPJ = findViewById(R.id.radioGroupFiscaCPFouCNPJ);
        radioGroupFiscaCPFouCNPJ.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked && checkedRadioButton.getText().toString().equals("CPF")){
                    textInputLayoutFiscaCNPJ.setVisibility(View.GONE);
                    textInputLayoutFiscaCPF.setVisibility(View.VISIBLE);
                    buttonFiscaConsultaCPF.setVisibility(View.VISIBLE);
                    textViewCPF.setVisibility(View.VISIBLE);
                    radioGroupFiscaCpfConsultado.setVisibility(View.VISIBLE);
                }else{
                    textInputLayoutFiscaCNPJ.setVisibility(View.VISIBLE);
                    textInputLayoutFiscaCPF.setVisibility(View.GONE);
                    buttonFiscaConsultaCPF.setVisibility(View.GONE);
                    textViewCPF.setVisibility(View.GONE);
                    radioGroupFiscaCpfConsultado.setVisibility(View.GONE);
                }
            }
        });





        // MASKED EDIT TEXT
        maskedEditTextFiscaNascimento = this.findViewById(R.id.maskedEditTextFiscaNascimento);
        maskedEditTextFiscaNascimento.setKeepHint(false);
        maskedEditTextFiscaNascimento.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    maskedEditTextFiscaNascimento.setKeepHint(true);
                    maskedEditTextFiscaNascimento.setHint("XXXX");
                    maskedEditTextFiscaNascimento.setMask("##/##/####");
                }else{
                    maskedEditTextFiscaNascimento.setKeepHint(false);
                    maskedEditTextFiscaNascimento.setHint(null);
                }
            }
        });

        maskedEditTextFiscaTelefone = this.findViewById(R.id.maskedEditTextFiscaTelefone);
        maskedEditTextFiscaTelefone.setKeepHint(false);

        maskedEditTextFiscaCelular = this.findViewById(R.id.maskedEditTextFiscaCelular);
        maskedEditTextFiscaCelular.setKeepHint(false);



        // VALIDATE
        setValidateEmpty(textInputLayoutFiscaNome);
        setValidateEmpty(textInputLayoutFiscaEndereco);
        setValidateEmpty(textInputLayoutFiscaBairro);
        setValidateEmpty(textInputLayoutFiscaCPF);
        setValidateEmpty(textInputLayoutFiscaRG);
        setValidateEmptyMasked(maskedEditTextFiscaNascimento);
        setValidateEmpty(textInputLayoutFiscaMedidor1);
        setValidateEmpty(textInputLayoutFiscaLatitude);
        setValidateEmpty(textInputLayoutFiscaLongitude);
        setValidateEmpty(textInputLayoutFiscaCNPJ);

    }

    private void fillForm(fiscaClandModel fisca) {
        textInputLayoutFiscaNome.getEditText().setText(fisca.getNome());
        textInputLayoutFiscaEndereco.getEditText().setText(fisca.getEndereco());
        textInputLayoutFiscaBairro.getEditText().setText(fisca.getBairro());
        textInputLayoutFiscaCPF.getEditText().setText(fisca.getCpf());
        textInputLayoutFiscaNIS.getEditText().setText(fisca.getNis());
        textInputLayoutFiscaRG.getEditText().setText(fisca.getRg());
        textInputLayoutFiscaMedidor1.getEditText().setText(fisca.getMedidor_vizinho_1());
        textInputLayoutFiscaMedidor2.getEditText().setText(fisca.getMedidor_vizinho_2());
        textInputLayoutFiscaEmail.getEditText().setText(fisca.getEmail());
        textInputLayoutFiscaLatitude.getEditText().setText(fisca.getLatitude());
        textInputLayoutFiscaLongitude.getEditText().setText(fisca.getLongitude());
        textInputLayoutCPFIndicado.getEditText().setText(fisca.getCpf_pre_indicacao());
        textInputLayoutFiscaOrdem.getEditText().setText(fisca.getNumero_ordem());
        textInputLayoutFiscaCNPJ.getEditText().setText(fisca.getCnpj());

        spinnerFiscaFuncionario.setSelection(spinnerFiscaFuncionarioAdapter.getPosition(fisca.getFuncionario()));
        spinnerFiscaMunicipio.setSelection(spinnerFiscaMunicipioAdapter.getPosition(fisca.getMunicipio()));
        spinnerFiscaFrente.setSelection(spinnerFiscaFrenteAdapter.getPosition(fisca.getFrente_trabalho()));

        maskedEditTextFiscaNascimento.setText(fisca.getData_nascimento());
        maskedEditTextFiscaTelefone.setText(fisca.getTelefone());
        maskedEditTextFiscaCelular.setText(fisca.getCelular());

        for(int i=0; i<radioGroupFiscaCpfConsultado.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaCpfConsultado.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getCpf_status())) {
                btn.setChecked(true);
                break;
            }
        }
        for(int i=0; i<radioGroupFiscaAreaPreserva.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaAreaPreserva.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getPreservacao_ambiental())) {
                btn.setChecked(true);
                break;
            }
        }
        for(int i=0; i<radioGroupFiscaInvadida.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaInvadida.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getArea_invadida())) {
                btn.setChecked(true);
                break;
            }
        }
        for(int i=0; i<radioGroupFiscaLigacao.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaLigacao.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getTipo_ligacao())) {
                btn.setChecked(true);
                break;
            }
        }
        for(int i=0; i<radioGroupFiscaRede.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaRede.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getRede_local())) {
                btn.setChecked(true);
                break;
            }
        }
        for(int i=0; i<radioGroupFiscaPadrao.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaPadrao.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getPadrao_montado())) {
                btn.setChecked(true);
                break;
            }
        }
        for(int i=0; i<radioGroupFiscaServidao.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaServidao.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getFaixa_servidao())) {
                btn.setChecked(true);
                break;
            }
        }
        for(int i=0; i<radioGroupFiscaIndicacao.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaIndicacao.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getPre_indicacao())) {
                btn.setChecked(true);
                break;
            }
        }
        for(int i=0; i<radioGroupFiscaOrdemServico.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaOrdemServico.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getExiste_ordem())) {
                btn.setChecked(true);
                break;
            }
        }
        for(int i=0; i<radioGroupFiscaEstadoOrdem.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaEstadoOrdem.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getEstado_ordem())) {
                btn.setChecked(true);
                break;
            }
        }
        for(int i=0; i<radioGroupFiscaServicoDirecionado.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaServicoDirecionado.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getServico_direcionado())) {
                btn.setChecked(true);
                break;
            }
        }
        for(int i=0; i<radioGroupFiscaCPFouCNPJ.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaCPFouCNPJ.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getCpf_ou_cnpj())) {
                btn.setChecked(true);
                break;
            }
        }
        for(int i=0; i<radioGroupFiscaEnergiaEmprestada.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaEnergiaEmprestada.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getEnergia_emprestada())) {
                btn.setChecked(true);
                break;
            }
        }

    }

    private fiscaClandModel getFiscaFromFields(fiscaClandModel fisca) {
        fisca.setNome(textInputLayoutFiscaNome.getEditText().getText().toString());
        fisca.setEndereco(textInputLayoutFiscaEndereco.getEditText().getText().toString());
        fisca.setBairro(textInputLayoutFiscaBairro.getEditText().getText().toString());
        fisca.setCpf(textInputLayoutFiscaCPF.getEditText().getText().toString());
        fisca.setNis(textInputLayoutFiscaNIS.getEditText().getText().toString());
        fisca.setRg(textInputLayoutFiscaRG.getEditText().getText().toString());
        fisca.setMedidor_vizinho_1(textInputLayoutFiscaMedidor1.getEditText().getText().toString());
        fisca.setMedidor_vizinho_2(textInputLayoutFiscaMedidor2.getEditText().getText().toString());
        fisca.setEmail(textInputLayoutFiscaEmail.getEditText().getText().toString());
        fisca.setLatitude(textInputLayoutFiscaLatitude.getEditText().getText().toString());
        fisca.setLongitude(textInputLayoutFiscaLongitude.getEditText().getText().toString());
        fisca.setCpf_pre_indicacao(textInputLayoutCPFIndicado.getEditText().getText().toString());
        fisca.setNumero_ordem(textInputLayoutFiscaOrdem.getEditText().getText().toString());
        fisca.setCnpj(textInputLayoutFiscaCNPJ.getEditText().getText().toString());

        fisca.setFuncionario(spinnerFiscaFuncionario.getSelectedItem().toString());
        fisca.setMunicipio(spinnerFiscaMunicipio.getSelectedItem().toString());
        if(spinnerFiscaFrente.getSelectedItem().toString().equals("Selecione")){
            fisca.setFrente_trabalho("");
        }else{
            fisca.setFrente_trabalho(spinnerFiscaFrente.getSelectedItem().toString());
        }

        fisca.setData_nascimento(maskedEditTextFiscaNascimento.getRawText());
        if(maskedEditTextFiscaTelefone.getRawText().equals("(")){fisca.setTelefone("");}
        else{fisca.setTelefone(maskedEditTextFiscaTelefone.getRawText());}
        if(maskedEditTextFiscaCelular.getRawText().equals("(")){fisca.setCelular("");}
        else{fisca.setCelular(maskedEditTextFiscaCelular.getRawText());}

        if(radioGroupFiscaCpfConsultado.getCheckedRadioButtonId() != -1) {fisca.setCpf_status(((RadioButton)findViewById(radioGroupFiscaCpfConsultado.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setCpf_status("");}
        if(radioGroupFiscaAreaPreserva.getCheckedRadioButtonId() != -1) {fisca.setPreservacao_ambiental(((RadioButton) findViewById(radioGroupFiscaAreaPreserva.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setPreservacao_ambiental("");}
        if(radioGroupFiscaInvadida.getCheckedRadioButtonId() != -1) {fisca.setArea_invadida(((RadioButton)findViewById(radioGroupFiscaInvadida.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setArea_invadida("");}
        if(radioGroupFiscaLigacao.getCheckedRadioButtonId() != -1) {fisca.setTipo_ligacao(((RadioButton)findViewById(radioGroupFiscaLigacao.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setTipo_ligacao("");}
        if(radioGroupFiscaRede.getCheckedRadioButtonId() != -1) {fisca.setRede_local(((RadioButton)findViewById(radioGroupFiscaRede.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setRede_local("");}
        if(radioGroupFiscaPadrao.getCheckedRadioButtonId() != -1) {fisca.setPadrao_montado(((RadioButton)findViewById(radioGroupFiscaPadrao.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setPadrao_montado("");}
        if(radioGroupFiscaServidao.getCheckedRadioButtonId() != -1) {fisca.setFaixa_servidao(((RadioButton)findViewById(radioGroupFiscaServidao.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setFaixa_servidao("");}
        if(radioGroupFiscaIndicacao.getCheckedRadioButtonId() != -1) {fisca.setPre_indicacao(((RadioButton)findViewById(radioGroupFiscaIndicacao.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setPre_indicacao("");}
        if(radioGroupFiscaOrdemServico.getCheckedRadioButtonId() != -1) {fisca.setExiste_ordem(((RadioButton)findViewById(radioGroupFiscaOrdemServico.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setExiste_ordem("");}
        if(radioGroupFiscaOrdemServico.getCheckedRadioButtonId() == R.id.radioButtonFiscaOrdemServico1){
            fisca.setEstado_ordem(((RadioButton)findViewById(radioGroupFiscaEstadoOrdem.getCheckedRadioButtonId())).getText().toString());
        }else{
            fisca.setNumero_ordem("");
            fisca.setEstado_ordem("");
        }
        if(radioGroupFiscaServicoDirecionado.getCheckedRadioButtonId() != -1) {fisca.setServico_direcionado(((RadioButton)findViewById(radioGroupFiscaServicoDirecionado.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setServico_direcionado("");}
        if(radioGroupFiscaCPFouCNPJ.getCheckedRadioButtonId() != -1){
            fisca.setCpf_ou_cnpj(((RadioButton)findViewById(radioGroupFiscaCPFouCNPJ.getCheckedRadioButtonId())).getText().toString());
        }
        if(radioGroupFiscaCPFouCNPJ.getCheckedRadioButtonId() == R.id.radioButtonFiscaCPFouCNPJ1){
            fisca.setCnpj("");
        }else{
            fisca.setCpf("");
            fisca.setCpf_status("");
        }
        if(radioGroupFiscaEnergiaEmprestada.getCheckedRadioButtonId() != -1) {fisca.setEnergia_emprestada(((RadioButton)findViewById(radioGroupFiscaEnergiaEmprestada.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setEnergia_emprestada("");}


        return fisca;
    }

    public void deleteImagesFromPhoneMemory(fiscaClandModel fisca) {
        List<String> imagesListToDelete = fisca.getImagesList();
        for (int i = 0; i < imagesListToDelete.size(); i++) {
            File file = new File(imagesListToDelete.get(i));
            boolean deleted = file.delete();
            Log.d("TAG4", "delete() called: " + deleted);
        }

        String path = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (File file : files) {
            boolean deleted = file.delete();
            Log.d("TAG4", "delete() called: " + deleted);
        }
    }



    // VALIDAÇÃO
    private boolean validadeFields() {
        String isValid = "";

        if(fieldIsEmpty(textInputLayoutFiscaNome)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaEndereco)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaBairro)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaRG)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaMedidor1)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaLatitude)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaLongitude)){isValid = "false";}

        if(fieldIsEmptyMasked(maskedEditTextFiscaNascimento)){isValid = "false";}

        switch(radioGroupFiscaOrdemServico.getCheckedRadioButtonId()){
            case -1:
                isValid = "Ordem";
                break;
            case R.id.radioButtonFiscaOrdemServico1:
                if(fieldIsEmpty(textInputLayoutFiscaOrdem)){isValid = "false";}
                switch(radioGroupFiscaEstadoOrdem.getCheckedRadioButtonId()){
                    case -1:
                        isValid = "EstadoOrdem";
                        break;
                }
                break;
        }
        switch(radioGroupFiscaIndicacao.getCheckedRadioButtonId()){
            case -1:
                isValid = "Indicacao";
                break;
            case R.id.radioButtonFiscaIndicacao1:
                if(fieldIsEmpty(textInputLayoutCPFIndicado)){isValid = "false";}
                break;
        }
        switch(radioGroupFiscaServicoDirecionado.getCheckedRadioButtonId()){
            case -1:
                isValid = "ServicoDirecionado";
                break;
            case R.id.radioButtonFiscaServicoDirecionado1:
                if (spinnerFiscaFrente.getSelectedItemPosition() == 0) {
                    isValid = "frente";
                }
                break;
        }
        switch(radioGroupFiscaCPFouCNPJ.getCheckedRadioButtonId()){
            case -1:
                isValid = "CPFouCNPJ";
                break;
            case R.id.radioButtonFiscaCPFouCNPJ1:
                if(radioGroupFiscaCpfConsultado.getCheckedRadioButtonId() == -1) {
                    isValid = "cpfConsultado";
                }
                if(fieldIsEmpty(textInputLayoutFiscaCPF)){isValid = "false";}
                break;
            case R.id.radioButtonFiscaCPFouCNPJ2:
                if(fieldIsEmpty(textInputLayoutFiscaCNPJ)){isValid = "false";}
                break;
        }
        if(radioGroupFiscaServidao.getCheckedRadioButtonId() == -1) {
            isValid = "Servidao";
        }
        if(radioGroupFiscaPadrao.getCheckedRadioButtonId() == -1) {
            isValid = "Padrao";
        }
        if(radioGroupFiscaRede.getCheckedRadioButtonId() == -1) {
            isValid = "Rede";
        }
        if(radioGroupFiscaLigacao.getCheckedRadioButtonId() == -1) {
            isValid = "Ligacao";
        }
        if(radioGroupFiscaInvadida.getCheckedRadioButtonId() == -1) {
            isValid = "Invadida";
        }
        if(radioGroupFiscaEnergiaEmprestada.getCheckedRadioButtonId() == -1) {
            isValid = "EnergiaEmprestada";
        }


        if(spinnerFiscaMunicipio.getSelectedItemPosition() == 0) {
            isValid = "municipio";
        }
        if(spinnerFiscaFuncionario.getSelectedItemPosition() == 0) {
            isValid = "funcionario";
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
            case "cpfConsultado":
                Toast.makeText(this, "Favor selecionar a opção CPF consultado!", Toast.LENGTH_SHORT).show();
                break;
            case "Invadida":
                Toast.makeText(this, "Favor selecionar a opção Área invadida !", Toast.LENGTH_SHORT).show();
                break;
            case "Ligacao":
                Toast.makeText(this, "Favor selecionar a opção Tipo de ligação!", Toast.LENGTH_SHORT).show();
                break;
            case "Rede":
                Toast.makeText(this, "Favor selecionar a opção Existe rede no local!", Toast.LENGTH_SHORT).show();
                break;
            case "Padrao":
                Toast.makeText(this, "Favor selecionar a opção Padrão já montado!", Toast.LENGTH_SHORT).show();
                break;
            case "Servidao":
                Toast.makeText(this, "Favor selecionar a opção Faixa de servidão!", Toast.LENGTH_SHORT).show();
                break;
            case "Indicacao":
                Toast.makeText(this, "Favor selecionar a opção Houve pré indicação!", Toast.LENGTH_SHORT).show();
                break;
            case "EstadoOrdem":
                Toast.makeText(this, "Favor selecionar a opção Estado da Ordem!", Toast.LENGTH_SHORT).show();
                break;
            case "Ordem":
                Toast.makeText(this, "Favor selecionar a opção Existe ordem de serviço para o local!", Toast.LENGTH_SHORT).show();
                break;
            case "ServicoDirecionado":
                Toast.makeText(this, "Favor selecionar a opção Serviço direcionado!", Toast.LENGTH_SHORT).show();
                break;
            case "frente":
                Toast.makeText(this, "Favor selecionar uma Frente de trabalho!", Toast.LENGTH_SHORT).show();
                break;
            case "CPFouCNPJ":
                Toast.makeText(this, "Favor selecionar CPF ou CNPJ para preenchimento!", Toast.LENGTH_SHORT).show();
                break;
            case "EnergiaEmprestada":
                Toast.makeText(this, "Favor selecionar a opção Energia Emprestada!", Toast.LENGTH_SHORT).show();
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
    private boolean fieldIsEmptyMasked(MaskedEditText maskedEditText) {
        String text = maskedEditText.getRawText();
        if(text.isEmpty()) {
            maskedEditText.setError("Campo obrigatório!");
        }else{
            maskedEditText.setError(null);
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
    private void setValidateEmptyMasked(final MaskedEditText maskedEditText) {
        maskedEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                maskedEditText.setError(null);
                if(!hasFocus){
                    String text = maskedEditText.getRawText();
                    if(text.equals("")){
                        maskedEditText.setError(REQUIRED_FIELD_ERROR_MSG);
                    }
                }else{
                    maskedEditText.setError(null);
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

            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    if(location != null){

                        final double[] longitude = {0};
                        final double[] latitude = {0};

                        latitude[0] = location.getLatitude();
                        longitude[0] = location.getLongitude();

                        textInputLayoutFiscaLatitude.getEditText().setText(String.valueOf(latitude[0]));
                        textInputLayoutFiscaLongitude.getEditText().setText(String.valueOf(longitude[0]));

                        progressBarFiscaGPS.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                    progressBarFiscaGPS.setVisibility(View.VISIBLE);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    if (provider.equals(LocationManager.GPS_PROVIDER)) {
                        progressBarFiscaGPS.setVisibility(View.GONE);
                        Toast.makeText(fiscaClandFormActivity.this, "Favor, habilitar o GPS!", Toast.LENGTH_LONG).show();
                        Intent startGPSIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        fiscaClandFormActivity.this.startActivity(startGPSIntent);
                    }
                }
            };

            assert locationManager != null;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        }
    }


    // HELPERS
    private void disableAll() {
        disableEditText(textInputLayoutFiscaNome.getEditText());
        disableEditText(textInputLayoutFiscaEndereco.getEditText());
        disableEditText(textInputLayoutFiscaBairro.getEditText());
        disableEditText(textInputLayoutFiscaCPF.getEditText());
        disableEditText(textInputLayoutFiscaNIS.getEditText());
        disableEditText(textInputLayoutFiscaRG.getEditText());
        disableEditText(textInputLayoutFiscaNascimento.getEditText());
        disableEditText(textInputLayoutFiscaMedidor1.getEditText());
        disableEditText(textInputLayoutFiscaMedidor2.getEditText());
        disableEditText(textInputLayoutFiscaTelefone.getEditText());
        disableEditText(textInputLayoutFiscaCelular.getEditText());
        disableEditText(textInputLayoutFiscaEmail.getEditText());
        disableEditText(textInputLayoutFiscaLatitude.getEditText());
        disableEditText(textInputLayoutFiscaLongitude.getEditText());
        disableEditText(textInputLayoutCPFIndicado.getEditText());
        disableEditText(textInputLayoutFiscaOrdem.getEditText());
        disableEditText(textInputLayoutFiscaCNPJ.getEditText());

        disableSpinner(spinnerFiscaMunicipio);
        disableSpinner(spinnerFiscaFuncionario);
        disableSpinner(spinnerFiscaFrente);

        disableRadioGroup(radioGroupFiscaCpfConsultado);
        disableRadioGroup(radioGroupFiscaAreaPreserva);
        disableRadioGroup(radioGroupFiscaInvadida);
        disableRadioGroup(radioGroupFiscaLigacao);
        disableRadioGroup(radioGroupFiscaRede);
        disableRadioGroup(radioGroupFiscaPadrao);
        disableRadioGroup(radioGroupFiscaServidao);
        disableRadioGroup(radioGroupFiscaIndicacao);
        disableRadioGroup(radioGroupFiscaOrdemServico);
        disableRadioGroup(radioGroupFiscaEstadoOrdem);
        disableRadioGroup(radioGroupFiscaServicoDirecionado);
        disableRadioGroup(radioGroupFiscaCPFouCNPJ);
        disableRadioGroup(radioGroupFiscaEnergiaEmprestada);
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setTextColor(Color.parseColor("#616161"));
    }
    private void disableSimpleEditText(EditText editText) {
        editText.setEnabled(false);
    }
    private void enableEditText(EditText editText) {
        editText.setEnabled(true);
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

}
