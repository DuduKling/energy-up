package com.dudukling.enelup.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class fiscaModel implements Serializable {
    private int id;
    private String funcionario;
    private String nome;
    private String endereco;
    private String bairro;
    private String municipio;
    private String cpf;
    private String cpf_status;
    private String nis;
    private String rg;
    private String data_nascimento;
    private String medidor_vizinho_1;
    private String medidor_vizinho_2;
    private String telefone;
    private String celular;
    private String email;
    private String latitude;
    private String longitude;
    private String preservacao_ambiental;
    private String area_invadida;
    private String tipo_ligacao;
    private String rede_local;
    private String padrao_montado;
    private String faixa_servidao;
    private String pre_indicacao;
    private String cpf_pre_indicacao;
    private String existe_ordem;
    private String numero_ordem;
    private String estado_ordem;

    private List<String> imagesList  = new ArrayList<>();


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

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
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

    public String getMunicipio() {
        return municipio;
    }
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCpf_status() {
        return cpf_status;
    }
    public void setCpf_status(String cpf_status) {
        this.cpf_status = cpf_status;
    }

    public String getNis() {
        return nis;
    }
    public void setNis(String nis) {
        this.nis = nis;
    }

    public String getRg() {
        return rg;
    }
    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getData_nascimento() {
        return data_nascimento;
    }
    public void setData_nascimento(String data_nascimento) {
        this.data_nascimento = data_nascimento;
    }

    public String getMedidor_vizinho_1() {
        return medidor_vizinho_1;
    }
    public void setMedidor_vizinho_1(String medidor_vizinho_1) {
        this.medidor_vizinho_1 = medidor_vizinho_1;
    }

    public String getMedidor_vizinho_2() {
        return medidor_vizinho_2;
    }
    public void setMedidor_vizinho_2(String medidor_vizinho_2) {
        this.medidor_vizinho_2 = medidor_vizinho_2;
    }

    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCelular() {
        return celular;
    }
    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
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

    public String getPreservacao_ambiental() {
        return preservacao_ambiental;
    }
    public void setPreservacao_ambiental(String preservacao_ambiental) {
        this.preservacao_ambiental = preservacao_ambiental;
    }

    public String getArea_invadida() {
        return area_invadida;
    }
    public void setArea_invadida(String area_invadida) {
        this.area_invadida = area_invadida;
    }

    public String getTipo_ligacao() {
        return tipo_ligacao;
    }
    public void setTipo_ligacao(String tipo_ligacao) {
        this.tipo_ligacao = tipo_ligacao;
    }

    public String getRede_local() {
        return rede_local;
    }
    public void setRede_local(String rede_local) {
        this.rede_local = rede_local;
    }

    public String getPadrao_montado() {
        return padrao_montado;
    }
    public void setPadrao_montado(String padrao_montado) {
        this.padrao_montado = padrao_montado;
    }

    public String getFaixa_servidao() {
        return faixa_servidao;
    }
    public void setFaixa_servidao(String faixa_servidao) {
        this.faixa_servidao = faixa_servidao;
    }

    public String getPre_indicacao() {
        return pre_indicacao;
    }
    public void setPre_indicacao(String pre_indicacao) {
        this.pre_indicacao = pre_indicacao;
    }

    public String getCpf_pre_indicacao() {
        return cpf_pre_indicacao;
    }
    public void setCpf_pre_indicacao(String cpf_pre_indicacao) {
        this.cpf_pre_indicacao = cpf_pre_indicacao;
    }

    public String getNumero_ordem() {
        return numero_ordem;
    }
    public void setNumero_ordem(String numero_ordem) {
        this.numero_ordem = numero_ordem;
    }

    public String getEstado_ordem() {
        return estado_ordem;
    }
    public void setEstado_ordem(String estado_ordem) {
        this.estado_ordem = estado_ordem;
    }

    public List<String> getImagesList() {
        return imagesList;
    }
    public void setImagesList(List<String> imagesList) {
        this.imagesList = imagesList;
    }

    public String getExiste_ordem() {
        return existe_ordem;
    }
    public void setExiste_ordem(String existe_ordem) {
        this.existe_ordem = existe_ordem;
    }
}
