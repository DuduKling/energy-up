package com.dudukling.enelz.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dudukling.enelz.model.lpClandestino;
import com.dudukling.enelz.model.lpModel;

import java.util.ArrayList;
import java.util.List;

public class lpDAO extends SQLiteOpenHelper {
    public lpDAO(Context context) {
        super(context, "lpTable", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE lpTable (" +
                " id INTEGER PRIMARY KEY," +
                " ordem TEXT NOT NULL," +
                " cliente TEXT NOT NULL," +
                " endereco TEXT NOT NULL," +
                " bairro TEXT NOT NULL," +
                " numero_cliente TEXT NOT NULL," +
                " descricao_etapa TEXT NOT NULL," +
                " observacoes TEXT NOT NULL," +
                " descricao_retorno TEXT NOT NULL," +
                " observacao_exe TEXT NOT NULL," +
                " tempo_max_servico TEXT NOT NULL," +
                " perc_tempo_maximo TEXT NOT NULL," +
                " userObservacao TEXT NOT NULL," +
                " userCargaMedida TEXT NOT NULL," +
                " tipo_ordem TEXT NOT NULL," +
                " etapa TEXT NOT NULL," +
                " localidade TEXT NOT NULL" +
                ");";
        db.execSQL(sql);

        String sql2 = "CREATE TABLE lpImages ( " +
                "id INTEGER PRIMARY KEY, " +
                "path TEXT NOT NULL, " +
                "lpID INTEGER NOT NULL, " +
                "FOREIGN KEY (lpID) REFERENCES lpTable(id)" +
                ");";
        db.execSQL(sql2);

        String sql3 = "CREATE TABLE lpClandestino ( " +
                "id INTEGER PRIMARY KEY, " +
                "endereco TEXT NOT NULL, " +
                "transformador TEXT NOT NULL," +
                "tensao TEXT NOT NULL," +
                "corrente TEXT NOT NULL," +
                "protecao TEXT NOT NULL," +
                "fatorPotencia TEXT NOT NULL," +
                "carga TEXT NOT NULL," +
                "descricao TEXT NOT NULL," +
                "lpID INTEGER NOT NULL, " +
                "FOREIGN KEY (lpID) REFERENCES lpTable(id)" +
                ");";
        db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS lpTable";
        db.execSQL(sql);
        onCreate(db);
    }


    // PRIMARY FEATURES
    public void insert(lpModel lp) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues queryData = getContentValues(lp);

        queryData.put("userObservacao", "");
        queryData.put("userCargaMedida", "");

        db.insert("lpTable", null, queryData);

        // Add pictures:
//        ContentValues queryData2 = new ContentValues();
//        List<String> images = lp.getImagesList();
//        int lastId = lastID();
//
//        for(int i=0; i <= (images.size() - 1); i++){
//            queryData2.clear();
//            queryData2.put("path", images.get(i));
//            queryData2.put("lpID", lastId);
//            db.insert("lpImages", null, queryData2);
//        }
    }

    public void delete(lpModel lp) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(lp.getId())};
        db.delete("lpTable","id = ?", params);
        db.delete("lpImages","collectionID = ?", params);
    }

    public void updateLPInfo(lpModel lp) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues queryData = getContentValues(lp);
        String[] params = {String.valueOf(lp.getId())};
        db.update("lpTable", queryData, "id=?", params);


//        int lpId = lp.getId();
//        deleteAllImagesFromDB(lpId);

