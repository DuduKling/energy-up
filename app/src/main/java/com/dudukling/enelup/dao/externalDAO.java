package com.dudukling.enelup.dao;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dudukling.enelup.adapter.fiscalizacao_recyclerAdapter;
import com.dudukling.enelup.model.fiscaModel;

import java.util.HashMap;
import java.util.Map;

public class externalDAO {
    private static final String WEB_APP_URL = "https://script.google.com/macros/s/AKfycbwCwGCQjTEV9Ey1l1MCWbXbyCgpu2E-j-mB7sfqpqCLVJxLFFdC/exec";
    private Context context;

    public externalDAO(Context context) {
        this.context = context;
    }

    public void sendFiscalizacaoClandestinoExternal(final String type, final fiscaModel fisca) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WEB_APP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("naum")){
                            Toast.makeText(context, "Registro não encontrado. Não é possível atualizar os dados..", Toast.LENGTH_LONG).show();
                        }else {
                            fiscalizaDAO dao = new fiscalizaDAO(context);
                            dao.updateFiscaDate(fisca.getId(), response);
                            fiscalizacao_recyclerAdapter.fiscaList = dao.getFiscaList();
                            fiscalizacao_recyclerAdapter.context2.notifyDataSetChanged();
                            dao.close();

//                            Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Ocorreu um erro no envio para a base do Google.", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                if(type.equals("insert")){
                    params.put("action", "insert");
                }else if(type.equals("update")){
                    params.put("action", "update");
                }

                params.put("id", String.valueOf(fisca.getId()));
                params.put("funcionario", fisca.getFuncionario()==null?"":fisca.getFuncionario());
                params.put("nome", fisca.getNome()==null?"":fisca.getNome());
                params.put("endereco", fisca.getEndereco()==null?"":fisca.getEndereco());
                params.put("bairro", fisca.getBairro()==null?"":fisca.getBairro());
                params.put("municipio", fisca.getMunicipio()==null?"":fisca.getMunicipio());
                params.put("cpf", fisca.getCpf()==null?"":fisca.getCpf());
                params.put("cpf_status", fisca.getCpf_status()==null?"":fisca.getCpf_status());
                params.put("nis", fisca.getNis()==null?"":fisca.getNis());
                params.put("rg", fisca.getRg()==null?"":fisca.getRg());
                params.put("data_nascimento", fisca.getData_nascimento()==null?"":fisca.getData_nascimento());
                params.put("medidor_visinho_1", fisca.getMedidor_vizinho_1()==null?"":fisca.getMedidor_vizinho_1());
                params.put("medidor_visinho_2", fisca.getMedidor_vizinho_2()==null?"":fisca.getMedidor_vizinho_2());
                params.put("telefone", fisca.getTelefone()==null?"":fisca.getTelefone());
                params.put("celular", fisca.getCelular()==null?"":fisca.getCelular());
                params.put("email", fisca.getEmail()==null?"":fisca.getEmail());
                params.put("latitude", fisca.getLatitude()==null?"":fisca.getLatitude());
                params.put("longitude", fisca.getLongitude()==null?"":fisca.getLongitude());
                params.put("preservacao_ambiental", fisca.getPreservacao_ambiental()==null?"":fisca.getPre_indicacao());
                params.put("area_invadida", fisca.getArea_invadida()==null?"":fisca.getArea_invadida());
                params.put("tipo_ligacao", fisca.getTipo_ligacao()==null?"":fisca.getTipo_ligacao());
                params.put("rede_local", fisca.getRede_local()==null?"":fisca.getRede_local());
                params.put("padrao_montado", fisca.getPadrao_montado()==null?"":fisca.getPadrao_montado());
                params.put("faixa_servidao", fisca.getFaixa_servidao()==null?"":fisca.getFaixa_servidao());
                params.put("pre_indicacao", fisca.getPre_indicacao()==null?"":fisca.getPre_indicacao());
                params.put("cpf_pre_indicacao", fisca.getCpf_pre_indicacao()==null?"":fisca.getCpf_pre_indicacao());
                params.put("existe_ordem", fisca.getExiste_ordem()==null?"":fisca.getExiste_ordem());
                params.put("numero_ordem", fisca.getNumero_ordem()==null?"":fisca.getNumero_ordem());
                params.put("estado_ordem", fisca.getEstado_ordem()==null?"":fisca.getEstado_ordem());

                params.put("date", fisca.getData_google_sheets()==null?"":fisca.getData_google_sheets());

                return params;
            }
        };

        int socketTimeOut = 5000;

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(context);

        queue.add(stringRequest);
    }
}
