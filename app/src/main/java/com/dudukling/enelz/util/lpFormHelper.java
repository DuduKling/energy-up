package com.dudukling.enelz.util;

import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.enelz.LigProvLevCargaActivity;
import com.dudukling.enelz.R;
import com.dudukling.enelz.dao.lpDAO;
import com.dudukling.enelz.ligProvFormActivity;
import com.dudukling.enelz.model.lpModel;
import com.dudukling.enelz.model.lpPotencia;

import java.io.File;
import java.util.List;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

public class lpFormHelper {
    private static final String REQUIRED_FIELD_ERROR_MSG = "Campo obrigatório!";

    private static ligProvFormActivity activity;
    private final lpModel lp;
    private final String formType;

    private TextView TextViewOrdem;
    private TextView TextViewCliente;
    private TextView TextViewEndereco;
    private TextView TextViewNumeroCliente;
    private TextView TextViewBairro;
    private TextView TextViewDescEtapa;
    private TextView TextViewObservacoes;
    private TextView TextViewRetorno;
    private TextView TextViewObservacaoExe;

    private TextInputLayout textInputObs;
    private EditText fieldObs;


    private TextView textViewInfoTotDec;
    private TextView textViewInfoCorrente;
    private TextView textViewInfoLevCarga;
    private TextView textViewInfoTotEnc;
    private TextView textViewInfoDiferenca;


    private Spinner spinnerCalcDec;
    private ArrayAdapter<CharSequence> spinnerCalcDecAdapter;
    private int calcDecSpinnerPosition;

    private Spinner spinnerCalcDecFatPot;
    private ArrayAdapter<CharSequence> spinnerCalcDecFatPotAdapter;
    private TextView textViewCalcDecFatPot;

    private TextInputLayout textInputLayoutCalcDecValor;
    private TextInputLayout textInputLayoutCalcDecPeriodo;
    private TextInputLayout textInputLayoutCalcDecTempo;
    private TextInputLayout textInputLayoutCalcDecTensao;

    private CheckBox checkBoxCalcEncExcedente;
    private CheckBox checkBoxCalcEncCorrente;
    private TextInputLayout textInputLayoutCalcEncPeriodo;
    private TextInputLayout textInputLayoutCalcEncTempo;
    private TextInputLayout textInputLayoutCalcEncCorrente;
    private TextInputLayout textInputLayoutCalcEncTensao;

    private CheckBox checkBoxCalcEncLevCarga;
    private FloatingActionButton floatingActionButtonCalcEncLevCarga;
    private int potenciaTotalLevCarga;


    public lpFormHelper(final ligProvFormActivity activity1, String formType, lpModel lp1) {
        activity = activity1;
        lp = lp1;
        this.formType = formType;

        TextViewOrdem = activity.findViewById(R.id.TextViewFormOrdem);
        TextViewCliente = activity.findViewById(R.id.TextViewFormCliente);
        TextViewEndereco = activity.findViewById(R.id.TextViewFormEndereco);

        TextViewNumeroCliente = activity.findViewById(R.id.TextViewnNumeroCliente);
        TextViewBairro = activity.findViewById(R.id.TextViewFormBairro);
        TextViewDescEtapa = activity.findViewById(R.id.TextViewFormDescEtapa);
        TextViewObservacoes = activity.findViewById(R.id.TextViewFormObservacoes);
        TextViewRetorno = activity.findViewById(R.id.TextViewFormDescRetorno);
        TextViewObservacaoExe = activity.findViewById(R.id.TextViewFormObservacaoExe);

        setFields();

        checkTypeOfForm(formType);
    }

    private void setFields() {
        textInputObs = activity.findViewById(R.id.TextInputObs);
        fieldObs = textInputObs.getEditText();
        setValidateEmpty(textInputObs);

        setCalcDecFields();
        setValidateDecFields();

        setCalcEncFields();
        setValidateEncFields();

        setCalcInfoFields();
    }

