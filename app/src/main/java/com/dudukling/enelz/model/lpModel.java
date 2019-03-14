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

    private List<String> imagesList  = new ArrayList<>();


    private String userObservacao;
    private String userCargaMedida;

    private String tipoOrdem;
    private String etapa;
    private String localidade;

    private String latitude;
    private String longitude;

    private String autoLat;
    private String autoLong;


    private String calcDecValor;
    private String calcDecFatorPotencia;
    private String calcDecPeriodo;
    private String calcDecTempo;
    private String calcDecTensao;
    private String calcDecKwh;


    private String calcEncPeriodo;
    private String calcEncTempo;
    private String calcEncCorrente;
    private String calcEncTensao;
    private String calcEncKwh;



    public String getTipoOrdem() {
        return tipoOrdem;
    }
    public void setTipoOrdem(String tipoOrdem) {
        this.tipoOrdem = tipoOrdem;
    }

    public String getEtapa() {
        return etapa;
    }
    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public String getLocalidade() {
        return localidade;
    }
    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

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

    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAutoLat() {
        return autoLat;
    }
    public void setAutoLat(String autoLat) {
        this.autoLat = autoLat;
    }

    public String getAutoLong() {
        return autoLong;
    }
    public void setAutoLong(String autoLong) {
        this.autoLong = autoLong;
    }


    public String getCalcDecValor() {
        return calcDecValor;
    }
    public void setCalcDecValor(String calcDecValor) {
        this.calcDecValor = calcDecValor;
    }

    public String getCalcDecFatorPotencia() {
        return calcDecFatorPotencia;
    }
    public void setCalcDecFatorPotencia(String calcDecFatorPotencia) {
        this.calcDecFatorPotencia = calcDecFatorPotencia;
    }

    public String getCalcDecPeriodo() {
        return calcDecPeriodo;
    }
    public void setCalcDecPeriodo(String calcDecPeriodo) {
        this.calcDecPeriodo = calcDecPeriodo;
    }

    public String getCalcDecTempo() {
        return calcDecTempo;
    }
    public void setCalcDecTempo(String calcDecTempo) {
        this.calcDecTempo = calcDecTempo;
    }

    public String getCalcDecTensao() {
        return calcDecTensao;
    }
    public void setCalcDecTensao(String calcDecTensao) {
        this.calcDecTensao = calcDecTensao;
    }

    public String getCalcDecKwh() {
        return calcDecKwh;
    }
    public void setCalcDecKwh(String calcDecKwh) {
        this.calcDecKwh = calcDecKwh;
    }


    public String getCalcEncPeriodo() {
        return calcEncPeriodo;
    }
    public void setCalcEncPeriodo(String calcEncPeriodo) {
        this.calcEncPeriodo = calcEncPeriodo;
    }

    public String getCalcEncTempo() {
        return calcEncTempo;
    }
    public void setCalcEncTempo(String calcEncTempo) {
        this.calcEncTempo = calcEncTempo;
    }

    public String getCalcEncCorrente() {
        return calcEncCorrente;
    }
    public void setCalcEncCorrente(String calcEncCorrente) {
        this.calcEncCorrente = calcEncCorrente;
    }


    public String getCalcEncTensao() {
        return calcEncTensao;
    }
    public void setCalcEncTensao(String calcEncTensao) {
        this.calcEncTensao = calcEncTensao;
    }


    public String getCalcEncKwh() {
        return calcEncKwh;
    }
    public void setCalcEncKwh(String calcEncKwh) {
        this.calcEncKwh = calcEncKwh;
    }
}
