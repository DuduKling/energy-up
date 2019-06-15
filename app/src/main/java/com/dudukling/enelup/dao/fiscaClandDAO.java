package com.dudukling.enelup.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dudukling.enelup.adapter.fiscaCland_recyclerAdapter;
import com.dudukling.enelup.model.fiscaClandModel;

import java.util.ArrayList;
import java.util.List;

public class fiscaClandDAO extends SQLiteOpenHelper {
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
    private String FIELD_DATA_ENVIO_GOOGLE_SHEETS = "data_google_sheets";
    private String FIELD_FLAG_GOOGLE_SHEETS = "flag_google_sheets";     // '' - new, 0 - editado, 1 - enviado
    private String FIELD_SERVICO_DIRECIONADO = "servico_direcionado";
    private String FIELD_FRENTE_TRABALHO = "frente_trabalho";
    private String FIELD_CPF_OU_CNPJ = "cpf_ou_cnpj";
    private String FIELD_CNPJ = "cnpj";
    private String FIELD_ENERGIA_EMPRESTADA = "energia_emprestada";

    public fiscaClandDAO(Context context) {
        super(context, "fiscaTable", null, 3);
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
                FIELD_ESTADO_ORDEM + " TEXT NOT NULL," +
                FIELD_DATA_ENVIO_GOOGLE_SHEETS + " TEXT NOT NULL," +
                FIELD_FLAG_GOOGLE_SHEETS + " TEXT NOT NULL," +
                FIELD_SERVICO_DIRECIONADO + " TEXT NOT NULL," +
                FIELD_FRENTE_TRABALHO + " TEXT NOT NULL," +
                FIELD_CPF_OU_CNPJ + " TEXT NOT NULL," +
                FIELD_CNPJ + " TEXT NOT NULL," +
                FIELD_ENERGIA_EMPRESTADA + " TEXT NOT NULL" +
                ");";
        db.execSQL(sql);

        String sql2 = "CREATE TABLE fiscaImages ( " +
                "id INTEGER PRIMARY KEY, " +
                "path TEXT NOT NULL, " +
                "fiscaID INTEGER NOT NULL, " +
                "flag_google_sheets INTEGER NOT NULL, " +
                "FOREIGN KEY (fiscaID) REFERENCES "+TABLE_NAME+"("+FIELD_ID+")" +
                ");";
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE " +TABLE_NAME+ " ADD Column "+ FIELD_CPF_OU_CNPJ);
                db.execSQL("ALTER TABLE " +TABLE_NAME+ " ADD Column "+ FIELD_CNPJ);
            case 2:
                db.execSQL("ALTER TABLE " +TABLE_NAME+ " ADD Column "+ FIELD_ENERGIA_EMPRESTADA);
            case 3:
                //upgrade logic from version 3 to 4
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown oldVersion" + oldVersion);
        }