    private void checkTypeOfForm(String formType) {
        fillForm();

        if (formType.equals("readOnly")) {
            disableEditText(fieldObs);

            disableSpinner(spinnerCalcDec);
            disableSpinner(spinnerCalcDecFatPot);
            disableEditText(textInputLayoutCalcDecValor.getEditText());
            disableEditText(textInputLayoutCalcDecPeriodo.getEditText());
            disableEditText(textInputLayoutCalcDecTempo.getEditText());
            disableEditText(textInputLayoutCalcDecTensao.getEditText());

            disableCheckbox(checkBoxCalcEncExcedente);
            disableCheckbox(checkBoxCalcEncCorrente);
            disableCheckbox(checkBoxCalcEncLevCarga);

        }
    }

    private void fillForm() {
        TextViewOrdem.setText(lp.getOrdem());
        TextViewCliente.setText(lp.getCliente());
        TextViewEndereco.setText(lp.getEndereco());

        TextViewNumeroCliente.setText(lp.getNumero_cliente());
        TextViewBairro.setText(lp.getBairro());
        TextViewDescEtapa.setText(lp.getDescricao_etapa());
        TextViewObservacoes.setText(lp.getObservacoes());
        TextViewRetorno.setText(lp.getDescricao_retorno());
        TextViewObservacaoExe.setText(lp.getObservacao_exe());

        fieldObs.setText(lp.getUserObservacao());

        fillCalcDecForm();
        fillCalcEncForm();
        fillCalcInfoForm();
    }

    public lpModel getLPFromForm(lpModel lp) {

        lp.setUserObservacao(fieldObs.getText().toString());

        return lp;
    }