//        // Add all pictures:
//        ContentValues queryData2 = new ContentValues();
//        List<String> images = lp.getImagesList();
//
//        for(int i=0; i <= (images.size() - 1); i++){
//            queryData2.clear();
//            queryData2.put("path", images.get(i));
//            queryData2.put("lpID", lpId);
//            db.insert("Images", null, queryData2);
//        }
    }

    public void deleteImage(int lpID, int imageID) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(lpID), String.valueOf(imageID)};
        db.delete("lpImages","lpID = ? AND id = ?", params);
    }

    public void truncateDBs() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("lpTable", null,null);
        db.delete("lpImages", null,null);
        db.delete("lpClandestino", null,null);
    }

    public void insertImage(lpModel lp, String path) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues queryData = new ContentValues();
        queryData.put("path", path);
        queryData.put("lpID", lp.getId());

        db.insert("lpImages", null, queryData);
    }


    // GETS
    public List<lpModel> getLPList() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM lpTable ORDER BY id DESC";

        Cursor c = db.rawQuery(sql, null);
        List<lpModel> lpList = new ArrayList<>();

        while (c.moveToNext()) {
            lpModel lp = new lpModel();

            int dbLPID = c.getInt(c.getColumnIndex("id"));
            lp.setId(dbLPID);

            lp.setOrdem(c.getString(c.getColumnIndex("ordem")));
            lp.setCliente(c.getString(c.getColumnIndex("cliente")));
            lp.setEndereco(c.getString(c.getColumnIndex("endereco")));

            lp.setNumero_cliente(c.getString(c.getColumnIndex("numero_cliente")));
            lp.setBairro(c.getString(c.getColumnIndex("bairro")));
            lp.setDescricao_etapa(c.getString(c.getColumnIndex("descricao_etapa")));
            lp.setObservacoes(c.getString(c.getColumnIndex("observacoes")));
            lp.setDescricao_retorno(c.getString(c.getColumnIndex("descricao_retorno")));
            lp.setObservacao_exe(c.getString(c.getColumnIndex("observacao_exe")));
            lp.setTempo_max_servico(c.getString(c.getColumnIndex("tempo_max_servico")));
            lp.setPerc_tempo_maximo(c.getString(c.getColumnIndex("perc_tempo_maximo")));

            lp.setUserObservacao(c.getString(c.getColumnIndex("userObservacao")));
            lp.setUserCargaMedida(c.getString(c.getColumnIndex("userCargaMedida")));

            lp.setTipoOrdem(c.getString(c.getColumnIndex("tipo_ordem")));
            lp.setEtapa(c.getString(c.getColumnIndex("etapa")));
            lp.setLocalidade(c.getString(c.getColumnIndex("localidade")));


            // Imagens:
            List<String> imagesList = getImagesDB(dbLPID);
            lp.setImagesList(imagesList);

            lpList.add(lp);
        }

        c.close();

        return lpList;
    }

    public List<String> getImagesDB(int dbSampleID) {
        SQLiteDatabase db = getReadableDatabase();

        List<String> imagesList = new ArrayList<>();
        String sql2 = "SELECT * FROM lpImages WHERE lpID = ?";
        Cursor c2 = db.rawQuery(sql2, new String[]{String.valueOf(dbSampleID)});
        while (c2.moveToNext()) {
            imagesList.add(c2.getString(c2.getColumnIndex("path")));
        }
        c2.close();

        return imagesList;
    }





    // HELPERS
    private ContentValues getContentValues(lpModel lp) {
        ContentValues queryData = new ContentValues();

        queryData.put("ordem", lp.getOrdem());
        queryData.put("cliente", lp.getCliente());
        queryData.put("endereco", lp.getEndereco());

        queryData.put("numero_cliente", lp.getNumero_cliente());
        queryData.put("bairro", lp.getBairro());
        queryData.put("descricao_etapa", lp.getDescricao_etapa());
        queryData.put("observacoes", lp.getObservacoes());
        queryData.put("descricao_retorno", lp.getDescricao_retorno());
        queryData.put("observacao_exe", lp.getObservacao_exe());
        queryData.put("tempo_max_servico", lp.getTempo_max_servico());
        queryData.put("perc_tempo_maximo", lp.getPerc_tempo_maximo());

        queryData.put("userObservacao", lp.getUserObservacao());
        queryData.put("userCargaMedida", lp.getUserCargaMedida());

        queryData.put("tipo_ordem", lp.getTipoOrdem());
        queryData.put("etapa", lp.getEtapa());
        queryData.put("localidade", lp.getLocalidade());

        return queryData;
    }

    public int lastID(){
        String sql = "SELECT MAX(id) AS LAST FROM lpTable";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int ID = c.getInt(0);
        c.close();

        return ID;
    }

    private void deleteAllImagesFromDB(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(id)};
        db.delete("lpImages","lpID = ?", params);
    }

    public int getImageIdDB(String imagePath) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT id FROM lpImages WHERE path = ?";
        Cursor c = db.rawQuery(sql, new String[]{imagePath});
        c.moveToFirst();
        int imageID = c.getInt(0);
        c.close();

        return imageID;
    }


    // CLANDESTINO
    public void insertClandestino(lpClandestino clandest, int idLP) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues queryData = new ContentValues();

        queryData.put("endereco", clandest.getEndereco());
        queryData.put("transformador", clandest.getTransformador());
        queryData.put("tensao", clandest.getTensao());
        queryData.put("corrente", clandest.getCorrente());
        queryData.put("protecao", clandest.getProtecao());
        queryData.put("fatorPotencia", clandest.getFatorPotencia());
        queryData.put("carga", clandest.getCarga());
        queryData.put("descricao", clandest.getDescricao());

        queryData.put("lpID", idLP);

        db.insert("lpClandestino", null, queryData);
    }

    public List<lpClandestino> getClandestinoList() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT lpClandestino.id as idClandest, lpClandestino.endereco as enderecoClandest, lpClandestino.transformador as transformadorClandest, lpClandestino.tensao as tensaoClandest, " +
                "lpClandestino.corrente as correnteClandest, lpClandestino.protecao as protecaoClandest, " +
                "lpClandestino.fatorPotencia as fpClandest, lpClandestino.carga as cargaClandest, lpClandestino.descricao as descricaoClandest, lpTable.ordem  as ordemLP FROM lpClandestino " +
                "INNER JOIN lpTable ON lpClandestino.lpID=lpTable.id ORDER BY idClandest DESC";

        Cursor c = db.rawQuery(sql, null);
        List<lpClandestino> lpClandestList = new ArrayList<>();

        while (c.moveToNext()) {
            lpClandestino clandest = new lpClandestino();

            int dbLPID = c.getInt(c.getColumnIndex("idClandest"));
            clandest.setId(dbLPID);

            clandest.setEndereco(c.getString(c.getColumnIndex("enderecoClandest")));
            clandest.setTransformador(c.getString(c.getColumnIndex("transformadorClandest")));
            clandest.setTensao(c.getString(c.getColumnIndex("tensaoClandest")));
            clandest.setCorrente(c.getString(c.getColumnIndex("correnteClandest")));
            clandest.setProtecao(c.getString(c.getColumnIndex("protecaoClandest")));
            clandest.setFatorPotencia(c.getString(c.getColumnIndex("fpClandest")));
            clandest.setCarga(c.getString(c.getColumnIndex("cargaClandest")));
            clandest.setDescricao(c.getString(c.getColumnIndex("descricaoClandest")));

            clandest.setOrdem(c.getString(c.getColumnIndex("ordemLP")));


            lpClandestList.add(clandest);
        }

        c.close();

        return lpClandestList;
    }

    public void deleteClandestino(lpClandestino clandest) {
            SQLiteDatabase db = getWritableDatabase();
            String[] params = {String.valueOf(clandest.getId())};
            db.delete("lpClandestino","id = ?", params);
    }

    public void updateClandestino(lpClandestino clandest) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues queryData = new ContentValues();

        queryData.put("endereco", clandest.getEndereco());
        queryData.put("transformador", clandest.getTransformador());
        queryData.put("tensao", clandest.getTensao());
        queryData.put("corrente", clandest.getCorrente());
        queryData.put("protecao", clandest.getProtecao());
        queryData.put("fatorPotencia", clandest.getFatorPotencia());
        queryData.put("carga", clandest.getCarga());
        queryData.put("descricao", clandest.getDescricao());

        String[] params = {String.valueOf(clandest.getId())};
        db.update("lpClandestino", queryData, "id=?", params);
    }

    public int getClandestinoLastID() {
        String sql = "SELECT MAX(id) AS LAST FROM lpClandestino";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int ID = c.getInt(0);
        c.close();

        return ID;
    }
}
