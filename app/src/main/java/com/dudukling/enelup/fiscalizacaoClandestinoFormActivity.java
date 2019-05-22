package com.dudukling.enelup;

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
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.dudukling.enelup.dao.fiscalizaDAO;
import com.dudukling.enelup.model.fiscaModel;

import java.util.Objects;

import br.com.sapereaude.maskedEditText.MaskedEditText;

public class fiscalizacaoClandestinoFormActivity extends AppCompatActivity {
    private static final String REQUIRED_FIELD_ERROR_MSG = "Campo obrigatório!";
    private static final int GPS_REQUEST_CODE = 999;

    private fiscaModel fisca;
    private String formType;

    private Spinner spinnerFiscaFuncionario;
    private TextInputLayout textInputLayoutFiscaNome;
    private TextInputLayout textInputLayoutFiscaEndereco;
    private TextInputLayout textInputLayoutFiscaBairro;
    private Spinner spinnerFiscaMunicipio;
    private TextInputLayout textInputLayoutFiscaCPF;
    private RadioGroup radioGroupFiscaCpfConsultado;
    private TextInputLayout textInputLayoutFiscaNIS;
    private TextInputLayout textInputLayoutFiscaRG;
    private TextInputLayout textInputLayoutFiscaNascimento;
    private MaskedEditText maskedEditTextFiscaNascimento;
    private TextInputLayout textInputLayoutFiscaMedidor1;
    private TextInputLayout textInputLayoutFiscaMedidor2;
    private TextInputLayout textInputLayoutFiscaTelefone;
    private MaskedEditText maskedEditTextFiscaTelefone;
    private TextInputLayout textInputLayoutFiscaCelular;
    private MaskedEditText maskedEditTextFiscaCelular;
    private TextInputLayout textInputLayoutFiscaEmail;
    private TextInputLayout textInputLayoutFiscaLatitude;
    private TextInputLayout textInputLayoutFiscaLongitude;
    private RadioGroup radioGroupFiscaAreaPreserva;
    private RadioGroup radioGroupFiscaInvadida;
    private RadioGroup radioGroupFiscaLigacao;
    private RadioGroup radioGroupFiscaRede;
    private RadioGroup radioGroupFiscaPadrao;
    private RadioGroup radioGroupFiscaServidao;
    private RadioGroup radioGroupFiscaIndicacao;
    private TextInputLayout textInputLayoutCPFIndicado;
    private RadioGroup radioGroupFiscaOrdemServico;
    private TextInputLayout textInputLayoutFiscaOrdem;
    private TextView textViewFiscaEstadoOrdem;
    private RadioGroup radioGroupFiscaEstadoOrdem;

    private ArrayAdapter<CharSequence> spinnerFiscaFuncionarioAdapter;
    private ArrayAdapter<CharSequence> spinnerFiscaMunicipioAdapter;
    private FloatingActionButton floatingActionButtonFiscaAlbum;
    private Button buttonFiscaConsultaCPF;

