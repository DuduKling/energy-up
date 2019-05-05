package com.dudukling.enelup.dao;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.dudukling.enelup.adapter.fiscalizacao_recyclerAdapter;
import com.dudukling.enelup.model.fiscaModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class externalDAO {
    private static final String WEB_APP_URL = "https://script.google.com/macros/s/AKfycbwCwGCQjTEV9Ey1l1MCWbXbyCgpu2E-j-mB7sfqpqCLVJxLFFdC/exec";
    private Context context;

    public externalDAO(Context context) {
        this.context = context;
    }

    public void sendFiscalizacaoClandestinoExternal(final String type, List<fiscaModel> fiscaList, final ProgressDialog dialog) {

        // Preparar JSON com o fiscaList
        JSONObject finalJSON = new JSONObject();
        JSONArray jsonArr = new JSONArray();
        try {
            for (fiscaModel list : fiscaList) {
                JSONObject fiscaJSON = new JSONObject();

                fiscaJSON.put("id", list.getId());
                fiscaJSON.put("funcionario", list.getFuncionario());
                fiscaJSON.put("nome", list.getNome());
                fiscaJSON.put("endereco", list.getEndereco());
                fiscaJSON.put("bairro", list.getBairro());
                fiscaJSON.put("municipio", list.getMunicipio());
                fiscaJSON.put("cpf", list.getCpf());
                fiscaJSON.put("cpf_status", list.getCpf_status());
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

                jsonArr.put(fiscaJSON);
            }
            finalJSON.put("valores", jsonArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("PORRA", String.valueOf(finalJSON));

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
                        fiscalizaDAO dao = new fiscalizaDAO(context);
                        dao.updateToEnviadoFiscasFlag();
                        fiscalizacao_recyclerAdapter.fiscaList = dao.getFiscaList();
                        fiscalizacao_recyclerAdapter.context2.notifyDataSetChanged();
                        dao.close();

                        dialog.dismiss();
                        Toast.makeText(context, "Enviado com sucesso!!", Toast.LENGTH_LONG).show();
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

                params.put("action", "insert");

                return params;
            }
        };

        int socketTimeOut = 5000;

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(context);

        queue.add(jsonObjReq);
    }
}