    // HELPERS
    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setTextColor(Color.parseColor("#616161"));
    }
    private void disableCheckbox(CheckBox checkBox) {
        checkBox.setEnabled(false);
    }
    private void disableSpinner(Spinner spinner) {
        spinner.setEnabled(false);
        spinner.setClickable(false);
    }

    public static void deleteImagesFromPhoneMemory(lpModel lp) {
        List<String> imagesListToDelete = lp.getImagesList();
        for(int i = 0; i < imagesListToDelete.size(); i++){
            File file = new File(imagesListToDelete.get(i));
            boolean deleted = file.delete();
            Log.d("TAG4", "delete() called: "+deleted);
        }
    }



    // VALIDAÇÃO
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

    public String validateForm(lpModel lp) {
        if(fieldIsEmpty(textInputObs)){return "false";}

        if(fieldIsEmpty(textInputLayoutCalcDecPeriodo)){return "false";}
        if(fieldIsEmpty(textInputLayoutCalcDecTempo)){return "false";}

        if(!isCalcDecValid()){return "false";}

        if(!isCalcEncValid()){return "false";}

        if(
            lp.getAutoLat().isEmpty() ||
            lp.getAutoLong().isEmpty()
        ){
            return "gps";
        }

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



    // CÁLCULO DECLARADO
    private void setCalcDecFields() {
        spinnerCalcDec = activity.findViewById(R.id.spinnerCalcDec);
        spinnerCalcDecAdapter = ArrayAdapter.createFromResource(activity, R.array.unidades, android.R.layout.simple_spinner_item);
        spinnerCalcDecAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCalcDec.setAdapter(spinnerCalcDecAdapter);

        spinnerCalcDec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                calcDecSpinnerPosition = position;

                switch(position){
                    case 0: // Selecione
                        spinnerCalcDecFatPot.setVisibility(View.GONE);
                        spinnerCalcDecFatPot.setSelection(0);
                        textViewCalcDecFatPot.setVisibility(View.GONE);
                        textInputLayoutCalcDecValor.setVisibility(View.GONE);
                        textInputLayoutCalcDecValor.getEditText().setText(null);
//                        textInputLayoutCalcDecPeriodo.setVisibility(View.GONE);
//                        textInputLayoutCalcDecPeriodo.getEditText().setText(null);
//                        textInputLayoutCalcDecTempo.setVisibility(View.GONE);
//                        textInputLayoutCalcDecTempo.getEditText().setText(null);
                        textInputLayoutCalcDecTensao.setVisibility(View.GONE);
                        textInputLayoutCalcDecTensao.getEditText().setText(null);

                        break;

                    case 1: // kVA
                        spinnerCalcDecFatPot.setVisibility(View.VISIBLE);
                        textViewCalcDecFatPot.setVisibility(View.VISIBLE);

                        textInputLayoutCalcDecValor.setVisibility(View.VISIBLE);
                        textInputLayoutCalcDecValor.setHint("Valor kVA");
//                        textInputLayoutCalcDecPeriodo.setVisibility(View.VISIBLE);
//                        textInputLayoutCalcDecTempo.setVisibility(View.VISIBLE);


                        textInputLayoutCalcDecTensao.setVisibility(View.GONE);
                        textInputLayoutCalcDecTensao.getEditText().setText(null);
                        break;

                    case 2: // kW
                        textInputLayoutCalcDecValor.setVisibility(View.VISIBLE);
                        textInputLayoutCalcDecValor.setHint("Valor kW");
//                        textInputLayoutCalcDecPeriodo.setVisibility(View.VISIBLE);
//                        textInputLayoutCalcDecTempo.setVisibility(View.VISIBLE);


                        spinnerCalcDecFatPot.setVisibility(View.GONE);
                        spinnerCalcDecFatPot.setSelection(0);
                        textViewCalcDecFatPot.setVisibility(View.GONE);
                        textInputLayoutCalcDecTensao.setVisibility(View.GONE);
                        textInputLayoutCalcDecTensao.getEditText().setText(null);
                        break;

                    case 3: // Watts
                        textInputLayoutCalcDecValor.setVisibility(View.VISIBLE);
                        textInputLayoutCalcDecValor.setHint("Valor Watts");
//                        textInputLayoutCalcDecPeriodo.setVisibility(View.VISIBLE);
//                        textInputLayoutCalcDecTempo.setVisibility(View.VISIBLE);


                        spinnerCalcDecFatPot.setVisibility(View.GONE);
                        spinnerCalcDecFatPot.setSelection(0);
                        textViewCalcDecFatPot.setVisibility(View.GONE);
                        textInputLayoutCalcDecTensao.setVisibility(View.GONE);
                        textInputLayoutCalcDecTensao.getEditText().setText(null);
                        break;

                    case 4: // Ampere
                        textInputLayoutCalcDecValor.setVisibility(View.VISIBLE);
                        textInputLayoutCalcDecValor.setHint("Valor Amperes");
//                        textInputLayoutCalcDecPeriodo.setVisibility(View.VISIBLE);
//                        textInputLayoutCalcDecTempo.setVisibility(View.VISIBLE);
                        textInputLayoutCalcDecTensao.setVisibility(View.VISIBLE);

                        spinnerCalcDecFatPot.setVisibility(View.GONE);
                        spinnerCalcDecFatPot.setSelection(0);
                        textViewCalcDecFatPot.setVisibility(View.GONE);
                        break;

                    case 5: // kWh
                        textInputLayoutCalcDecValor.setVisibility(View.VISIBLE);
                        textInputLayoutCalcDecValor.setHint("Valor KWh");


                        spinnerCalcDecFatPot.setVisibility(View.GONE);
                        spinnerCalcDecFatPot.setSelection(0);
                        textViewCalcDecFatPot.setVisibility(View.GONE);
//                        textInputLayoutCalcDecPeriodo.setVisibility(View.GONE);
//                        textInputLayoutCalcDecPeriodo.getEditText().setText(null);
//                        textInputLayoutCalcDecTempo.setVisibility(View.GONE);
//                        textInputLayoutCalcDecTempo.getEditText().setText(null);
                        textInputLayoutCalcDecTensao.setVisibility(View.GONE);
                        textInputLayoutCalcDecTensao.getEditText().setText(null);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });



        // Campos:
        spinnerCalcDecFatPot = activity.findViewById(R.id.spinnerCalcDecFatPot);
        spinnerCalcDecFatPotAdapter = ArrayAdapter.createFromResource(activity, R.array.fatorPotencia, android.R.layout.simple_spinner_item);
        spinnerCalcDecFatPotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCalcDecFatPot.setAdapter(spinnerCalcDecFatPotAdapter);
        textViewCalcDecFatPot = activity.findViewById(R.id.textViewCalcDecFatPot);
        spinnerCalcDecFatPot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected = spinnerCalcDecFatPot.getSelectedItem().toString();
                if(!selected.equals("Selecione")){
                    lp.setCalcDecFatorPotencia(selected);
                }else{
                    lp.setCalcDecFatorPotencia(null);
                }
                calculaDeclarado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        textInputLayoutCalcDecValor = activity.findViewById(R.id.textInputLayoutCalcDecValor);
        textInputLayoutCalcDecValor.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null || !String.valueOf(s).equals("")){
                    lp.setCalcDecValor(s.toString());
                }else{
                    lp.setCalcDecValor(null);
                }
                calculaDeclarado();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        textInputLayoutCalcDecPeriodo = activity.findViewById(R.id.textInputLayoutCalcDecPeriodo);
        textInputLayoutCalcDecPeriodo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null || !String.valueOf(s).equals("")){
                    lp.setCalcDecPeriodo(s.toString());
                }else{
                    lp.setCalcDecPeriodo(null);
                }
                calculaDeclarado();
                calculaEncontrado();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        textInputLayoutCalcDecTempo = activity.findViewById(R.id.textInputLayoutCalcDecTempo);
        textInputLayoutCalcDecTempo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null || !String.valueOf(s).equals("")){
                    lp.setCalcDecTempo(s.toString());
                }else{
                    lp.setCalcDecTempo(null);
                }
                calculaDeclarado();
                calculaEncontrado();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        textInputLayoutCalcDecTensao = activity.findViewById(R.id.textInputLayoutCalcDecTensao);
        textInputLayoutCalcDecTensao.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null || !String.valueOf(s).equals("")){
                    lp.setCalcDecTensao(s.toString());
                }else{
                    lp.setCalcDecTensao(null);
                }
                calculaDeclarado();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }

    private void calculaDeclarado() {
        float total = 0;

        float valor = 0;
        if(lp.getCalcDecValor()!=null){
            if(!lp.getCalcDecValor().equals("")){
                valor = Float.valueOf(lp.getCalcDecValor());
            }
        }

        float fp = 0;
        if(lp.getCalcDecFatorPotencia()!=null){
            if(!lp.getCalcDecFatorPotencia().equals("")){
                fp = Float.valueOf(lp.getCalcDecFatorPotencia());
            }
        }

        float tensao = 0;
        if(lp.getCalcDecTensao()!=null){
            if(!lp.getCalcDecTensao().equals("")){
                tensao = Float.valueOf(lp.getCalcDecTensao());
            }
        }

        int periodo = 0;
        if(lp.getCalcDecPeriodo()!=null){
            if(!lp.getCalcDecPeriodo().equals("")){
                periodo = Integer.valueOf(lp.getCalcDecPeriodo());
            }
        }

        int tempo = 0;
        if(lp.getCalcDecTempo()!=null){
            if(!lp.getCalcDecTempo().equals("")){
                tempo = Integer.valueOf(lp.getCalcDecTempo());
            }
        }

        switch(calcDecSpinnerPosition){
            case 0:
                break;

            case 1: // kVA = Valor * Fator * Período * Tempo
                total = valor * fp * periodo * tempo;

                break;

            case 2: // kW = Valor * Período * Tempo
                total = valor * periodo * tempo;
                break;

            case 3: // Watts = (Valor * Período * Tempo) / 1000
                total = valor * periodo * tempo;
                total = total /1000;
                break;

            case 4: // Ampere = ((Valor * Tensão) * Período * Tempo) / 1000
                total = valor * tensao * periodo * tempo;
                total = total /1000;
                break;

            case 5: // kWh = Valor
                total = valor;
                break;
        }

        if(total==0){
            textViewInfoTotDec.setText("Carga Total Declarada: ---");
        }else{
            textViewInfoTotDec.setText("Carga Total Declarada: "+ total +" kWh");
            lp.setCalcDecKwh(String.valueOf(total));
        }

        calculaDiferenca();
    }


    private void setValidateDecFields() {
        setValidateEmpty(textInputLayoutCalcDecValor);
        setValidateEmpty(textInputLayoutCalcDecPeriodo);
        setValidateEmpty(textInputLayoutCalcDecTempo);
        setValidateEmpty(textInputLayoutCalcDecTensao);
    }

    private boolean isCalcDecValid() {
        Boolean valid = true;
        switch(calcDecSpinnerPosition){
            case 0:
                Toast.makeText(activity, "Favor selecionar uma unidade na área de Carga Declarada!", Toast.LENGTH_SHORT).show();
                return false;

            case 1: // kVA
                if(spinnerCalcDecFatPot.getSelectedItemPosition()==0){
                    Toast.makeText(activity, "Selecione um fator de potência!", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
                if(fieldIsEmpty(textInputLayoutCalcDecValor)){valid = false;}
//                if(fieldIsEmpty(textInputLayoutCalcDecPeriodo)){valid = false;}
//                if(fieldIsEmpty(textInputLayoutCalcDecTempo)){valid = false;}
                break;

            case 2: // kW
            case 3: // Watts
                if(fieldIsEmpty(textInputLayoutCalcDecValor)){valid = false;}
//                if(fieldIsEmpty(textInputLayoutCalcDecPeriodo)){valid = false;}
//                if(fieldIsEmpty(textInputLayoutCalcDecTempo)){valid = false;}
                break;

            case 4: // Ampere
                if(fieldIsEmpty(textInputLayoutCalcDecValor)){valid = false;}
//                if(fieldIsEmpty(textInputLayoutCalcDecPeriodo)){valid = false;}
//                if(fieldIsEmpty(textInputLayoutCalcDecTempo)){valid = false;}
                if(fieldIsEmpty(textInputLayoutCalcDecTensao)){valid = false;}
                break;

            case 5: // kWh
                if(fieldIsEmpty(textInputLayoutCalcDecValor)){valid = false;}
                break;
        }

        return valid;
    }

    private void fillCalcDecForm() {
        if(lp.getCalcDecFatorPotencia()==null){lp.setCalcDecFatorPotencia("");}
        if(lp.getCalcDecTensao()==null){lp.setCalcDecTensao("");}

        if(
            !lp.getCalcDecValor().equals("") &&
            !lp.getCalcDecFatorPotencia().equals("")
        ) {

            spinnerCalcDec.setSelection(1); // kVA

        }else if(!lp.getCalcDecTensao().equals("")){

            spinnerCalcDec.setSelection(4); // Amperes

        }else if(
            !lp.getCalcDecValor().equals("") &&
            lp.getCalcDecFatorPotencia().equals("") &&
            lp.getCalcDecTensao().equals("")
        ){
            if(parseFloat(lp.getCalcDecValor()) * parseFloat(lp.getCalcDecPeriodo()) * parseFloat(lp.getCalcDecTempo())
                    == parseFloat(lp.getCalcDecKwh())){

                spinnerCalcDec.setSelection(2); // kW

            }else if((parseFloat(lp.getCalcDecValor()) * parseFloat(lp.getCalcDecPeriodo()) * parseFloat(lp.getCalcDecTempo()))/1000
                    == parseFloat(lp.getCalcDecKwh())){

                spinnerCalcDec.setSelection(3); // Watts

            }else if(parseFloat(lp.getCalcDecValor()) == parseFloat(lp.getCalcDecKwh())){
                spinnerCalcDec.setSelection(5); // kWh
            }
        }



        textInputLayoutCalcDecValor.getEditText().setText(lp.getCalcDecValor());
        textInputLayoutCalcDecPeriodo.getEditText().setText(lp.getCalcDecPeriodo());
        textInputLayoutCalcDecTempo.getEditText().setText(lp.getCalcDecTempo());
        textInputLayoutCalcDecTensao.getEditText().setText(lp.getCalcDecTensao());
        if(lp.getCalcDecFatorPotencia()!=null){
            if(!lp.getCalcDecFatorPotencia().equals("")){
                spinnerCalcDecFatPot.setSelection(spinnerCalcDecFatPotAdapter.getPosition(lp.getCalcDecFatorPotencia()));
            }
        }



    }



    // CÁLCULO ENCONTRADO
    private void setCalcEncFields() {
        textInputLayoutCalcEncPeriodo = activity.findViewById(R.id.textInputLayoutCalcEncPeriodo);
        textInputLayoutCalcEncTempo = activity.findViewById(R.id.TextInputLayoutCalcEncTempo);
        textInputLayoutCalcEncCorrente = activity.findViewById(R.id.textInputLayoutCalcEncCorrente);
        textInputLayoutCalcEncTensao = activity.findViewById(R.id.textInputLayoutCalcEncTensao);

        checkBoxCalcEncExcedente = activity.findViewById(R.id.checkBoxCalcEncExcedente);
        checkBoxCalcEncExcedente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    textInputLayoutCalcEncPeriodo.setVisibility(View.VISIBLE);
                    textInputLayoutCalcEncTempo.setVisibility(View.VISIBLE);
                }else{
                    textInputLayoutCalcEncPeriodo.setVisibility(View.GONE);
                    textInputLayoutCalcEncPeriodo.getEditText().setText(null);
                    textInputLayoutCalcEncTempo.setVisibility(View.GONE);
                    textInputLayoutCalcEncTempo.getEditText().setText(null);
                }
            }
        });


        checkBoxCalcEncCorrente = activity.findViewById(R.id.checkBoxCalcEncCorrente);
        checkBoxCalcEncCorrente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    textInputLayoutCalcEncCorrente.setVisibility(View.VISIBLE);
                    textInputLayoutCalcEncTensao.setVisibility(View.VISIBLE);
                }else{
                    textInputLayoutCalcEncCorrente.setVisibility(View.GONE);
                    textInputLayoutCalcEncCorrente.getEditText().setText(null);
                    textInputLayoutCalcEncTensao.setVisibility(View.GONE);
                    textInputLayoutCalcEncTensao.getEditText().setText(null);
                }
            }
        });

        floatingActionButtonCalcEncLevCarga = activity.findViewById(R.id.floatingActionButtonCalcEncLevCarga);
        floatingActionButtonCalcEncLevCarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToLevCarga = new Intent(activity, LigProvLevCargaActivity.class);
                if(formType.equals("readOnly")){
                    goToLevCarga
                        .putExtra("LPid", lp.getId())
                        .putExtra("type", "readOnly");
                }else{
                    goToLevCarga
                            .putExtra("LPid", lp.getId())
                            .putExtra("type", "");
                }
                activity.startActivityForResult(goToLevCarga, 1123);
            }
        });

        checkBoxCalcEncLevCarga = activity.findViewById(R.id.checkBoxCalcEncLevCarga);
        checkBoxCalcEncLevCarga.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    floatingActionButtonCalcEncLevCarga.setVisibility(View.VISIBLE);
                }else{
                    floatingActionButtonCalcEncLevCarga.setVisibility(View.GONE);
                }
            }
        });

        textInputLayoutCalcEncPeriodo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null || !String.valueOf(s).equals("")){
                    lp.setCalcEncPeriodo(s.toString());
                }else{
                    lp.setCalcEncPeriodo(null);
                }
                calculaEncontrado();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        textInputLayoutCalcEncTempo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null || !String.valueOf(s).equals("")){
                    lp.setCalcEncTempo(s.toString());
                }else{
                    lp.setCalcEncTempo(null);
                }
                calculaEncontrado();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        textInputLayoutCalcEncCorrente.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null || !String.valueOf(s).equals("")){
                    lp.setCalcEncCorrente(s.toString());
                }else{
                    lp.setCalcDecValor(null);
                }
                calculaEncontrado();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        textInputLayoutCalcEncTensao.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null || !String.valueOf(s).equals("")){
                    lp.setCalcEncTensao(s.toString());
                }else{
                    lp.setCalcEncTensao(null);
                }
                calculaEncontrado();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }

    private void calculaEncontrado() {
        float totalCorrente = 0;

        int periodo = 0;
        if(lp.getCalcDecPeriodo()!=null){
            if(!lp.getCalcDecPeriodo().equals("")){
                periodo = Integer.valueOf(lp.getCalcDecPeriodo());
            }
        }

        int tempo = 0;
        if(lp.getCalcDecTempo()!=null){
            if(!lp.getCalcDecTempo().equals("")){
                tempo = Integer.valueOf(lp.getCalcDecTempo());
            }
        }

        float corrente = 0;
        if(lp.getCalcEncCorrente()!=null){
            if(!lp.getCalcEncCorrente().equals("")){
                corrente = Float.valueOf(lp.getCalcEncCorrente());
            }
        }

        int periodoExcedente = 0;
        if(lp.getCalcEncPeriodo()!=null){
            if(!lp.getCalcEncPeriodo().equals("")){
                periodoExcedente = Integer.valueOf(lp.getCalcEncPeriodo());
            }
        }

        int tempoExcedente = 0;
        if(lp.getCalcEncTempo()!=null){
            if(!lp.getCalcEncTempo().equals("")){
                tempoExcedente = Integer.valueOf(lp.getCalcEncTempo());
            }
        }

        int tensao = 0;
        if(lp.getCalcEncTensao()!=null){
            if(!lp.getCalcEncTensao().equals("")){
                tensao = Integer.valueOf(lp.getCalcEncTensao());
            }
        }

        if(corrente!=0 && tensao!=0 && periodo!=0 && tempo!=0){
            if(periodoExcedente!=0 && tempoExcedente!=0){
                totalCorrente = (corrente * tensao * periodo * tempo) + (corrente * tensao * periodoExcedente * tempoExcedente);
                totalCorrente = totalCorrente /1000;
            }else{
                totalCorrente = corrente * tensao * periodo * tempo;
                totalCorrente = totalCorrente /1000;
            }
        }

        if(totalCorrente==0){
            textViewInfoCorrente.setText("Corrente: ---");
        }else{
            textViewInfoCorrente.setText("Corrente: "+ totalCorrente +" kWh");
        }



        // Levantamento:
        int totalLevCarga = 0;

        lpDAO dao = new lpDAO(activity);
        potenciaTotalLevCarga = dao.getLPPotenciaTotal(lp.getId());
        dao.close();

        if(potenciaTotalLevCarga!=0 && periodo!=0 && tempo!=0){
            if(periodoExcedente!=0 && tempoExcedente!=0){
                totalLevCarga = (potenciaTotalLevCarga * periodo * tempo) + (potenciaTotalLevCarga * periodoExcedente * tempoExcedente);
                totalLevCarga = totalLevCarga /1000;
            }else{
                totalLevCarga = potenciaTotalLevCarga * periodo * tempo;
                totalLevCarga = totalLevCarga /1000;
            }
        }

        if(totalLevCarga==0){
            textViewInfoLevCarga.setText("Levantamento: ---");
        }else{
            textViewInfoLevCarga.setText("Levantamento: "+ totalLevCarga +" kWh");
        }




        // Total:
        float totalEncontrado;
        if(totalLevCarga!=0 || totalCorrente!=0) {
            if(totalLevCarga > totalCorrente){
                totalEncontrado = totalLevCarga;
            }else{
                totalEncontrado = totalCorrente;
            }
            textViewInfoTotEnc.setText("Carga Total Encontrada: "+totalEncontrado+" kWh");
            lp.setCalcEncKwh(String.valueOf(totalEncontrado));
        }else{
            textViewInfoTotEnc.setText("Carga Total Encontrada: ---");
        }

        calculaDiferenca();
    }

    private void setValidateEncFields() {
        setValidateEmpty(textInputLayoutCalcEncPeriodo);
        setValidateEmpty(textInputLayoutCalcEncTempo);
        setValidateEmpty(textInputLayoutCalcEncCorrente);
        setValidateEmpty(textInputLayoutCalcEncTensao);
    }

    private boolean isCalcEncValid() {
        Boolean valid = true;
        if(checkBoxCalcEncExcedente.isChecked()){
            if(fieldIsEmpty(textInputLayoutCalcEncPeriodo)){valid = false;}
            if(fieldIsEmpty(textInputLayoutCalcEncTempo)){valid = false;}
        }

        if(checkBoxCalcEncCorrente.isChecked()){
            if(fieldIsEmpty(textInputLayoutCalcEncCorrente)){valid = false;}
            if(fieldIsEmpty(textInputLayoutCalcEncTensao)){valid = false;}
        }

        if(checkBoxCalcEncLevCarga.isChecked()){
            lpDAO dao = new lpDAO(activity);
            List<lpPotencia> potlist = dao.getLPPotenciaList(lp.getId());
            dao.close();
            if(potlist.size()<1){
                Toast.makeText(activity, "Favor preencher a área de Levantamento de Carga!", Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }


        return valid;
    }

    private void fillCalcEncForm() {
        if(!lp.getCalcEncPeriodo().equals("") && !lp.getCalcEncTempo().equals("")){
            checkBoxCalcEncExcedente.setChecked(true);


            textInputLayoutCalcEncPeriodo.getEditText().setText(lp.getCalcEncPeriodo());
            disableEditText(textInputLayoutCalcEncPeriodo.getEditText());

            textInputLayoutCalcEncTempo.getEditText().setText(lp.getCalcEncTempo());
            disableEditText(textInputLayoutCalcEncTempo.getEditText());
        }

        if(!lp.getCalcEncCorrente().equals("") && !lp.getCalcEncTensao().equals("")){
            checkBoxCalcEncCorrente.setChecked(true);


            textInputLayoutCalcEncCorrente.getEditText().setText(lp.getCalcEncCorrente());
            disableEditText(textInputLayoutCalcEncCorrente.getEditText());

            textInputLayoutCalcEncTensao.getEditText().setText(lp.getCalcEncTensao());
            disableEditText(textInputLayoutCalcEncTensao.getEditText());
        }


        lpDAO dao = new lpDAO(activity);
        List<lpPotencia> potlist = dao.getLPPotenciaList(lp.getId());
        dao.close();
        if(potlist.size()>0){
            checkBoxCalcEncLevCarga.setChecked(true);
            floatingActionButtonCalcEncLevCarga.setVisibility(View.VISIBLE);
        }

    }



    // CÁLCULO INFO
    private void setCalcInfoFields() {
        textViewInfoTotDec = activity.findViewById(R.id.textViewInfoTotDec);
        textViewInfoCorrente = activity.findViewById(R.id.textViewInfoCorrente);
        textViewInfoLevCarga = activity.findViewById(R.id.textViewInfoLevCarga);
        textViewInfoTotEnc = activity.findViewById(R.id.textViewInfoTotEnc);
        textViewInfoDiferenca = activity.findViewById(R.id.textViewInfoDiferenca);
    }

    private void fillCalcInfoForm() {
        if(!lp.getCalcDecKwh().equals("")){
            if(parseFloat(lp.getCalcDecKwh())==0){
                textViewInfoTotDec.setText("Carga Total Declarada: ---");
            }else{
                textViewInfoTotDec.setText("Carga Total Declarada: "+ lp.getCalcDecKwh() +" kWh");
            }
        }
        calculaEncontrado();
    }

    public void updateInfoLevCarga(){
        calculaEncontrado();
    }

    private void calculaDiferenca() {
        if(lp.getCalcDecKwh()!=null && lp.getCalcEncKwh()!=null){
            if(!lp.getCalcDecKwh().equals("") && !lp.getCalcEncKwh().equals("")) {
                float diferenca = parseFloat(lp.getCalcEncKwh()) - parseFloat(lp.getCalcDecKwh());
                if (diferenca > 0) {
                    textViewInfoDiferenca.setTextColor(Color.parseColor("#f44336"));
                } else {
                    textViewInfoDiferenca.setTextColor(Color.parseColor("#808080"));
                }
                textViewInfoDiferenca.setText("Diferença de consumo: "+ diferenca +" kWh");
            }
        }else{
            textViewInfoDiferenca.setText("Diferença de consumo: ---");
        }
    }

}