    private FloatingActionButton floatingActionButtonFiscaGPS;
    private ProgressBar progressBarFiscaGPS;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean get_gps;


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_fiscalizacao_clandestino_form);
        setTitle("Cadastramento Clandestino");

        Intent intent = getIntent();
        fisca = (fiscaModel) intent.getSerializableExtra("fisca");
        formType = (String) intent.getSerializableExtra("type");

        setFields();

        floatingActionButtonFiscaAlbum = findViewById(R.id.floatingActionButtonFiscaAlbum);
        floatingActionButtonFiscaAlbum.setVisibility(View.GONE);
        buttonFiscaConsultaCPF = findViewById(R.id.buttonFiscaConsultaCPF);
        buttonFiscaConsultaCPF.setVisibility(View.GONE);

        if(formType.equals("readOnly")){
            fillForm(fisca);
            disableAll();
            floatingActionButtonFiscaAlbum.setVisibility(View.VISIBLE);
            floatingActionButtonFiscaAlbum.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    Intent goToAlbum = new Intent(fiscalizacaoClandestinoFormActivity.this, fiscalizacaoClandestinoAlbumActivity.class);
                    goToAlbum.putExtra("fisca", fisca);
                    startActivity(goToAlbum);
                }
            });
            floatingActionButtonFiscaGPS.setVisibility(View.GONE);
        }
        if(formType.equals("edit")){
            fillForm(fisca);
        }
        if(formType.equals("edit")||formType.equals("new")){
            buttonFiscaConsultaCPF.setVisibility(View.VISIBLE);
            buttonFiscaConsultaCPF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String CPF = textInputLayoutFiscaCPF.getEditText().getText().toString();

                    if(!CPF.isEmpty()){
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("cpf copiado", CPF);
                        clipboard.setPrimaryClip(clip);

                        Toast.makeText(fiscalizacaoClandestinoFormActivity.this, "CPF "+CPF+" copiado para a área de transferência!", Toast.LENGTH_SHORT).show();

                        String url = "https://www.situacaocadastral.com.br";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }else{
                        Toast.makeText(getBaseContext(), "Nenhum CPF informado..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            floatingActionButtonFiscaGPS.setVisibility(View.VISIBLE);
        }
    }

    private void disableAll() {
        disableSpinner(spinnerFiscaFuncionario);
        disableEditText(textInputLayoutFiscaNome.getEditText());
        disableEditText(textInputLayoutFiscaEndereco.getEditText());
        disableEditText(textInputLayoutFiscaBairro.getEditText());
        disableSpinner(spinnerFiscaMunicipio);
        disableEditText(textInputLayoutFiscaCPF.getEditText());
        disableRadioGroup(radioGroupFiscaCpfConsultado);
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
        disableRadioGroup(radioGroupFiscaAreaPreserva);
        disableRadioGroup(radioGroupFiscaInvadida);
        disableRadioGroup(radioGroupFiscaLigacao);
        disableRadioGroup(radioGroupFiscaRede);
        disableRadioGroup(radioGroupFiscaPadrao);
        disableRadioGroup(radioGroupFiscaServidao);
        disableRadioGroup(radioGroupFiscaIndicacao);
        disableEditText(textInputLayoutCPFIndicado.getEditText());
        disableRadioGroup(radioGroupFiscaOrdemServico);
        disableEditText(textInputLayoutFiscaOrdem.getEditText());
        disableRadioGroup(radioGroupFiscaEstadoOrdem);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menu_save_button:
                fiscalizaDAO dao = new fiscalizaDAO(this);
                if(validadeFields()){
                    fiscaModel fiscaToSave = getFiscaFromFields(fisca);
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

    private fiscaModel getFiscaFromFields(fiscaModel fisca) {
        fisca.setFuncionario(spinnerFiscaFuncionario.getSelectedItem().toString());
        fisca.setNome(textInputLayoutFiscaNome.getEditText().getText().toString());
        fisca.setEndereco(textInputLayoutFiscaEndereco.getEditText().getText().toString());
        fisca.setBairro(textInputLayoutFiscaBairro.getEditText().getText().toString());
        fisca.setMunicipio(spinnerFiscaMunicipio.getSelectedItem().toString());
        fisca.setCpf(textInputLayoutFiscaCPF.getEditText().getText().toString());

        if(radioGroupFiscaCpfConsultado.getCheckedRadioButtonId() != -1) {fisca.setCpf_status(((RadioButton)findViewById(radioGroupFiscaCpfConsultado.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setCpf_status("");}

        fisca.setNis(textInputLayoutFiscaNIS.getEditText().getText().toString());
        fisca.setRg(textInputLayoutFiscaRG.getEditText().getText().toString());
//        fisca.setData_nascimento(textInputLayoutFiscaNascimento.getEditText().getText().toString());
        fisca.setData_nascimento(maskedEditTextFiscaNascimento.getRawText());
        fisca.setMedidor_vizinho_1(textInputLayoutFiscaMedidor1.getEditText().getText().toString());
        fisca.setMedidor_vizinho_2(textInputLayoutFiscaMedidor2.getEditText().getText().toString());
//        fisca.setTelefone(textInputLayoutFiscaTelefone.getEditText().getText().toString());
        if(maskedEditTextFiscaTelefone.getRawText().equals("(")){fisca.setTelefone("");}
        else{fisca.setTelefone(maskedEditTextFiscaTelefone.getRawText());}
//        fisca.setCelular(textInputLayoutFiscaCelular.getEditText().getText().toString());
        if(maskedEditTextFiscaCelular.getRawText().equals("(")){fisca.setCelular("");}
        else{fisca.setCelular(maskedEditTextFiscaCelular.getRawText());}
        fisca.setEmail(textInputLayoutFiscaEmail.getEditText().getText().toString());
        fisca.setLatitude(textInputLayoutFiscaLatitude.getEditText().getText().toString());
        fisca.setLongitude(textInputLayoutFiscaLongitude.getEditText().getText().toString());

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

        fisca.setCpf_pre_indicacao(textInputLayoutCPFIndicado.getEditText().getText().toString());

        if(radioGroupFiscaOrdemServico.getCheckedRadioButtonId() != -1) {fisca.setExiste_ordem(((RadioButton)findViewById(radioGroupFiscaOrdemServico.getCheckedRadioButtonId())).getText().toString());
        }else{fisca.setExiste_ordem("");}

        fisca.setNumero_ordem(textInputLayoutFiscaOrdem.getEditText().getText().toString());

        if(radioGroupFiscaOrdemServico.getCheckedRadioButtonId() == R.id.radioButtonFiscaOrdemServico1){fisca.setEstado_ordem(((RadioButton)findViewById(radioGroupFiscaEstadoOrdem.getCheckedRadioButtonId())).getText().toString());
        }else{
            fisca.setNumero_ordem("");
            fisca.setEstado_ordem("");
        }

        return fisca;
    }

    private void setFields() {
        spinnerFiscaFuncionario = findViewById(R.id.spinnerFiscaFuncionario);
        spinnerFiscaFuncionarioAdapter = ArrayAdapter.createFromResource(this, R.array.funcionarios, android.R.layout.simple_spinner_item);
        spinnerFiscaFuncionarioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiscaFuncionario.setAdapter(spinnerFiscaFuncionarioAdapter);

        textInputLayoutFiscaNome = findViewById(R.id.textInputLayoutFiscaNome);
        textInputLayoutFiscaEndereco = findViewById(R.id.textInputLayoutFiscaEndereco);
        textInputLayoutFiscaBairro = findViewById(R.id.textInputLayoutFiscaBairro);

        spinnerFiscaMunicipio = findViewById(R.id.spinnerFiscaMunicipio);
        spinnerFiscaMunicipioAdapter = ArrayAdapter.createFromResource(this, R.array.municipios, android.R.layout.simple_spinner_item);
        spinnerFiscaMunicipioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiscaMunicipio.setAdapter(spinnerFiscaMunicipioAdapter);

        textInputLayoutFiscaCPF = findViewById(R.id.textInputLayoutFiscaCPF);
        radioGroupFiscaCpfConsultado = findViewById(R.id.radioGroupFiscaCpfConsultado);
        textInputLayoutFiscaNIS = findViewById(R.id.textInputLayoutFiscaNIS);
        textInputLayoutFiscaRG = findViewById(R.id.textInputLayoutFiscaRG);

        textInputLayoutFiscaNascimento = findViewById(R.id.textInputLayoutFiscaNascimento);
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

        textInputLayoutFiscaMedidor1 = findViewById(R.id.textInputLayoutFiscaMedidor1);
        textInputLayoutFiscaMedidor2 = findViewById(R.id.textInputLayoutFiscaMedidor2);

        textInputLayoutFiscaTelefone = findViewById(R.id.textInputLayoutFiscaTelefone);
        maskedEditTextFiscaTelefone = this.findViewById(R.id.maskedEditTextFiscaTelefone);
        maskedEditTextFiscaTelefone.setKeepHint(false);
        maskedEditTextFiscaTelefone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    maskedEditTextFiscaTelefone.setKeepHint(true);
                    maskedEditTextFiscaTelefone.setHint("XXXX");
                    maskedEditTextFiscaTelefone.setMask("(##)####-####");
                }else{
                    maskedEditTextFiscaTelefone.setKeepHint(false);
                    maskedEditTextFiscaTelefone.setHint(null);
                }
            }
        });

        textInputLayoutFiscaCelular = findViewById(R.id.textInputLayoutFiscaCelular);
        maskedEditTextFiscaCelular = this.findViewById(R.id.maskedEditTextFiscaCelular);
        maskedEditTextFiscaCelular.setKeepHint(false);
        maskedEditTextFiscaCelular.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    maskedEditTextFiscaCelular.setKeepHint(true);
                    maskedEditTextFiscaCelular.setHint("XXXX");
                    maskedEditTextFiscaCelular.setMask("(##)#####-####");
                }else{
                    maskedEditTextFiscaCelular.setKeepHint(false);
                    maskedEditTextFiscaCelular.setHint(null);
                }
            }
        });

        textInputLayoutFiscaEmail = findViewById(R.id.textInputLayoutFiscaEmail);
        textInputLayoutFiscaLatitude = findViewById(R.id.textInputLayoutFiscaLatitude);
        textInputLayoutFiscaLongitude = findViewById(R.id.textInputLayoutFiscaLongitude);
        radioGroupFiscaAreaPreserva = findViewById(R.id.radioGroupFiscaAreaPreserva);
        radioGroupFiscaInvadida = findViewById(R.id.radioGroupFiscaInvadida);
        radioGroupFiscaLigacao = findViewById(R.id.radioGroupFiscaLigacao);
        radioGroupFiscaRede = findViewById(R.id.radioGroupFiscaRede);
        radioGroupFiscaPadrao = findViewById(R.id.radioGroupFiscaPadrao);
        radioGroupFiscaServidao = findViewById(R.id.radioGroupFiscaServidao);

        textInputLayoutCPFIndicado = findViewById(R.id.textInputLayoutCPFIndicado);
        textInputLayoutCPFIndicado.setVisibility(View.GONE);
        radioGroupFiscaIndicacao = findViewById(R.id.radioGroupFiscaIndicacao);
        radioGroupFiscaIndicacao.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
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

        textInputLayoutFiscaOrdem = findViewById(R.id.textInputLayoutFiscaOrdem);
        textInputLayoutFiscaOrdem.setVisibility(View.GONE);
        textViewFiscaEstadoOrdem = findViewById(R.id.textViewFiscaEstadoOrdem);
        textViewFiscaEstadoOrdem.setVisibility(View.GONE);
        radioGroupFiscaEstadoOrdem = findViewById(R.id.radioGroupFiscaEstadoOrdem);
        radioGroupFiscaEstadoOrdem.setVisibility(View.GONE);
        radioGroupFiscaOrdemServico = findViewById(R.id.radioGroupFiscaOrdemServico);
        radioGroupFiscaOrdemServico.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
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

        setValidateEmpty(textInputLayoutFiscaNome);
        setValidateEmpty(textInputLayoutFiscaEndereco);
        setValidateEmpty(textInputLayoutFiscaBairro);
        setValidateEmpty(textInputLayoutFiscaCPF);
        setValidateEmpty(textInputLayoutFiscaRG);

