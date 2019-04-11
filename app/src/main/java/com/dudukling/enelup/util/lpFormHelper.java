package com.dudukling.enelup.util;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
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

import com.dudukling.enelup.ligProvLevCargaActivity;
import com.dudukling.enelup.R;
import com.dudukling.enelup.dao.lpDAO;
import com.dudukling.enelup.ligProvFormActivity;
import com.dudukling.enelup.model.lpModel;
import com.dudukling.enelup.model.lpPotencia;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.sapereaude.maskedEditText.MaskedEditText;

import static java.lang.Float.parseFloat;

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
    private TextView TextViewEnderecoDaObs;

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
    private MaskedEditText maskedEditTextCalcDecTempo;
    private TextInputLayout textInputLayoutCalcDecTensao;

    private CheckBox checkBoxCalcEncExcedente;
    private CheckBox checkBoxCalcEncCorrente;
    private TextInputLayout textInputLayoutCalcEncPeriodo;
    private TextInputLayout textInputLayoutCalcEncTempo;
    private MaskedEditText maskedEditTextCalcEncTempo;
    private TextInputLayout textInputLayoutCalcEncCorrente;
    private TextInputLayout textInputLayoutCalcEncTensao;

    private CheckBox checkBoxCalcEncLevCarga;
    private FloatingActionButton floatingActionButtonCalcEncLevCarga;
    private int potenciaTotalLevCarga;
    private FloatingActionButton floatingActionButtonInfoSendEmail;


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
        TextViewEnderecoDaObs = activity.findViewById(R.id.textViewEnderecoDaObs);

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
            disableEditText(textInputLayoutCalcEncPeriodo.getEditText());
            disableEditText(textInputLayoutCalcEncTempo.getEditText());
            disableEditText(textInputLayoutCalcEncCorrente.getEditText());
            disableEditText(textInputLayoutCalcEncTensao.getEditText());

            if(lp.getCalcDecKwh()!=null && lp.getCalcEncKwh()!=null){
                if(!lp.getCalcDecKwh().equals("") && !lp.getCalcEncKwh().equals("")) {
                    float diferenca = parseFloat(lp.getCalcEncKwh()) - parseFloat(lp.getCalcDecKwh());
                    if (diferenca > 0) {
                        floatingActionButtonInfoSendEmail.setVisibility(View.VISIBLE);
                    }else{
                        floatingActionButtonInfoSendEmail.setVisibility(View.GONE);
                    }
                }
            }
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


        String text = lp.getObservacoes();
        String charr = "**";
        if(text.contains(charr)){
            String endereco1 = text.substring(text.indexOf(charr)+charr.length());
            if(endereco1.contains(charr)) {
                String endereco2 = endereco1.substring(0, endereco1.indexOf(charr));
                TextViewEnderecoDaObs.setText(endereco2);
            }else{
                TextViewEnderecoDaObs.setText("---");
            }
        }else{
            TextViewEnderecoDaObs.setText("---");
        }


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

    private void setValidateZeros(final TextInputLayout textInputCampo){
        final EditText campo = textInputCampo.getEditText();
        campo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                textInputCampo.setError(null);
                textInputCampo.setErrorEnabled(false);
                if(!hasFocus){
                    String text = campo.getText().toString();
                    if(text.equals("0.00")){
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

    private boolean fieldTimeIsValid(final TextInputLayout textInputCampo, final MaskedEditText maskedEditText) {
        textInputCampo.setError(null);
        textInputCampo.setErrorEnabled(false);
        String text = maskedEditText.getRawText();
        if(maskedEditText.getRawText().equals("")) {
            textInputCampo.setError("Campo obrigatório!");
            return true;
        }else{
            if (text.length() < 4) {
                textInputCampo.setError("Formato incorreto!");
                return true;
            }else{
                if (Integer.parseInt(text.substring(0, 2)) > 24) {
                    textInputCampo.setError("Formato da hora incorreto!");
                    return true;
                }else if (Integer.parseInt(text.substring(2)) > 59) {
                    textInputCampo.setError("Formato do minuto incorreto!");
                    return true;
                }
            }
        }
        return false;
    }

    private void setValidateTime(final TextInputLayout textInputCampo, final MaskedEditText maskedEditText){
        final EditText campo = textInputCampo.getEditText();
        campo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                textInputCampo.setError(null);
                textInputCampo.setErrorEnabled(false);
                if(!hasFocus){
                    String text = maskedEditText.getRawText();
                    if(text.length()<4){
                        textInputCampo.setError("Formato incorreto!");
                    }else if(Integer.parseInt(text.substring(0,2)) > 24){
                        textInputCampo.setError("Formato da hora incorreto!");
                    }else if(Integer.parseInt(text.substring(2)) > 59){
                        textInputCampo.setError("Formato do minuto incorreto!");
                    }
                }else{
                    textInputCampo.setError(null);
                    textInputCampo.setErrorEnabled(false);
                }
            }
        });
    }

    private boolean fieldZerosIsValid(final TextInputLayout textInputCampo){
        final EditText campo = textInputCampo.getEditText();
        textInputCampo.setError(null);
        textInputCampo.setErrorEnabled(false);
        String text = campo.getText().toString();
        if(text.equals("0.00")){
            textInputCampo.setError(REQUIRED_FIELD_ERROR_MSG);
            return true;
        }else{
            textInputCampo.setError(null);
            textInputCampo.setErrorEnabled(false);
            return false;
        }
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

        maskedEditTextCalcDecTempo = activity.findViewById(R.id.maskedEditTextCalcDecTempo);
        maskedEditTextCalcDecTempo.setKeepHint(false);
        maskedEditTextCalcDecTempo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    maskedEditTextCalcDecTempo.setKeepHint(true);
                    maskedEditTextCalcDecTempo.setHint("XXXX");
                    maskedEditTextCalcDecTempo.setMask("##:##");
                }else{
                    maskedEditTextCalcDecTempo.setKeepHint(false);
                    maskedEditTextCalcDecTempo.setHint(null);
                }
            }
        });
        maskedEditTextCalcDecTempo.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
