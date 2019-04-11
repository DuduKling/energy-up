package com.dudukling.enelup.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dudukling.enelup.model.fiscaModel;

import java.util.ArrayList;
import java.util.List;

public class fiscalizaDAO extends SQLiteOpenHelper {
    private String TABLE_NAME = "fiscaTable";

    private String FIELD_ID = "id";
    private String FIELD_FUNCIONARIO = "funcionario";
    private String FIELD_NOME = "nome";
    private String FIELD_ENDERECO = "endereco";
    private String FIELD_BAIRRO = "bairro";
    private String FIELD_MUNICIPIO = "municipio";
    private String FIELD_CPF = "cpf";
    private String FIELD_CPF_STATUS = "cpf_status";
    private String FIELD_NIS = "nis";
    private String FIELD_RG = "rg";
    private String FIELD_DATA_NASCIMENTO = "data_nascimento";
    private String FIELD_MEDIDOR_1 = "medidor_vizinho_1";
    private String FIELD_MEDIDOR_2 = "medidor_vizinho_2";
    private String FIELD_TELEFONE = "telefone";
    private String FIELD_CELULAR = "celular";
    private String FIELD_EMAIL = "email";
    private String FIELD_LATITUDE = "latitude";
    private String FIELD_LONGITUDE = "longitude";
    private String FIELD_PRESERVACAO_AMBIENTAL = "preservacao_ambiental";
    private String FIELD_AREA_INVADIDA = "area_invadida";
    private String FIELD_TIPO_LIGACAO = "tipo_ligacao";
    private String FIELD_REDE_LOCAL = "rede_local";
    private String FIELD_AREA_MONTADO = "padrao_montado";
    private String FIELD_FAIXA_SERVIDAO = "faixa_servidao";
    private String FIELD_PRE_INDICADO = "pre_indicacao";
    private String FIELD_CPF_PRE_INDICADO = "cpf_pre_indicacao";
    private String FIELD_EXISTE_ORDEM = "existe_ordem";
    private String FIELD_ORDEM = "numero_ordem";
    private String FIELD_ESTADO_ORDEM = "estado_ordem";

