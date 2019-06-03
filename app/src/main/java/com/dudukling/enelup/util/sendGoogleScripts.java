package com.dudukling.enelup.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dudukling.enelup.adapter.fiscaCland_recyclerAdapter;
import com.dudukling.enelup.dao.fiscaClandDAO;
import com.dudukling.enelup.model.fiscaClandModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sendGoogleScripts {
    private static final String WEB_APP_URL = "https://script.google.com/macros/s/AKfycbyWVi9aHnPc0o3rpVYa6f3qIAIQcmhufXtuie9xCrYsU0OPhs8/exec";
    private Context context;

    public sendGoogleScripts(Context context) {
        this.context = context;
    }

    public void sendFiscalizacaoClandestinoExternal(List<fiscaClandModel> fiscaList, final ProgressDialog dialog) {

        // Preparar JSON com o fiscaList
        JSONObject finalJSON = new JSONObject();
        JSONArray jsonArr = new JSONArray();
        try {
            for (fiscaClandModel list : fiscaList) {
                JSONObject fiscaJSON = new JSONObject();

                fiscaJSON.put("id", list.getId());
                fiscaJSON.put("funcionario", list.getFuncionario());
                fiscaJSON.put("nome", list.getNome());
                fiscaJSON.put("endereco", list.getEndereco());
                fiscaJSON.put("bairro", list.getBairro());
                fiscaJSON.put("municipio", list.getMunicipio());
                fiscaJSON.put("cpf", list.getCpf());
                fiscaJSON.put("cpf_status", list.getCpf_status());
                fiscaJSON.put("cnpj", list.getCnpj());
                fiscaJSON.put("nis", list.getNis());
                fiscaJSON.put("rg", list.getRg());
                fiscaJSON.put("data_nascimento", list.getData_nascimento());
                fiscaJSON.put("medidor_vizinho_1", list.getMedidor_vizinho_1());
                fiscaJSON.put("medidor_vizinho_2", list.getMedidor_vizinho_2());
                fiscaJSON.put("telefone", list.getTelefone());
                fiscaJSON.put("celular", list.getCelular());
                fiscaJSON.put("email", list.getEmail());
                fiscaJSON.put("latitude", list.getLatitude());
                fiscaJSON.put("longitude", list.getLongitude());
                fiscaJSON.put("preservacao_ambiental", list.getPreservacao_ambiental());
                fiscaJSON.put("area_invadida", list.getArea_invadida());
                fiscaJSON.put("tipo_ligacao", list.getTipo_ligacao());
                fiscaJSON.put("rede_local", list.getRede_local());
                fiscaJSON.put("padrao_montado", list.getPadrao_montado());
                fiscaJSON.put("faixa_servidao", list.getFaixa_servidao());
                fiscaJSON.put("pre_indicacao", list.getPre_indicacao());
                fiscaJSON.put("cpf_pre_indicacao", list.getCpf_pre_indicacao());
                fiscaJSON.put("existe_ordem", list.getExiste_ordem());
                fiscaJSON.put("numero_ordem", list.getNumero_ordem());
                fiscaJSON.put("estado_ordem", list.getEstado_ordem());
                fiscaJSON.put("servico_direcionado", list.getServico_direcionado());
                fiscaJSON.put("frente_trabalho", list.getFrente_trabalho());

                if(list.getImagesList().size() > 0){
                    JSONArray imagesArrJSON = new JSONArray();
                    for (String imagePath : list.getImagesList()){
                        fiscaClandDAO dao = new fiscaClandDAO(context);
                        if(dao.isNotEnviadoYet(imagePath)){
                            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                            Bitmap rbitmap = getResizedBitmap(bitmap, 1500);    // maxSize de 1500 eh aproximadamente 1 MB..
                            String userImage = getStringImage(rbitmap);
                            imagesArrJSON.put(userImage);
                        }
                    }
                    fiscaJSON.put("imagens", imagesArrJSON);
                }

                jsonArr.put(fiscaJSON);
            }
            finalJSON.put("valores", jsonArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("POW", String.valueOf(finalJSON));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, WEB_APP_URL, finalJSON,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    String resp = "";
                    try {
                        resp = response.getString("result");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(resp.equals("foi")){
                        fiscaClandDAO dao = new fiscaClandDAO(context);
                        dao.updateToEnviadoFiscasFlag();
                        dao.updateToEnviadoImages();
                        fiscaCland_recyclerAdapter.fiscaList = dao.getFiscaList();
                        fiscaCland_recyclerAdapter.context2.notifyDataSetChanged();
                        dao.close();

                        dialog.dismiss();
                        Toast.makeText(context, "Enviado com sucesso!", Toast.LENGTH_LONG).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(context, "Ocorreu um erro: TimeoutError ou NoConnectionError", Toast.LENGTH_LONG).show();
                    }else if(error instanceof AuthFailureError) {
                        Toast.makeText(context, "Ocorreu um erro: AuthFailureError", Toast.LENGTH_LONG).show();
                    }else if(error instanceof ServerError) {
                        Toast.makeText(context, "Ocorreu um erro: ServerError", Toast.LENGTH_LONG).show();
                    }else if(error instanceof NetworkError) {
                        Toast.makeText(context, "Ocorreu um erro: NetworkError", Toast.LENGTH_LONG).show();
                    }else if(error instanceof ParseError) {
                        Toast.makeText(context, "Ocorreu um erro: ParseError", Toast.LENGTH_LONG).show();
                    }

                    dialog.dismiss();
                }
            }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("action", "insert"); // Nao eh utilizado no momento..

                return params;
            }
        };

        int socketTimeOut = 500000;

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(context);

        queue.add(jsonObjReq);
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);

    }

    private String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }
}
