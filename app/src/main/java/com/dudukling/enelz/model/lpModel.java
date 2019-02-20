package com.dudukling.enelz.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class lpModel implements Serializable {
    private int id;

    private String ordem;
    private String cliente;
    private String endereco;

    private String numero_cliente;
    private String bairro;
    private String descricao_etapa;
    private String observacoes;
    private String descricao_retorno;
    private String observacao_exe;
    private String tempo_max_servico;
    private String perc_tempo_maximo;

    private List<String> imagesList  = new ArrayList<>();


    private String userObservacao;
    private String userCargaMedida;





    public String getUserObservacao() {
        return userObservacao;
    }
    public void setUserObservacao(String userObservacao) {
        this.userObservacao = userObservacao;
    }

    public String getUserCargaMedida() {
        return userCargaMedida;
    }
    public void setUserCargaMedida(String userCargaMedida) {
        this.userCargaMedida = userCargaMedida;
    }

    public String getNumero_cliente() {
        return numero_cliente;
    }
    public void setNumero_cliente(String numero_cliente) {
        this.numero_cliente = numero_cliente;
    }

    public String getBairro() {
        return bairro;
    }
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getDescricao_etapa() {
        return descricao_etapa;
    }
    public void setDescricao_etapa(String descricao_etapa) {
        this.descricao_etapa = descricao_etapa;
    }

    public String getObservacoes() {
        return observacoes;
    }
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getDescricao_retorno() {
        return descricao_retorno;
    }
    public void setDescricao_retorno(String descricao_retorno) {
        this.descricao_retorno = descricao_retorno;
    }

    public String getObservacao_exe() {
        return observacao_exe;
    }
    public void setObservacao_exe(String observacao_exe) {
        this.observacao_exe = observacao_exe;
    }

    public String getTempo_max_servico() {
        return tempo_max_servico;
    }
    public void setTempo_max_servico(String tempo_max_servico) {
        this.tempo_max_servico = tempo_max_servico;
    }

    public String getPerc_tempo_maximo() {
        return perc_tempo_maximo;
    }
    public void setPerc_tempo_maximo(String perc_tempo_maximo) {
        this.perc_tempo_maximo = perc_tempo_maximo;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getOrdem() {
        return ordem;
    }
    public void setOrdem(String ordem) {
        this.ordem = ordem;
    }

    public String getCliente() {
        return cliente;
    }
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public List<String> getImagesList() {
        return imagesList;
    }
    public void setImagesList(List<String> imagesList) {
        this.imagesList = imagesList;
    }
}