        //        String sql = "DROP TABLE IF EXISTS fiscaTable";
        //        db.execSQL(sql);
        //
        //        String sql2 = "DROP TABLE IF EXISTS fiscaImages";
        //        db.execSQL(sql2);
        //
        //        onCreate(db);
    }


    // PRIMARY FEATURES
    public void insert(fiscaClandModel fisca) {
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

    public void insertImage(fiscaClandModel fisca, String path) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues queryData = new ContentValues();
        queryData.put("path", path);
        queryData.put("fiscaID", fisca.getId());
        queryData.put("flag_google_sheets", 0);

        db.insert("fiscaImages", null, queryData);

        if(fisca.getFlag_google_sheets()!=null){
            if(fisca.getFlag_google_sheets().equals("1")) {
                updateToEditFiscaFlag(fisca.getId());
                fiscaCland_recyclerAdapter.fiscaList = getFiscaList();
                fiscaCland_recyclerAdapter.context2.notifyDataSetChanged();
            }
        }
    }

    public void delete(fiscaClandModel fisca) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(fisca.getId())};
        db.delete(TABLE_NAME, "id=?", params);
        deleteImages(fisca.getId());
    }

    public void update(fiscaClandModel fisca) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues queryData = getContentValues(fisca);
        String[] params = {String.valueOf(fisca.getId())};
        db.update(TABLE_NAME, queryData, "id=?", params);

        updateToEditFiscaFlag(fisca.getId());
    }

    public void truncateFiscalizacoes() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null,null);
        db.delete("fiscaImages", null,null);
    }

    public void updateToEnviadoFiscasFlag() {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues queryData = new ContentValues();queryData.put(FIELD_FLAG_GOOGLE_SHEETS, "1");

        db.update(TABLE_NAME, queryData, FIELD_FLAG_GOOGLE_SHEETS+"<>'1'", null);
    }

    private void updateToEditFiscaFlag(int id) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues queryData = new ContentValues();
        queryData.put(FIELD_FLAG_GOOGLE_SHEETS, "0");

        String[] params = {String.valueOf(id)};
        db.update(TABLE_NAME, queryData, FIELD_ID+"=?", params);
    }


    // GETS
    private List<fiscaClandModel> getAllFromFiscaTable(Cursor c) {
        List<fiscaClandModel> fiscaList = new ArrayList<>();
        while (c.moveToNext()) {
            fiscaClandModel fisca = new fiscaClandModel();

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
            fisca.setData_google_sheets(c.getString(c.getColumnIndex(FIELD_DATA_ENVIO_GOOGLE_SHEETS)));
            fisca.setFlag_google_sheets(c.getString(c.getColumnIndex(FIELD_FLAG_GOOGLE_SHEETS)));
            fisca.setServico_direcionado(c.getString(c.getColumnIndex(FIELD_SERVICO_DIRECIONADO)));
            fisca.setFrente_trabalho(c.getString(c.getColumnIndex(FIELD_FRENTE_TRABALHO)));
            fisca.setCpf_ou_cnpj(c.getString(c.getColumnIndex(FIELD_CPF_OU_CNPJ)));
            fisca.setCnpj(c.getString(c.getColumnIndex(FIELD_CNPJ)));
            fisca.setEnergia_emprestada(c.getString(c.getColumnIndex(FIELD_ENERGIA_EMPRESTADA)));

            // Imagens:
            List<String> imagesList = getImagesDB(dbFiscaList);
            fisca.setImagesList(imagesList);

            fiscaList.add(fisca);
        }

        return fiscaList;
    }

    public List<fiscaClandModel> getFiscaList() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM "+ TABLE_NAME +" ORDER BY "+ FIELD_ID +" DESC";

        Cursor c = db.rawQuery(sql, null);
        List<fiscaClandModel> fiscaList = getAllFromFiscaTable(c);
        c.close();

        return fiscaList;
    }

    public List<String> getImagesDB(int dbFiscaID) {
        SQLiteDatabase db = getReadableDatabase();

        List<String> imagesList = new ArrayList<>();
        String sql2 = "SELECT * FROM fiscaImages WHERE fiscaID = ?";
        Cursor c2 = db.rawQuery(sql2, new String[]{String.valueOf(dbFiscaID)});
        while (c2.moveToNext()) {
            imagesList.add(c2.getString(c2.getColumnIndex("path")));
        }
        c2.close();

        return imagesList;
    }

    public List<fiscaClandModel> getFiscaListNotUploadedYet() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM "+ TABLE_NAME +" WHERE "+ FIELD_FLAG_GOOGLE_SHEETS +"<>'1' ORDER BY "+ FIELD_ID +" DESC";

        Cursor c = db.rawQuery(sql, null);
        List<fiscaClandModel> fiscaList = getAllFromFiscaTable(c);
        c.close();

        return fiscaList;
    }



    // HELPERS
    private ContentValues getContentValues(fiscaClandModel fisca) {
        ContentValues queryData = new ContentValues();

        queryData.put(FIELD_FUNCIONARIO, fisca.getFuncionario());
        queryData.put(FIELD_NOME, fisca.getNome());
        queryData.put(FIELD_ENDERECO, fisca.getEndereco());
        queryData.put(FIELD_BAIRRO, fisca.getBairro());
        queryData.put(FIELD_MUNICIPIO, fisca.getMunicipio());
        queryData.put(FIELD_CPF, fisca.getCpf());
        queryData.put(FIELD_CPF_STATUS, fisca.getCpf_status());
        queryData.put(FIELD_NIS, fisca.getNis());
        queryData.put(FIELD_RG, fisca.getRg());
        queryData.put(FIELD_DATA_NASCIMENTO, fisca.getData_nascimento());
        queryData.put(FIELD_MEDIDOR_1, fisca.getMedidor_vizinho_1());
        queryData.put(FIELD_MEDIDOR_2, fisca.getMedidor_vizinho_2());
        queryData.put(FIELD_TELEFONE, fisca.getTelefone());
        queryData.put(FIELD_CELULAR, fisca.getCelular());
        queryData.put(FIELD_EMAIL, fisca.getEmail());
        queryData.put(FIELD_LATITUDE, fisca.getLatitude());
        queryData.put(FIELD_LONGITUDE, fisca.getLongitude());
        queryData.put(FIELD_PRESERVACAO_AMBIENTAL, fisca.getPreservacao_ambiental());
        queryData.put(FIELD_AREA_INVADIDA, fisca.getArea_invadida());
        queryData.put(FIELD_TIPO_LIGACAO, fisca.getTipo_ligacao());
        queryData.put(FIELD_REDE_LOCAL, fisca.getRede_local());
        queryData.put(FIELD_AREA_MONTADO, fisca.getPadrao_montado());
        queryData.put(FIELD_FAIXA_SERVIDAO, fisca.getFaixa_servidao());
        queryData.put(FIELD_PRE_INDICADO, fisca.getPre_indicacao());
        queryData.put(FIELD_CPF_PRE_INDICADO, fisca.getCpf_pre_indicacao());
        queryData.put(FIELD_EXISTE_ORDEM, fisca.getExiste_ordem());
        queryData.put(FIELD_ORDEM, fisca.getNumero_ordem());
        queryData.put(FIELD_ESTADO_ORDEM, fisca.getEstado_ordem());
        queryData.put(FIELD_DATA_ENVIO_GOOGLE_SHEETS, fisca.getData_google_sheets());
        queryData.put(FIELD_FLAG_GOOGLE_SHEETS, fisca.getFlag_google_sheets());
        queryData.put(FIELD_SERVICO_DIRECIONADO, fisca.getServico_direcionado());
        queryData.put(FIELD_FRENTE_TRABALHO, fisca.getFrente_trabalho());
        queryData.put(FIELD_CPF_OU_CNPJ, fisca.getCpf_ou_cnpj());
        queryData.put(FIELD_CNPJ, fisca.getCnpj());
        queryData.put(FIELD_ENERGIA_EMPRESTADA, fisca.getEnergia_emprestada());

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



    public boolean isNotEnviadoYet(String imagePath) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT flag_google_sheets FROM fiscaImages WHERE path = ?";
        Cursor c = db.rawQuery(sql, new String[]{imagePath});
        c.moveToFirst();
        int flagGoogle = c.getInt(0);
        c.close();

        return flagGoogle == 0;
    }

    public void updateToEnviadoImages() {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues queryData = new ContentValues();queryData.put("flag_google_sheets", 1);

        db.update("fiscaImages", queryData, "flag_google_sheets==0", null);
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