//                if(s!=null || !String.valueOf(s).equals("")){
                    String rawTempo = maskedEditTextCalcDecTempo.getRawText();
                    if(rawTempo.length()>3){
//                        textInputLayoutCalcDecTempo.setError(null);
//                        textInputLayoutCalcDecTempo.setErrorEnabled(false)
                        lp.setCalcDecTempo(formatTimeToNumber(rawTempo));

                    }else if(!rawTempo.equals("")){
//                        textInputLayoutCalcDecTempo.setError("Formato errado!");
                        lp.setCalcDecTempo(null);
                    }
//                }else{

//                }
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

        float tempo = 0;
        if(lp.getCalcDecTempo()!=null){
            if(!lp.getCalcDecTempo().equals("")){
                tempo = Float.valueOf(lp.getCalcDecTempo());
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

//        setValidateEmpty(textInputLayoutCalcDecTempo);
        setValidateTime(textInputLayoutCalcDecTempo, maskedEditTextCalcDecTempo);
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
            if(round(parseFloat(lp.getCalcDecValor()) * parseFloat(lp.getCalcDecPeriodo()) * parseFloat(lp.getCalcDecTempo()),1) == round(parseFloat(lp.getCalcDecKwh()),1)){

                spinnerCalcDec.setSelection(2); // kW

            }else if(round((parseFloat(lp.getCalcDecValor()) * parseFloat(lp.getCalcDecPeriodo()) * parseFloat(lp.getCalcDecTempo()))/1000,1) == round(parseFloat(lp.getCalcDecKwh()),1)){

                spinnerCalcDec.setSelection(3); // Watts

            }else if(parseFloat(lp.getCalcDecValor()) == parseFloat(lp.getCalcDecKwh())){
                spinnerCalcDec.setSelection(5); // kWh
            }
        }



        textInputLayoutCalcDecValor.getEditText().setText(lp.getCalcDecValor());
        textInputLayoutCalcDecPeriodo.getEditText().setText(lp.getCalcDecPeriodo());

        maskedEditTextCalcDecTempo.setText(formatNumberToTime(lp.getCalcDecTempo()));

        textInputLayoutCalcDecTensao.getEditText().setText(lp.getCalcDecTensao());
        if(lp.getCalcDecFatorPotencia()!=null){
            if(!lp.getCalcDecFatorPotencia().equals("")){
                spinnerCalcDecFatPot.setSelection(spinnerCalcDecFatPotAdapter.getPosition(lp.getCalcDecFatorPotencia()));
            }
        }



    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
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
                Intent goToLevCarga = new Intent(activity, ligProvLevCargaActivity.class);
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
//        textInputLayoutCalcEncTempo.getEditText().addTextChangedListener(new TextWatcher() {
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(s!=null || !String.valueOf(s).equals("")){
//                    lp.setCalcEncTempo(s.toString());
//                }else{
//                    lp.setCalcEncTempo(null);
//                }
//                calculaEncontrado();
//            }
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//        });

        maskedEditTextCalcEncTempo = activity.findViewById(R.id.maskedEditTextCalcEncTempo);
        maskedEditTextCalcEncTempo.setKeepHint(false);
        maskedEditTextCalcEncTempo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    maskedEditTextCalcEncTempo.setKeepHint(true);
                    maskedEditTextCalcEncTempo.setHint("XXXX");
                    maskedEditTextCalcEncTempo.setMask("##:##");
                }else{
                    maskedEditTextCalcEncTempo.setKeepHint(false);
                    maskedEditTextCalcEncTempo.setHint(null);
                }
            }
        });
        maskedEditTextCalcEncTempo.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String rawTempo = maskedEditTextCalcEncTempo.getRawText();
                if(rawTempo.length()>3){
                    lp.setCalcEncTempo(formatTimeToNumber(rawTempo));
                }else if(!rawTempo.equals("")){
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


        final EditText editTextCalcEncCorrente = textInputLayoutCalcEncCorrente.getEditText();
        if(lp.getCalcEncCorrente()==null||lp.getCalcEncCorrente().equals("")){editTextCalcEncCorrente.setText("0.00");}
        editTextCalcEncCorrente.setSelection(editTextCalcEncCorrente.getText().length()); // After initialization keep cursor on right side
        editTextCalcEncCorrente.setCursorVisible(false);  // Disable the cursor.
        editTextCalcEncCorrente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextCalcEncCorrente.setSelection(editTextCalcEncCorrente.getText().length());
            }
        });
        editTextCalcEncCorrente.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().matches("^(\\d+)(\\.\\d{2})?$"))
                {
                    String userInput= ""+s.toString().replaceAll("[^\\d]", "");
                    StringBuilder cashAmountBuilder = new StringBuilder(userInput);

                    while (cashAmountBuilder.length() > 3 && cashAmountBuilder.charAt(0) == '0') {
                        cashAmountBuilder.deleteCharAt(0);
                    }
                    while (cashAmountBuilder.length() < 3) {
                        cashAmountBuilder.insert(0, '0');
                    }
                    cashAmountBuilder.insert(cashAmountBuilder.length()-2, '.');

                    editTextCalcEncCorrente.setText(cashAmountBuilder.toString());
                    editTextCalcEncCorrente.setSelection(cashAmountBuilder.toString().length());

                    lp.setCalcEncCorrente(s.toString());
                }else{
                    lp.setCalcEncCorrente("");
                }
                calculaEncontrado();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        final EditText editTextCalcEncTensao = textInputLayoutCalcEncTensao.getEditText();
        if(lp.getCalcEncTensao()==null||lp.getCalcEncCorrente().equals("")){editTextCalcEncTensao.setText("0.00");}
        editTextCalcEncTensao.setSelection(editTextCalcEncTensao.getText().length());
        editTextCalcEncTensao.setCursorVisible(false);
        editTextCalcEncTensao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextCalcEncTensao.setSelection(editTextCalcEncTensao.getText().length());
            }
        });
        editTextCalcEncTensao.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().matches("^(\\d+)(\\.\\d{2})?$"))
                {
                    String userInput= ""+s.toString().replaceAll("[^\\d]", "");
                    StringBuilder cashAmountBuilder = new StringBuilder(userInput);

                    while (cashAmountBuilder.length() > 3 && cashAmountBuilder.charAt(0) == '0') {
                        cashAmountBuilder.deleteCharAt(0);
                    }
                    while (cashAmountBuilder.length() < 3) {
                        cashAmountBuilder.insert(0, '0');
                    }
                    cashAmountBuilder.insert(cashAmountBuilder.length()-2, '.');

                    editTextCalcEncTensao.setText(cashAmountBuilder.toString());
                    editTextCalcEncTensao.setSelection(cashAmountBuilder.toString().length());

                    lp.setCalcEncTensao(s.toString());
                }else{
                    lp.setCalcEncTensao("");
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

        float tempo = 0;
        if(lp.getCalcDecTempo()!=null){
            if(!lp.getCalcDecTempo().equals("")){
                tempo = Float.valueOf(lp.getCalcDecTempo());
            }
        }

        float corrente = 0;
        if(lp.getCalcEncCorrente()!=null){
            if(!lp.getCalcEncCorrente().equals("")){
                corrente = Float.valueOf(lp.getCalcEncCorrente());
            }
        }

        float periodoExcedente = 0;
        if(lp.getCalcEncPeriodo()!=null){
            if(!lp.getCalcEncPeriodo().equals("")){
                periodoExcedente = Float.valueOf(lp.getCalcEncPeriodo());
            }
        }

        float tempoExcedente = 0;
        if(lp.getCalcEncTempo()!=null){
            if(!lp.getCalcEncTempo().equals("")){
                tempoExcedente = Float.valueOf(lp.getCalcEncTempo());
            }
        }

        float tensao = 0;
        if(lp.getCalcEncTensao()!=null){
            if(!lp.getCalcEncTensao().equals("")){
                tensao = Float.valueOf(lp.getCalcEncTensao());
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
        float totalLevCarga = 0;

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

//        setValidateEmpty(textInputLayoutCalcEncTempo);
        setValidateTime(textInputLayoutCalcEncTempo, maskedEditTextCalcEncTempo);
        setValidateZeros(textInputLayoutCalcEncCorrente);
        setValidateZeros(textInputLayoutCalcEncTensao);
    }

    private boolean isCalcEncValid() {
        Boolean valid = true;
        if(checkBoxCalcEncExcedente.isChecked()){
            if(fieldIsEmpty(textInputLayoutCalcEncPeriodo)){valid = false;}
//            if(fieldIsEmpty(textInputLayoutCalcEncTempo)){valid = false;}
            if(fieldTimeIsValid(textInputLayoutCalcEncTempo, maskedEditTextCalcEncTempo)){valid = false;}
        }

        if(checkBoxCalcEncCorrente.isChecked()){
            if(fieldZerosIsValid(textInputLayoutCalcEncCorrente)){valid = false;}
            if(fieldZerosIsValid(textInputLayoutCalcEncTensao)){valid = false;}
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
            maskedEditTextCalcEncTempo.setText(formatNumberToTime(lp.getCalcEncTempo()));
        }

        if(!lp.getCalcEncCorrente().equals("") && !lp.getCalcEncTensao().equals("")){
            checkBoxCalcEncCorrente.setChecked(true);

            textInputLayoutCalcEncCorrente.getEditText().setText(lp.getCalcEncCorrente());
            textInputLayoutCalcEncTensao.getEditText().setText(lp.getCalcEncTensao());

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

        floatingActionButtonInfoSendEmail = activity.findViewById(R.id.floatingActionButtonInfoSendEmail);

        floatingActionButtonInfoSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(activity, "aaaa", Toast.LENGTH_SHORT).show();
                lpDAO dao = new lpDAO(activity);
                List<String> imagesList = dao.getImagesDB(lp.getId());
                dao.close();

                Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                intent.setType("message/rfc822");
//                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"email@enel.com","email222@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Ligação Provisória (Fiscalização Gestor Enel) – Nº OS: "+lp.getOrdem());
                intent.putExtra(Intent.EXTRA_TEXT,
                        "Segue abaixo as informações da fiscalização executadas no aplicativo Enel Up:\n\n" +
                        "Carga Total Declarada: "+lp.getCalcDecKwh()+" kWh\n" +
                        "Carga Total Encontrada: "+lp.getCalcDecKwh()+" kWh\n\n" +
                        "Diferença de consumo: "+(Float.valueOf(lp.getCalcEncKwh()) - Float.valueOf(lp.getCalcDecKwh()))+" kWh\n\n\n"
                );

                File caminho = new File(activity.getExternalFilesDir(null)+"/Pictures/"+lp.getOrdem());
                File[] listaArquivos = caminho.listFiles();

                if(listaArquivos.length>0){
                    ArrayList<Uri> arrayUriList = new ArrayList<>();
                    for (File listaArquivo : listaArquivos) {
                        Uri uri = FileProvider.getUriForFile(
                                activity,
                                "com.dudukling.enelup.fileProvider",
                                listaArquivo);
                        arrayUriList.add(uri);
                    }
                    intent.putExtra(Intent.EXTRA_STREAM, arrayUriList);
                }

                try {
                    activity.startActivity(Intent.createChooser(intent, "Enviar e-mail:"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(activity, "Não há um cliente de e-mail instalado.", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    private String formatTimeToNumber(String rawTempo) {
        String minutosTempo = rawTempo.substring(2);
        float minutosReal = Float.valueOf(minutosTempo)/60;
        String tempoTotal = String.valueOf(Float.valueOf(rawTempo.substring(0,2)) + minutosReal);

        return tempoTotal;
    }
    private String formatNumberToTime(String calcDecTempo) {
        if(!calcDecTempo.equals("")){
            String tempoTotal = calcDecTempo;
            String  minutosReal = String.valueOf(Float.valueOf(tempoTotal.substring(2)) * 6);
            if(minutosReal.length()==1){minutosReal = "0"+minutosReal;}
            String horasReal = tempoTotal.substring(0, tempoTotal.indexOf('.'));
            if(horasReal.length()==1){horasReal = "0"+horasReal;}
            String tempoFormatado = horasReal+":"+ minutosReal;

            return tempoFormatado;
        }
        return "";
    }

}
