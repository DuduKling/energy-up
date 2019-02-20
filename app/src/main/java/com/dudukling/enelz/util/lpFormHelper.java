package com.dudukling.enelz.util;

import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dudukling.enelz.R;
import com.dudukling.enelz.dao.lpDAO;
import com.dudukling.enelz.form_ligProvActivity;
import com.dudukling.enelz.model.lpModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class lpFormHelper {
    private static final String REQUIRED_FIELD_ERROR_MSG = "Campo obrigatório!";

    private static form_ligProvActivity activity;
    private final lpModel lp;

    private TextView TextViewOrdem;
    private TextView TextViewCliente;
    private TextView TextViewEndereco;
    private TextView TextViewNumeroCliente;
    private TextView TextViewBairro;
    private TextView TextViewDescEtapa;
    private TextView TextViewObservacoes;
    private TextView TextViewRetorno;
    private TextView TextViewObservacaoExe;
    private TextView TextViewTempoMaxServico;
    private TextView TextViewPercTempoMaximo;

    private TextInputLayout textInputObs;
    private TextInputLayout textInputCargaMedida;

    private EditText fieldObs;
    private EditText fieldCargaMedida;


    public lpFormHelper(final form_ligProvActivity activity1, String formType, lpModel lp1) {
        activity = activity1;
        lp = lp1;

        TextViewOrdem = activity.findViewById(R.id.TextViewFormOrdem);
        TextViewCliente = activity.findViewById(R.id.TextViewFormCliente);
        TextViewEndereco = activity.findViewById(R.id.TextViewFormEndereco);

        TextViewNumeroCliente = activity.findViewById(R.id.TextViewnNumeroCliente);
        TextViewBairro = activity.findViewById(R.id.TextViewFormBairro);
        TextViewDescEtapa = activity.findViewById(R.id.TextViewFormDescEtapa);
        TextViewObservacoes = activity.findViewById(R.id.TextViewFormObservacoes);
        TextViewRetorno = activity.findViewById(R.id.TextViewFormDescRetorno);
        TextViewObservacaoExe = activity.findViewById(R.id.TextViewFormObservacaoExe);
        TextViewTempoMaxServico = activity.findViewById(R.id.TextViewFormTempoMaxServico);
        TextViewPercTempoMaximo = activity.findViewById(R.id.TextViewFormPercTempoMaximo);

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
        TextViewTempoMaxServico.setText(lp.getTempo_max_servico());
        TextViewPercTempoMaximo.setText(lp.getPerc_tempo_maximo());

        fieldObs.setText(lp.getUserObservacao());
        fieldCargaMedida.setText(lp.getUserCargaMedida());
    }

    private void setFields() {
        textInputObs = activity.findViewById(R.id.TextInputObs);
        fieldObs = textInputObs.getEditText();
        setValidateEmpty(textInputObs);

        textInputCargaMedida = activity.findViewById(R.id.TextInputCargaMedida);
        fieldCargaMedida = textInputCargaMedida.getEditText();
        setValidateEmpty(textInputCargaMedida);
    }

    public lpModel getLPFromForm(lpModel lp, List<String> imagesList) {

        lp.setUserCargaMedida(fieldCargaMedida.getText().toString());
        lp.setUserObservacao(fieldObs.getText().toString());

//        lpDAO dao = new lpDAO(activity);
//        List<String> imagesListDB = dao.getImagesDB(lp.getId());
//        dao.close();
//
//        List<String> newestImageList = new ArrayList<>();
//        newestImageList.addAll(imagesListDB);
//        newestImageList.addAll(imagesList);
//
//        lp.setImagesList(newestImageList);

        //Toast.makeText(activity,""+formActivity.imagesList,Toast.LENGTH_LONG).show();

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
