package com.dudukling.energyup.model;

import java.io.Serializable;

public class fiscaIPModel implements Serializable {
    private int id;
    private String funcionario = "";

    private String municipio = "";
    private String endereco = "";
    private String bairro = "";

    private String latitude = "";
    private String longitude = "";

    private String area_risco = "";
    private String acesa_24h = "";
    private String tipo_luminaria = "";
    private String potencia = "";
    private String observacao = "";
    private String horario = "";
    private String plaqueta_prefeitura = "";
    private String area = "";
    private String quebrada = "";


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getFuncionario() {
        return funcionario;
    }
    public void setFuncionario(String funcionario) {
        this.funcionario = funcionario;
    }

    public String getMunicipio() {
        return municipio;
    }
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }
    public void setBairro(String bairro) {
        this.bairro = bairro;
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

    public String getArea_risco() {
        return area_risco;
    }
    public void setArea_risco(String area_risco) {
        this.area_risco = area_risco;
    }

    public String getAcesa_24h() {
        return acesa_24h;
    }
    public void setAcesa_24h(String acesa_24h) {
        this.acesa_24h = acesa_24h;
    }

    public String getTipo_luminaria() {
        return tipo_luminaria;
    }
    public void setTipo_luminaria(String tipo_luminaria) {
        this.tipo_luminaria = tipo_luminaria;
    }

    public String getPotencia() {
        return potencia;
    }
    public void setPotencia(String potencia) {
        this.potencia = potencia;
    }

    public String getObservacao() {
        return observacao;
    }
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getHorario() {
        return horario;
    }
    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getPlaqueta_prefeitura() {
        return plaqueta_prefeitura;
    }
    public void setPlaqueta_prefeitura(String plaqueta_prefeitura) {
        this.plaqueta_prefeitura = plaqueta_prefeitura;
    }

    public String getArea() {
        return area;
    }
    public void setArea(String area) {
        this.area = area;
    }

    public String getQuebrada() {
        return quebrada;
    }
    public void setQuebrada(String quebrada) {
        this.quebrada = quebrada;
    }
}
