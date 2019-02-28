package com.dudukling.enelz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.enelz.dao.lpDAO;
import com.dudukling.enelz.model.lpClandestino;

import java.util.Objects;

public class clandestinoFormActivity extends AppCompatActivity {
    private static final String REQUIRED_FIELD_ERROR_MSG = "Campo obrigatório!";

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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clandestino_form);
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
        }else if(tipoForm.equals("new")){
            setValidation();
            lpDAO dao = new lpDAO(this);
            int nextID = dao.getClandestinoLastID() + 1;
            dao.close();
            textViewClandestNumero.setText("Clandestino #"+nextID);
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

        textViewClandestNumero.setText("Clandestino #"+lpClandest.getId());
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
        lpDAO dao = new lpDAO(this);

        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;

            case R.id.menu_saveLP_button:
                if(validateForm()){
                    lpClandestino clandestSave = getLPFromForm();
                    if(tipoForm.equals("new")){
                        dao.insertClandestino(clandestSave, LPid);
                    }else{
                        dao.updateClandestino(clandestSave);
                    }

                    finish();
                }else{
                    Toast.makeText(clandestinoFormActivity.this, "Favor preencher todos os campos obrigatórios!", Toast.LENGTH_LONG).show();
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
        dao.close();
        return super.onOptionsItemSelected(item);
    }

    public void setValidation(){
        setValidateEmpty(textInputLayoutClandestEndereco);
        setValidateEmpty(textInputLayoutClandestTransformador);
        setValidateEmpty(textInputLayoutClandestTensao);
        setValidateEmpty(textInputLayoutClandestCorrente);
        setValidateEmpty(textInputLayoutClandestProtecao);
        setValidateEmpty(textInputLayoutClandestFatorPotencia);
        setValidateEmpty(textInputLayoutClandestCarga);
        setValidateEmpty(textInputLayoutClandestDescricao);
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

    public boolean validateForm() {
        if(fieldIsEmpty(textInputLayoutClandestEndereco)){return false;}
        if(fieldIsEmpty(textInputLayoutClandestTransformador)){return false;}
        if(fieldIsEmpty(textInputLayoutClandestTensao)){return false;}
        if(fieldIsEmpty(textInputLayoutClandestCorrente)){return false;}
        if(fieldIsEmpty(textInputLayoutClandestProtecao)){return false;}
        if(fieldIsEmpty(textInputLayoutClandestFatorPotencia)){return false;}
        if(fieldIsEmpty(textInputLayoutClandestCarga)){return false;}
        if(fieldIsEmpty(textInputLayoutClandestDescricao)){return false;}

        return true;
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
}
