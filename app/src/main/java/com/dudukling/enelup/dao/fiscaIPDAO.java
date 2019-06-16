package com.dudukling.enelup.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dudukling.enelup.model.fiscaIPModel;

import java.util.ArrayList;
import java.util.List;

public class fiscaIPDAO extends SQLiteOpenHelper {
    private String TABLE_NAME = "fiscaIPTable";

    private String FIELD_ID = "id";
    private String FIELD_FUNCIONARIO = "funcionario";
    private String FIELD_MUNICIPIO = "municipio";
    private String FIELD_ENDERECO = "endereco";
    private String FIELD_BAIRRO = "bairro";
    private String FIELD_LATITUDE = "latitude";
    private String FIELD_LONGITUDE = "longitude";
    private String FIELD_AREA_RISCO = "area_risco";
    private String FIELD_ACESA24h = "acesa_24h";
    private String FIELD_TIPO_LUMINARIA = "tipo_luminaria";
    private String FIELD_POTENCIA = "potencia";
    private String FIELD_OBSERVACAO = "observacao";
    private String FIELD_HORARIO = "horario";
    private String FIELD_PLAQUETA = "plaqueta_prefeitura";
    private String FIELD_AREA = "area_urbano_rural";
    private String FIELD_QUEBRADA = "quebrada";


    public fiscaIPDAO(Context context) {
        super(context, "fiscaIPTable", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "+TABLE_NAME+ "(" +
                FIELD_ID + " INTEGER PRIMARY KEY," +
                FIELD_FUNCIONARIO + " TEXT NOT NULL," +
                FIELD_ENDERECO + " TEXT NOT NULL," +
                FIELD_BAIRRO + " TEXT NOT NULL," +
                FIELD_MUNICIPIO + " TEXT NOT NULL," +
                FIELD_LATITUDE + " TEXT NOT NULL," +
                FIELD_LONGITUDE + " TEXT NOT NULL," +
                FIELD_AREA_RISCO + " TEXT NOT NULL," +
                FIELD_ACESA24h + " TEXT NOT NULL," +
                FIELD_TIPO_LUMINARIA + " TEXT NOT NULL," +
                FIELD_POTENCIA + " TEXT NOT NULL," +
                FIELD_OBSERVACAO + " TEXT NOT NULL," +
                FIELD_HORARIO + " TEXT NOT NULL," +
                FIELD_PLAQUETA + " TEXT NOT NULL," +
                FIELD_AREA + " TEXT NOT NULL," +
                FIELD_QUEBRADA + " TEXT NOT NULL" +
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion) {
            case 1:
                //upgrade logic from version 1 to 2
            case 2:
                //upgrade logic from version 2 to 3
            case 3:
                //upgrade logic from version 3 to 4
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown oldVersion" + oldVersion);
        }
    }


    // PRIMARY FEATURES
    public void insert(fiscaIPModel fisca) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues queryData = getContentValues(fisca);
        db.insert(TABLE_NAME, null, queryData);
    }

    public void delete(fiscaIPModel fisca) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(fisca.getId())};
        db.delete(TABLE_NAME, FIELD_ID+"=?", params);
    }

    public void update(fiscaIPModel fisca) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues queryData = getContentValues(fisca);
        String[] params = {String.valueOf(fisca.getId())};
        db.update(TABLE_NAME, queryData, FIELD_ID+"=?", params);
    }

    public void truncateFiscalizacoes() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null,null);
    }



    // GETS
    private List<fiscaIPModel> getAllFromFiscaIPTable(Cursor c) {
        List<fiscaIPModel> fiscaList = new ArrayList<>();
        while (c.moveToNext()) {
            fiscaIPModel fisca = new fiscaIPModel();

            int dbFiscaList = c.getInt(c.getColumnIndex(FIELD_ID));
            fisca.setId(dbFiscaList);

            fisca.setFuncionario(c.getString(c.getColumnIndex(FIELD_FUNCIONARIO)));
            fisca.setEndereco(c.getString(c.getColumnIndex(FIELD_ENDERECO)));
            fisca.setBairro(c.getString(c.getColumnIndex(FIELD_BAIRRO)));
            fisca.setMunicipio(c.getString(c.getColumnIndex(FIELD_MUNICIPIO)));
            fisca.setLatitude(c.getString(c.getColumnIndex(FIELD_LATITUDE)));
            fisca.setLongitude(c.getString(c.getColumnIndex(FIELD_LONGITUDE)));
            fisca.setArea_risco(c.getString(c.getColumnIndex(FIELD_AREA_RISCO)));
            fisca.setAcesa_24h(c.getString(c.getColumnIndex(FIELD_ACESA24h)));
            fisca.setTipo_luminaria(c.getString(c.getColumnIndex(FIELD_TIPO_LUMINARIA)));
            fisca.setPotencia(c.getString(c.getColumnIndex(FIELD_POTENCIA)));
            fisca.setObservacao(c.getString(c.getColumnIndex(FIELD_OBSERVACAO)));
            fisca.setHorario(c.getString(c.getColumnIndex(FIELD_HORARIO)));
            fisca.setPlaqueta_prefeitura(c.getString(c.getColumnIndex(FIELD_PLAQUETA)));
            fisca.setArea(c.getString(c.getColumnIndex(FIELD_AREA)));
            fisca.setQuebrada(c.getString(c.getColumnIndex(FIELD_QUEBRADA)));

            fiscaList.add(fisca);
        }

        return fiscaList;
    }

    public List<fiscaIPModel> getFiscaList() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM "+ TABLE_NAME +" ORDER BY "+ FIELD_ID +" DESC";

        Cursor c = db.rawQuery(sql, null);
        List<fiscaIPModel> fiscaList = getAllFromFiscaIPTable(c);
        c.close();

        return fiscaList;
    }



    // HELPERS
    private ContentValues getContentValues(fiscaIPModel fisca) {
        ContentValues queryData = new ContentValues();

        queryData.put(FIELD_FUNCIONARIO, fisca.getFuncionario());
        queryData.put(FIELD_ENDERECO, fisca.getEndereco());
        queryData.put(FIELD_BAIRRO, fisca.getBairro());
        queryData.put(FIELD_MUNICIPIO, fisca.getMunicipio());
        queryData.put(FIELD_LATITUDE, fisca.getLatitude());
        queryData.put(FIELD_LONGITUDE, fisca.getLongitude());
        queryData.put(FIELD_AREA_RISCO, fisca.getArea_risco());
        queryData.put(FIELD_ACESA24h, fisca.getAcesa_24h());
        queryData.put(FIELD_TIPO_LUMINARIA, fisca.getTipo_luminaria());
        queryData.put(FIELD_POTENCIA, fisca.getPotencia());
        queryData.put(FIELD_OBSERVACAO, fisca.getObservacao());
        queryData.put(FIELD_HORARIO, fisca.getHorario());
        queryData.put(FIELD_PLAQUETA, fisca.getPlaqueta_prefeitura());
        queryData.put(FIELD_AREA, fisca.getArea());
        queryData.put(FIELD_QUEBRADA, fisca.getQuebrada());

        return queryData;
    }

    public int getNextID() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT "+ FIELD_ID +" FROM "+ TABLE_NAME +" ORDER BY "+ FIELD_ID +" DESC LIMIT 1";

        Cursor c = db.rawQuery(sql, null);

        int id = 0;
        while (c.moveToNext()) {
            id = c.getInt(c.getColumnIndex(FIELD_ID));
        }

        c.close();
        return id+1;
    }
}