//        setValidateEmpty(textInputLayoutFiscaNascimento);
        setValidateEmptyMasked(maskedEditTextFiscaNascimento);

        setValidateEmpty(textInputLayoutFiscaMedidor1);
        setValidateEmpty(textInputLayoutFiscaLatitude);
        setValidateEmpty(textInputLayoutFiscaLongitude);

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
    }

    private void fillForm(fiscaModel fisca) {
        spinnerFiscaFuncionario.setSelection(spinnerFiscaFuncionarioAdapter.getPosition(fisca.getFuncionario()));
        textInputLayoutFiscaNome.getEditText().setText(fisca.getNome());
        textInputLayoutFiscaEndereco.getEditText().setText(fisca.getEndereco());
        textInputLayoutFiscaBairro.getEditText().setText(fisca.getBairro());
        spinnerFiscaMunicipio.setSelection(spinnerFiscaMunicipioAdapter.getPosition(fisca.getMunicipio()));
        textInputLayoutFiscaCPF.getEditText().setText(fisca.getCpf());
        for(int i=0; i<radioGroupFiscaCpfConsultado.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaCpfConsultado.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getCpf_status())) {
                btn.setChecked(true);
                break;
            }
        }
        textInputLayoutFiscaNIS.getEditText().setText(fisca.getNis());
        textInputLayoutFiscaRG.getEditText().setText(fisca.getRg());