    public fiscalizaDAO(Context context) {
        super(context, "fiscaTable", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "+TABLE_NAME+ "(" +
                FIELD_ID + " INTEGER PRIMARY KEY," +
                FIELD_FUNCIONARIO + " TEXT NOT NULL," +
                FIELD_NOME + " TEXT NOT NULL," +
                FIELD_ENDERECO + " TEXT NOT NULL," +
                FIELD_BAIRRO + " TEXT NOT NULL," +
                FIELD_MUNICIPIO + " TEXT NOT NULL," +
                FIELD_CPF + " TEXT NOT NULL," +
                FIELD_CPF_STATUS + " TEXT NOT NULL," +
                FIELD_NIS + " TEXT NOT NULL," +
                FIELD_RG + " TEXT NOT NULL," +
                FIELD_DATA_NASCIMENTO + " TEXT NOT NULL," +
                FIELD_MEDIDOR_1 + " TEXT NOT NULL," +
                FIELD_MEDIDOR_2 + " TEXT NOT NULL," +
                FIELD_TELEFONE + " TEXT NOT NULL," +
                FIELD_CELULAR + " TEXT NOT NULL," +
                FIELD_EMAIL + " TEXT NOT NULL," +
                FIELD_LATITUDE + " TEXT NOT NULL," +
                FIELD_LONGITUDE + " TEXT NOT NULL," +
                FIELD_PRESERVACAO_AMBIENTAL + " TEXT NOT NULL," +
                FIELD_AREA_INVADIDA + " TEXT NOT NULL," +
                FIELD_TIPO_LIGACAO + " TEXT NOT NULL," +
                FIELD_REDE_LOCAL + " TEXT NOT NULL," +
                FIELD_AREA_MONTADO + " TEXT NOT NULL," +
                FIELD_FAIXA_SERVIDAO + " TEXT NOT NULL," +
                FIELD_PRE_INDICADO + " TEXT NOT NULL," +
                FIELD_CPF_PRE_INDICADO + " TEXT NOT NULL," +
                FIELD_EXISTE_ORDEM + " TEXT NOT NULL," +
                FIELD_ORDEM + " TEXT NOT NULL," +
                FIELD_ESTADO_ORDEM + " TEXT NOT NULL" +
                ");";
        db.execSQL(sql);

        String sql2 = "CREATE TABLE fiscaImages ( " +
                "id INTEGER PRIMARY KEY, " +
                "path TEXT NOT NULL, " +
                "fiscaID INTEGER NOT NULL, " +
                "FOREIGN KEY (fiscaID) REFERENCES "+TABLE_NAME+"("+FIELD_ID+")" +
                ");";
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS lpTable";
        db.execSQL(sql);
        onCreate(db);
    }



    // PRIMARY FEATURES
    public void insert(fiscaModel fisca) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues queryData = getContentValues(fisca);
        db.insert(TABLE_NAME, null, queryData);
    }

    public void deleteImage(int fiscaID, int imageID) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(fiscaID), String.valueOf(imageID)};
        db.delete("fiscaImages","fiscaID = ? AND id = ?", params);
    }

    public void deleteImages(int fiscaID) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(fiscaID)};
        db.delete("fiscaImages","fiscaID = ?", params);
    }

    public void insertImage(fiscaModel fisca, String path) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues queryData = new ContentValues();
        queryData.put("path", path);
        queryData.put("fiscaID", fisca.getId());

        db.insert("fiscaImages", null, queryData);
    }

    public void delete(fiscaModel fisca) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(fisca.getId())};
        db.delete(TABLE_NAME, "id=?", params);
        deleteImages(fisca.getId());
    }

    public void update(fiscaModel fisca) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues queryData = getContentValues(fisca);
        String[] params = {String.valueOf(fisca.getId())};
        db.update(TABLE_NAME, queryData, "id=?", params);
    }

    public void truncateFiscalizacoes() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null,null);
        db.delete("fiscaImages", null,null);
    }


    // GETS
    public List<fiscaModel> getFiscaList() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM "+ TABLE_NAME +" ORDER BY "+ FIELD_ID +" DESC";

        Cursor c = db.rawQuery(sql, null);
        List<fiscaModel> fiscaList = new ArrayList<>();

        while (c.moveToNext()) {
            fiscaModel fisca = new fiscaModel();

            int dbFiscaList = c.getInt(c.getColumnIndex(FIELD_ID));
            fisca.setId(dbFiscaList);

            fisca.setFuncionario(c.getString(c.getColumnIndex(FIELD_FUNCIONARIO)));
            fisca.setNome(c.getString(c.getColumnIndex(FIELD_NOME)));
            fisca.setEndereco(c.getString(c.getColumnIndex(FIELD_ENDERECO)));
            fisca.setBairro(c.getString(c.getColumnIndex(FIELD_BAIRRO)));
            fisca.setMunicipio(c.getString(c.getColumnIndex(FIELD_MUNICIPIO)));
            fisca.setCpf(c.getString(c.getColumnIndex(FIELD_CPF)));
            fisca.setCpf_status(c.getString(c.getColumnIndex(FIELD_CPF_STATUS)));
            fisca.setNis(c.getString(c.getColumnIndex(FIELD_NIS)));
            fisca.setRg(c.getString(c.getColumnIndex(FIELD_RG)));
            fisca.setData_nascimento(c.getString(c.getColumnIndex(FIELD_DATA_NASCIMENTO)));
            fisca.setMedidor_vizinho_1(c.getString(c.getColumnIndex(FIELD_MEDIDOR_1)));
            fisca.setMedidor_vizinho_2(c.getString(c.getColumnIndex(FIELD_MEDIDOR_2)));
            fisca.setTelefone(c.getString(c.getColumnIndex(FIELD_TELEFONE)));
            fisca.setCelular(c.getString(c.getColumnIndex(FIELD_CELULAR)));
            fisca.setEmail(c.getString(c.getColumnIndex(FIELD_EMAIL)));
            fisca.setLatitude(c.getString(c.getColumnIndex(FIELD_LATITUDE)));
            fisca.setLongitude(c.getString(c.getColumnIndex(FIELD_LONGITUDE)));
            fisca.setPreservacao_ambiental(c.getString(c.getColumnIndex(FIELD_PRESERVACAO_AMBIENTAL)));
            fisca.setArea_invadida(c.getString(c.getColumnIndex(FIELD_AREA_INVADIDA)));
            fisca.setTipo_ligacao(c.getString(c.getColumnIndex(FIELD_TIPO_LIGACAO)));
            fisca.setRede_local(c.getString(c.getColumnIndex(FIELD_REDE_LOCAL)));
            fisca.setPadrao_montado(c.getString(c.getColumnIndex(FIELD_AREA_MONTADO)));
            fisca.setFaixa_servidao(c.getString(c.getColumnIndex(FIELD_FAIXA_SERVIDAO)));
            fisca.setPre_indicacao(c.getString(c.getColumnIndex(FIELD_PRE_INDICADO)));
            fisca.setCpf_pre_indicacao(c.getString(c.getColumnIndex(FIELD_CPF_PRE_INDICADO)));
            fisca.setExiste_ordem(c.getString(c.getColumnIndex(FIELD_EXISTE_ORDEM)));
            fisca.setNumero_ordem(c.getString(c.getColumnIndex(FIELD_ORDEM)));
            fisca.setEstado_ordem(c.getString(c.getColumnIndex(FIELD_ESTADO_ORDEM)));

            // Imagens:
            List<String> imagesList = getImagesDB(dbFiscaList);
            fisca.setImagesList(imagesList);

            fiscaList.add(fisca);
        }

        c.close();

        return fiscaList;
    }

    public List<String> getImagesDB(int dbSampleID) {
        SQLiteDatabase db = getReadableDatabase();

        List<String> imagesList = new ArrayList<>();
        String sql2 = "SELECT * FROM fiscaImages WHERE fiscaID = ?";
        Cursor c2 = db.rawQuery(sql2, new String[]{String.valueOf(dbSampleID)});
        while (c2.moveToNext()) {
            imagesList.add(c2.getString(c2.getColumnIndex("path")));
        }
        c2.close();

        return imagesList;
    }



    // HELPERS
    private ContentValues getContentValues(fiscaModel fisca) {
        ContentValues queryData = new ContentValues();

        queryData.put(FIELD_FUNCIONARIO, fisca.getFuncionario());
        queryData.put(FIELD_NOME, fisca.getNome());
        queryData.put(FIELD_ENDERECO, fisca.getEndereco());
        queryData.put(FIELD_BAIRRO, fisca.getBairro());
        queryData.put(FIELD_MUNICIPIO, fisca.getMunicipio());
        queryData.put(FIELD_CPF, fisca.getCpf());
        queryData.put(FIELD_CPF_STATUS, fisca.getCpf_status());

        queryData.put(FIELD_RG, fisca.getRg());
        queryData.put(FIELD_DATA_NASCIMENTO, fisca.getData_nascimento());
        queryData.put(FIELD_MEDIDOR_1, fisca.getMedidor_vizinho_1());

        queryData.put(FIELD_LATITUDE, fisca.getLatitude());
        queryData.put(FIELD_LONGITUDE, fisca.getLongitude());

        queryData.put(FIELD_AREA_INVADIDA, fisca.getArea_invadida());
        queryData.put(FIELD_TIPO_LIGACAO, fisca.getTipo_ligacao());
        queryData.put(FIELD_REDE_LOCAL, fisca.getRede_local());
        queryData.put(FIELD_AREA_MONTADO, fisca.getPadrao_montado());
        queryData.put(FIELD_FAIXA_SERVIDAO, fisca.getFaixa_servidao());
        queryData.put(FIELD_PRE_INDICADO, fisca.getPre_indicacao());
        queryData.put(FIELD_EXISTE_ORDEM, fisca.getExiste_ordem());
        queryData.put(FIELD_ORDEM, fisca.getNumero_ordem());
        queryData.put(FIELD_ESTADO_ORDEM, fisca.getEstado_ordem());

        if(fisca.getNis().isEmpty()){queryData.put(FIELD_NIS, "");}
        else{queryData.put(FIELD_NIS, fisca.getNis());}

        if(fisca.getMedidor_vizinho_2().isEmpty()){queryData.put(FIELD_MEDIDOR_2, "");}
        else{queryData.put(FIELD_MEDIDOR_2, fisca.getMedidor_vizinho_2());}

        if(fisca.getTelefone().isEmpty()){queryData.put(FIELD_TELEFONE, "");}
        else{queryData.put(FIELD_TELEFONE, fisca.getTelefone());}

        if(fisca.getCelular().isEmpty()){queryData.put(FIELD_CELULAR, "");}
        else{queryData.put(FIELD_CELULAR, fisca.getCelular());}

        if(fisca.getEmail().isEmpty()){queryData.put(FIELD_EMAIL, "");}
        else{queryData.put(FIELD_EMAIL, fisca.getEmail());}

        if(fisca.getPreservacao_ambiental().isEmpty()){queryData.put(FIELD_PRESERVACAO_AMBIENTAL, "");}
        else{queryData.put(FIELD_PRESERVACAO_AMBIENTAL, fisca.getPreservacao_ambiental());}


        if(fisca.getCpf_pre_indicacao().isEmpty()){queryData.put(FIELD_CPF_PRE_INDICADO, "");}
        else{queryData.put(FIELD_CPF_PRE_INDICADO, fisca.getCpf_pre_indicacao());}


        return queryData;
    }

    public int getImageIdDB(String imagePath) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT id FROM fiscaImages WHERE path = ?";
        Cursor c = db.rawQuery(sql, new String[]{imagePath});
        c.moveToFirst();
        int imageID = c.getInt(0);
        c.close();

        return imageID;
    }



}
