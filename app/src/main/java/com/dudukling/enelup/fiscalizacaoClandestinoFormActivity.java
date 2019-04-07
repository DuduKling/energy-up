package com.dudukling.enelup;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.enelup.dao.fiscalizaDAO;
import com.dudukling.enelup.model.fiscaModel;
import com.dudukling.enelup.model.lpModel;

import java.util.Objects;

public class fiscalizacaoClandestinoFormActivity extends AppCompatActivity {

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
    private TextInputLayout textInputLayoutFiscaMedidor1;
    private TextInputLayout textInputLayoutFiscaMedidor2;
    private TextInputLayout textInputLayoutFiscaTelefone;
    private TextInputLayout textInputLayoutFiscaCelular;
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
            fillForm();
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
        }
        if(formType.equals("edit")){
            fillForm();
        }
        if(formType.equals("edit")||formType.equals("new")){
            buttonFiscaConsultaCPF.setVisibility(View.VISIBLE);
            buttonFiscaConsultaCPF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String CPF = textInputLayoutFiscaCPF.getEditText().getText().toString();

                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("cpf copiado", CPF);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(fiscalizacaoClandestinoFormActivity.this, "CPF "+CPF+" copiado para a área de transferência!", Toast.LENGTH_SHORT).show();

                    String url = "https://www.situacaocadastral.com.br";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
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
                    getFiscaFromFields();
                    if(formType.equals("new")){
                        dao.insert(fisca);
                    }else{
                        dao.update(fisca);
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

    private void getFiscaFromFields() {
        fisca.setFuncionario(spinnerFiscaFuncionario.getSelectedItem().toString());
        fisca.setNome(textInputLayoutFiscaNome.getEditText().getText().toString());
        fisca.setEndereco(textInputLayoutFiscaEndereco.getEditText().getText().toString());
        fisca.setBairro(textInputLayoutFiscaBairro.getEditText().getText().toString());
        fisca.setMunicipio(spinnerFiscaMunicipio.getSelectedItem().toString());
        fisca.setCpf(textInputLayoutFiscaCPF.getEditText().getText().toString());
        fisca.setCpf_status(((RadioButton)findViewById(radioGroupFiscaCpfConsultado.getCheckedRadioButtonId())).getText().toString());
        fisca.setNis(textInputLayoutFiscaNIS.getEditText().getText().toString());
        fisca.setRg(textInputLayoutFiscaRG.getEditText().getText().toString());
        fisca.setData_nascimento(textInputLayoutFiscaNascimento.getEditText().getText().toString());
        fisca.setMedidor_visinho_1(textInputLayoutFiscaMedidor1.getEditText().getText().toString());
        fisca.setMedidor_visinho_2(textInputLayoutFiscaMedidor2.getEditText().getText().toString());
        fisca.setTelefone(textInputLayoutFiscaTelefone.getEditText().getText().toString());
        fisca.setCelular(textInputLayoutFiscaCelular.getEditText().getText().toString());
        fisca.setEmail(textInputLayoutFiscaEmail.getEditText().getText().toString());
        fisca.setLatitude(textInputLayoutFiscaLatitude.getEditText().getText().toString());
        fisca.setLongitude(textInputLayoutFiscaLongitude.getEditText().getText().toString());
        if(radioGroupFiscaAreaPreserva.getCheckedRadioButtonId() != -1) {
            fisca.setPreservacao_ambiental(((RadioButton) findViewById(radioGroupFiscaAreaPreserva.getCheckedRadioButtonId())).getText().toString());
        }else{
            fisca.setPreservacao_ambiental("");
        }
        fisca.setArea_invadida(((RadioButton)findViewById(radioGroupFiscaInvadida.getCheckedRadioButtonId())).getText().toString());
        fisca.setTipo_ligacao(((RadioButton)findViewById(radioGroupFiscaLigacao.getCheckedRadioButtonId())).getText().toString());
        fisca.setRede_local(((RadioButton)findViewById(radioGroupFiscaRede.getCheckedRadioButtonId())).getText().toString());
        fisca.setPadrao_montado(((RadioButton)findViewById(radioGroupFiscaPadrao.getCheckedRadioButtonId())).getText().toString());
        fisca.setFaixa_servidao(((RadioButton)findViewById(radioGroupFiscaServidao.getCheckedRadioButtonId())).getText().toString());
        fisca.setPre_indicacao(((RadioButton)findViewById(radioGroupFiscaIndicacao.getCheckedRadioButtonId())).getText().toString());
        fisca.setCpf_pre_indicacao(textInputLayoutCPFIndicado.getEditText().getText().toString());
        fisca.setExiste_ordem(((RadioButton)findViewById(radioGroupFiscaOrdemServico.getCheckedRadioButtonId())).getText().toString());
        fisca.setNumero_ordem(textInputLayoutFiscaOrdem.getEditText().getText().toString());
        if(radioGroupFiscaOrdemServico.getCheckedRadioButtonId() == R.id.radioButtonFiscaOrdemServico1){
            fisca.setEstado_ordem(((RadioButton)findViewById(radioGroupFiscaEstadoOrdem.getCheckedRadioButtonId())).getText().toString());
        }else{
            fisca.setNumero_ordem("");
            fisca.setEstado_ordem("");
        }
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
        textInputLayoutFiscaMedidor1 = findViewById(R.id.textInputLayoutFiscaMedidor1);
        textInputLayoutFiscaMedidor2 = findViewById(R.id.textInputLayoutFiscaMedidor2);
        textInputLayoutFiscaTelefone = findViewById(R.id.textInputLayoutFiscaTelefone);
        textInputLayoutFiscaCelular = findViewById(R.id.textInputLayoutFiscaCelular);
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
                }else{
                    textInputLayoutFiscaOrdem.setVisibility(View.GONE);
                    textViewFiscaEstadoOrdem.setVisibility(View.GONE);
                    radioGroupFiscaEstadoOrdem.setVisibility(View.GONE);
                }
            }
        });

    }

    private boolean validadeFields() {
        String isValid = "";

        if(fieldIsEmpty(textInputLayoutFiscaNome)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaEndereco)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaBairro)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaCPF)){isValid = "false";}
