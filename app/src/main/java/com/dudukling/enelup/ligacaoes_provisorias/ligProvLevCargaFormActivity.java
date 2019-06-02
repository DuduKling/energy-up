package com.dudukling.enelup.ligacaoes_provisorias;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.dudukling.enelup.R;
import com.dudukling.enelup.dao.lpDAO;
import com.dudukling.enelup.model.lpPotenciaModel;

import java.util.List;
import java.util.Objects;

public class ligProvLevCargaFormActivity extends AppCompatActivity {
    private static final String REQUIRED_FIELD_ERROR_MSG = "Campo obrigatório!";

    private TextView textViewLevCargaPotencia;
    private TextInputLayout textInputLayoutLevCargaOtherEquip;
    private TextInputLayout textInputLayoutLevCargaOtherPotencia;
    private TextInputLayout textInputLayoutLevCargaQuantidade;
    private Spinner spinnerLevCargaEquip;
    private int lpID;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.lig_prov_activity_form_lev_carga_form);
        setTitle("Adicionar carga");

        Intent intent = getIntent();
        lpID = (int) intent.getSerializableExtra("LPid");

        lpDAO dao = new lpDAO(this);
        List<String> descPotList = dao.getDescList();
        dao.close();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, descPotList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        textViewLevCargaPotencia = findViewById(R.id.textViewLevCargaPotencia);
        textInputLayoutLevCargaOtherEquip = findViewById(R.id.textInputLayoutLevCargaOtherEquip);
        textInputLayoutLevCargaOtherPotencia = findViewById(R.id.textInputLayoutLevCargaOtherPotencia);
        textInputLayoutLevCargaQuantidade = findViewById(R.id.textInputLayoutLevCargaQuantidade);

        setFields();

        spinnerLevCargaEquip = findViewById(R.id.spinnerLevCargaEquip);
        spinnerLevCargaEquip.setAdapter(arrayAdapter);
        spinnerLevCargaEquip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {
                    case 0: // OUTRO
                        textViewLevCargaPotencia.setText("Potência: ---");
                        textInputLayoutLevCargaOtherEquip.setVisibility(View.VISIBLE);
                        textInputLayoutLevCargaOtherPotencia.setVisibility(View.VISIBLE);
                        break;
                    default:
                        lpDAO dao = new lpDAO(ligProvLevCargaFormActivity.this);
                        String potencia = dao.getPotenciaFromItemID(position);
                        dao.close();
                        textViewLevCargaPotencia.setText("Potência: " +potencia+" W");
                        textInputLayoutLevCargaOtherEquip.setVisibility(View.GONE);
                        textInputLayoutLevCargaOtherPotencia.setVisibility(View.GONE);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setFields() {
        setValidateEmpty(textInputLayoutLevCargaQuantidade);
        setValidateEmpty(textInputLayoutLevCargaOtherEquip);
        setValidateEmpty(textInputLayoutLevCargaOtherPotencia);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        lpDAO dao = new lpDAO(this);

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;

            case R.id.menu_save_button:
                if(validadeFields()){
                    lpPotenciaModel lpPot = getLpPotFromFields();
                    dao.insertPotencia(lpPot, lpID);
                    finish();
                }
                break;
        }
        dao.close();

        return super.onOptionsItemSelected(item);
    }

    private lpPotenciaModel getLpPotFromFields() {
        lpPotenciaModel pot = new lpPotenciaModel();
        pot.setQuantidade(textInputLayoutLevCargaQuantidade.getEditText().getText().toString());

        if(spinnerLevCargaEquip.getSelectedItemPosition()==0) {
            pot.setDescricao(textInputLayoutLevCargaOtherEquip.getEditText().getText().toString());
            pot.setPotencia(textInputLayoutLevCargaOtherPotencia.getEditText().getText().toString());
        }else{
            lpDAO dao = new lpDAO(this);
            lpPotenciaModel potDao = dao.getPotInfo(spinnerLevCargaEquip.getSelectedItemPosition()+1);
            dao.close();
            pot.setDescricao(potDao.getDescricao());
            pot.setPotencia(potDao.getPotencia());
        }

        return pot;
    }

    private boolean validadeFields() {
        if(spinnerLevCargaEquip.getSelectedItemPosition()==0){
            if(fieldIsEmpty(textInputLayoutLevCargaOtherEquip)){return false;}
            if(fieldIsEmpty(textInputLayoutLevCargaOtherPotencia)){return false;}
        }

        if(fieldIsEmpty(textInputLayoutLevCargaQuantidade)){return false;}

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save_form, menu);

        return super.onCreateOptionsMenu(menu);
    }
}
