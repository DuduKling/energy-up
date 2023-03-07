package com.dudukling.energyup.model;

import java.io.Serializable;

public class ligClandModel implements Serializable {

    private int id;

    private String endereco = "";
    private String transformador = "";
    private String tensao = "";
    private String corrente = "";
    private String protecao = "";
    private String fatorPotencia = "";
    private String carga = "";
    private String descricao = "";
    private String obs = "";

    private String Ordem = "";

    private String autoLat = "";
    private String autoLong = "";


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTransformador() {
        return transformador;
    }
    public void setTransformador(String transformador) {
        this.transformador = transformador;
    }

    public String getTensao() {
        return tensao;
    }
    public void setTensao(String tensao) {
        this.tensao = tensao;
    }

    public String getCorrente() {
        return corrente;
    }
    public void setCorrente(String corrente) {
        this.corrente = corrente;
    }

    public String getProtecao() {
        return protecao;
    }
    public void setProtecao(String protecao) {
        this.protecao = protecao;
    }

    public String getFatorPotencia() {
        return fatorPotencia;
    }
    public void setFatorPotencia(String fatorPotencia) {
        this.fatorPotencia = fatorPotencia;
    }

    public String getCarga() {
        return carga;
    }
    public void setCarga(String carga) {
        this.carga = carga;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getObs() {
        return obs;
    }
    public void setObs(String obs) {
        this.obs = obs;
    }




    public String getOrdem() {
        return Ordem;
    }
    public void setOrdem(String ordem) {
        Ordem = ordem;
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
}
