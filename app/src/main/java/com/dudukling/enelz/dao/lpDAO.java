package com.dudukling.enelz.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                " endereco TEXT NOT NULL" +
                ");";
        db.execSQL(sql);

        String sql2 = "CREATE TABLE lpImages ( " +
                "id INTEGER PRIMARY KEY, " +
                "path TEXT NOT NULL, " +
                "lpID INTEGER NOT NULL, " +
                "FOREIGN KEY (lpID) REFERENCES lpTable(id)" +
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
    public void insert(lpModel lp) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues queryData = getContentValues(lp);
        db.insert("lpTable", null, queryData);

        // Add pictures:
        ContentValues queryData2 = new ContentValues();
        List<String> images = lp.getImagesList();
        int lastId = lastID();

        for(int i=0; i <= (images.size() - 1); i++){
            queryData2.clear();
            queryData2.put("path", images.get(i));
            queryData2.put("lpID", lastId);
            db.insert("lpImages", null, queryData2);
        }
    }

    public void delete(lpModel lp) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(lp.getId())};
        db.delete("lpTable","id = ?", params);
        db.delete("lpImages","collectionID = ?", params);
    }

    public void edit(lpModel lp) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues queryData = getContentValues(lp);
        String[] params = {String.valueOf(lp.getId())};
        db.update("lpTable", queryData, "id=?", params);

        int lpId = lp.getId();
        deleteAllImagesFromDB(lpId);

        // Add all pictures:
        ContentValues queryData2 = new ContentValues();
        List<String> images = lp.getImagesList();

        for(int i=0; i <= (images.size() - 1); i++){
            queryData2.clear();
            queryData2.put("path", images.get(i));
            queryData2.put("lpID", lpId);
            db.insert("Images", null, queryData2);
        }
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
    }



    // GETS
    public List<lpModel> getSamples() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM lpTable ORDER BY id DESC";

        Cursor c = db.rawQuery(sql, null);
        List<lpModel> lpList = new ArrayList<>();

        while (c.moveToNext()) {
            lpModel lp = new lpModel();

            int dbSampleID = c.getInt(c.getColumnIndex("id"));
            lp.setId(dbSampleID);

            lp.setOrdem(c.getString(c.getColumnIndex("ordem")));
            lp.setCliente(c.getString(c.getColumnIndex("cliente")));
            lp.setEndereco(c.getString(c.getColumnIndex("endereco")));

            // Imagens:
            List<String> imagesList = getImagesDB(dbSampleID);
            lp.setImagesList(imagesList);

            lpList.add(lp);
        }

        c.close();

        return lpList;
    }

    public List<String> getImagesDB(int dbSampleID) {
        SQLiteDatabase db = getReadableDatabase();

        List<String> imagesList = new ArrayList<>();
        String sql2 = "SELECT * FROM lpImages WHERE collectionID = ?";
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

        return queryData;
    }

    public int lastID(){
        String sql = "SELECT MAX(id) AS LAST FROM lpImages";
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

}