//        textInputLayoutFiscaNascimento.getEditText().setText(fisca.getData_nascimento());
        maskedEditTextFiscaNascimento.setText(formatData(fisca.getData_nascimento()));
        textInputLayoutFiscaMedidor1.getEditText().setText(fisca.getMedidor_vizinho_1());
        textInputLayoutFiscaMedidor2.getEditText().setText(fisca.getMedidor_vizinho_2());
//        textInputLayoutFiscaTelefone.getEditText().setText(fisca.getTelefone());
        maskedEditTextFiscaTelefone.setText(formatTelefone(fisca.getTelefone(), "tel"));
//        textInputLayoutFiscaCelular.getEditText().setText(fisca.getCelular());
        maskedEditTextFiscaCelular.setText(formatTelefone(fisca.getCelular(), "cel"));
        textInputLayoutFiscaEmail.getEditText().setText(fisca.getEmail());
        textInputLayoutFiscaLatitude.getEditText().setText(fisca.getLatitude());
        textInputLayoutFiscaLongitude.getEditText().setText(fisca.getLongitude());
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
        textInputLayoutCPFIndicado.getEditText().setText(fisca.getCpf_pre_indicacao());
        for(int i=0; i<radioGroupFiscaOrdemServico.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaOrdemServico.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getExiste_ordem())) {
                btn.setChecked(true);
                break;
            }
        }
        textInputLayoutFiscaOrdem.getEditText().setText(fisca.getNumero_ordem());
        for(int i=0; i<radioGroupFiscaEstadoOrdem.getChildCount(); i++) {
            RadioButton btn = (RadioButton) radioGroupFiscaEstadoOrdem.getChildAt(i);
            if (btn.getText().toString().equals(fisca.getEstado_ordem())) {
                btn.setChecked(true);
                break;
            }
        }
    }

    private String formatTelefone(String number, String type) {
        if(number!=null){
            if(!number.equals("")){

                String ddd = number.substring(0, 1);
                String middle = "";
                String end = "";
                if(type.equals("cel")){
                    middle = number.substring(2, 6);
                    end = number.substring(7, 10);
                }else{
                    middle = number.substring(2, 5);
                    end = number.substring(6, 9);
                }

                return "("+ ddd +")"+ middle +"-"+end;
            }
        }
        return "";
    }

    private String formatData(String data) {
        if(data!=null){
            if(!data.equals("")){

                String dia = "";
                String mes = "";
                String ano = "";

                if(data.length()>2){
                     dia = data.substring(0, 2);
                }
                if(data.length()>5){
                    mes = data.substring(2, 4);
                }

                if(data.length()<7){
                    ano = data.substring(4, 6);
                }else{
                    ano = data.substring(4, 8);
                }


                return dia +"/"+ mes +"/"+ano;
            }
        }
        return "";
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fiscaModel fiscaSaved = getFiscaFromFields(fisca);
        outState.putSerializable("fisca", fiscaSaved);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fiscaModel fiscaSaved = (fiscaModel) savedInstanceState.getSerializable("fisca");
        fillForm(fiscaSaved);
    }


    // VALIDAÇÃO
    private boolean validadeFields() {
        String isValid = "";

        if(fieldIsEmpty(textInputLayoutFiscaNome)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaEndereco)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaBairro)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaCPF)){isValid = "false";}
