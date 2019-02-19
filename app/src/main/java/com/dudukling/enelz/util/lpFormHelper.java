package com.dudukling.enelz.util;

import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dudukling.enelz.R;
import com.dudukling.enelz.form_ligProvActivity;
import com.dudukling.enelz.model.lpModel;

import java.util.List;

public class lpFormHelper {
    private static final String REQUIRED_FIELD_ERROR_MSG = "Campo obrigatório!";

    private static form_ligProvActivity activity;
    private final lpModel lp;

    private TextView fieldTextOrdem;
    private TextView fieldTextCliente;
    private TextView fieldTextEndereco;

    private TextInputLayout textInputObs;
    private TextInputLayout textInputCargaMedida;

    private EditText fieldObs;
    private EditText fieldCargaMedida;



    public lpFormHelper(final form_ligProvActivity activity1, String formType, lpModel lp1) {
        activity = activity1;
        lp = lp1;

        fieldTextOrdem = activity.findViewById(R.id.TextViewFormOrdem);
        fieldTextCliente = activity.findViewById(R.id.TextViewFormCliente);
        fieldTextEndereco = activity.findViewById(R.id.TextViewFormEndereco);

        setFields();

        checkTypeOfForm(formType);
    }

    private void checkTypeOfForm(String formType) {
        fillForm();

        if (formType.equals("readOnly")) {
            disableEditText(fieldObs);
            disableEditText(fieldCargaMedida);
        }
    }

    public void fillForm() {
        fieldTextOrdem.setText("Ordem: " + lp.getOrdem());
        fieldTextCliente.setText("Cliente: " + lp.getCliente());
        fieldTextEndereco.setText("Endereço: " + lp.getEndereco());
    }

    private void setFields() {
        textInputObs = activity.findViewById(R.id.TextInputObs);
        fieldObs = textInputObs.getEditText();
        setValidateEmpty(textInputObs);

        textInputCargaMedida = activity.findViewById(R.id.TextInputCargaMedida);
        fieldCargaMedida = textInputCargaMedida.getEditText();
        setValidateEmpty(textInputCargaMedida);
    }



    // HELPERS
    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setTextColor(Color.parseColor("#616161"));
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

    public boolean validateForm() {
        if(fieldIsEmpty(textInputObs)){return false;}
        if(fieldIsEmpty(textInputCargaMedida)){return false;}

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

}