//        if(fieldIsEmpty(textInputLayoutFiscaNIS)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaRG)){isValid = "false";}
        if(fieldIsEmpty(textInputLayoutFiscaNascimento)){isValid = "false";}
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
        switch(radioGroupFiscaServidao.getCheckedRadioButtonId()){
            case -1:
                isValid = "Servidao";
                break;
        }
        switch(radioGroupFiscaPadrao.getCheckedRadioButtonId()){
            case -1:
                isValid = "Padrao";
                break;
        }
        switch(radioGroupFiscaRede.getCheckedRadioButtonId()){
            case -1:
                isValid = "Rede";
                break;
        }
        switch(radioGroupFiscaLigacao.getCheckedRadioButtonId()){
            case -1:
                isValid = "Ligacao";
                break;
        }
        switch(radioGroupFiscaInvadida.getCheckedRadioButtonId()){
            case -1:
                isValid = "Invadida";
                break;
        }
//        switch(radioGroupFiscaAreaPreserva.getCheckedRadioButtonId()){
//            case -1:
//                isValid = "AreaPreserva";
//                break;
//        }
        switch(radioGroupFiscaCpfConsultado.getCheckedRadioButtonId()){
            case -1:
                isValid = "cpfConsultado";
                break;
        }
        switch(spinnerFiscaMunicipio.getSelectedItemPosition()){
            case 0: // Selecione
                isValid = "municipio";
                break;
        }
        switch(spinnerFiscaFuncionario.getSelectedItemPosition()){
            case 0: // Selecione
                isValid = "funcionario";
                break;
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

    private void fillForm() {
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
        textInputLayoutFiscaNascimento.getEditText().setText(fisca.getData_nascimento());
        textInputLayoutFiscaMedidor1.getEditText().setText(fisca.getMedidor_visinho_1());
        textInputLayoutFiscaMedidor2.getEditText().setText(fisca.getMedidor_visinho_2());
        textInputLayoutFiscaTelefone.getEditText().setText(fisca.getTelefone());
        textInputLayoutFiscaCelular.getEditText().setText(fisca.getCelular());
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

}