//        if(fieldIsEmpty(textInputLayoutFiscaNIS)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaRG)){isValid = "false";}
//        if(fieldIsEmpty(textInputLayoutFiscaNascimento)){isValid = "false";}
        if (fieldIsEmptyMasked(maskedEditTextFiscaNascimento)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaMedidor1)){isValid = "false";}
//        if(fieldIsEmpty(textInputLayoutFiscaMedidor2)){isValid = "false";}
//        if(fieldIsEmpty(textInputLayoutFiscaTelefone)){isValid = "false";}
//        if(fieldIsEmpty(textInputLayoutFiscaCelular)){isValid = "false";}
//        if(fieldIsEmpty(textInputLayoutFiscaEmail)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaLatitude)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaLongitude)){isValid = "false";}


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
        if (radioGroupFiscaServidao.getCheckedRadioButtonId() == -1) {
            isValid = "Servidao";
        }
        if (radioGroupFiscaPadrao.getCheckedRadioButtonId() == -1) {
            isValid = "Padrao";
        }
        if (radioGroupFiscaRede.getCheckedRadioButtonId() == -1) {
            isValid = "Rede";
        }
        if (radioGroupFiscaLigacao.getCheckedRadioButtonId() == -1) {
            isValid = "Ligacao";
        }
        if (radioGroupFiscaInvadida.getCheckedRadioButtonId() == -1) {
            isValid = "Invadida";
        }
//        switch(radioGroupFiscaAreaPreserva.getCheckedRadioButtonId()){
//            case -1:
//                isValid = "AreaPreserva";
//                break;
//        }
        if (radioGroupFiscaCpfConsultado.getCheckedRadioButtonId() == -1) {
            isValid = "cpfConsultado";
        }

        if (spinnerFiscaMunicipio.getSelectedItemPosition() == 0) {
            isValid = "municipio";
        }
        if (spinnerFiscaFuncionario.getSelectedItemPosition() == 0) {
            isValid = "funcionario";
        }

        switch (isValid){
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
//            case "AreaPreserva":
//                Toast.makeText(this, "Favor selecionar a opção Área de preservação ambiental!", Toast.LENGTH_SHORT).show();
//                break;
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
                        Toast.makeText(fiscalizacaoClandestinoFormActivity.this, "Favor, habilitar o GPS!", Toast.LENGTH_LONG).show();
                        Intent startGPSIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        fiscalizacaoClandestinoFormActivity.this.startActivity(startGPSIntent);
                    }
                }
            };

            assert locationManager != null;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(locationListener!=null){
            locationManager.removeUpdates(locationListener);
        }
    }

}
