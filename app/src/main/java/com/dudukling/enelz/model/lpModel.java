package com.dudukling.enelz.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class lpModel implements Serializable {
    private int id;

    private String ordem;
    private String cliente;
    private String endereco;
    private List<String> imagesList  = new ArrayList<>();



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
