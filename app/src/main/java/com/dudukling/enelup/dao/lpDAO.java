package com.dudukling.enelup.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dudukling.enelup.model.lpClandestinoModel;
import com.dudukling.enelup.model.lpModel;
import com.dudukling.enelup.model.lpPotenciaModel;

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
                " userObservacao TEXT NOT NULL," +
                " tipo_ordem TEXT NOT NULL," +
                " etapa TEXT NOT NULL," +
                " localidade TEXT NOT NULL," +
                " latitude TEXT NOT NULL," +
                " longitude TEXT NOT NULL," +

                " autoLat TEXT NOT NULL," +
                " autoLong TEXT NOT NULL," +

                " calcDecValor TEXT NOT NULL," +
                " calcDecFatorPotencia TEXT NOT NULL," +
                " calcDecPeriodo TEXT NOT NULL," +
                " calcDecTempo TEXT NOT NULL," +
                " calcDecTensao TEXT NOT NULL," +
                " calcDecKwh TEXT NOT NULL," +

                " calcEncPeriodo TEXT NOT NULL," +
                " calcEncTempo TEXT NOT NULL," +
                " calcEncCorrente TEXT NOT NULL," +
                " calcEncTensao TEXT NOT NULL," +
                "calcEncKwh TEXT NOT NULL" +

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
                "obs TEXT NOT NULL," +
                "autoLat TEXT NOT NULL," +
                "autoLong TEXT NOT NULL," +
                "NumeroOrdemLP INTEGER NOT NULL" +
                ");";
        db.execSQL(sql3);


        String sql4 = "CREATE TABLE lpPotencia ( " +
                "id INTEGER PRIMARY KEY, " +
                "descricao TEXT NOT NULL, " +
                "potencia TEXT NOT NULL" +
                ");";
        db.execSQL(sql4);
        fillPotenciaTable(db);

        String sql5 = "CREATE TABLE lpInterPotencia ( " +
                "id INTEGER PRIMARY KEY, " +
                "lpID INTEGER NOT NULL," +
                "quantidade TEXT NOT NULL," +
                "descricao TEXT NOT NULL, " +
                "potencia TEXT NOT NULL" +
                ");";
        db.execSQL(sql5);
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
        queryData.put("autoLat", "");
        queryData.put("autoLong", "");

        queryData.put("calcDecValor", "");
        queryData.put("calcDecFatorPotencia", "");
        queryData.put("calcDecPeriodo", "");
        queryData.put("calcDecTempo", "");
        queryData.put("calcDecTensao", "");
        queryData.put("calcDecKwh", "");

        queryData.put("calcEncPeriodo", "");
        queryData.put("calcEncTempo", "");
        queryData.put("calcEncCorrente", "");
        queryData.put("calcEncTensao", "");

        queryData.put("calcEncKwh", "");

        db.insert("lpTable", null, queryData);
    }

    public void updateLPInfo(lpModel lp) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues queryData = getContentValues(lp);
        String[] params = {String.valueOf(lp.getId())};
        db.update("lpTable", queryData, "id=?", params);
    }

    public void deleteImage(int lpID, int imageID) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(lpID), String.valueOf(imageID)};
        db.delete("lpImages","lpID = ? AND id = ?", params);
    }

    public void truncateLPs() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("lpTable", null,null);
        db.delete("lpImages", null,null);
        db.delete("lpInterPotencia", null,null);
    }

    public void truncateClandests() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("lpClandestinoModel", null,null);
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

            lp.setUserObservacao(c.getString(c.getColumnIndex("userObservacao")));

            lp.setTipoOrdem(c.getString(c.getColumnIndex("tipo_ordem")));
            lp.setEtapa(c.getString(c.getColumnIndex("etapa")));
            lp.setLocalidade(c.getString(c.getColumnIndex("localidade")));

            lp.setLatitude(c.getString(c.getColumnIndex("latitude")));
            lp.setLongitude(c.getString(c.getColumnIndex("longitude")));
            lp.setAutoLat(c.getString(c.getColumnIndex("autoLat")));
            lp.setAutoLong(c.getString(c.getColumnIndex("autoLong")));


            lp.setCalcDecValor(c.getString(c.getColumnIndex("calcDecValor")));
            lp.setCalcDecFatorPotencia(c.getString(c.getColumnIndex("calcDecFatorPotencia")));
            lp.setCalcDecPeriodo(c.getString(c.getColumnIndex("calcDecPeriodo")));
            lp.setCalcDecTempo(c.getString(c.getColumnIndex("calcDecTempo")));
            lp.setCalcDecTensao(c.getString(c.getColumnIndex("calcDecTensao")));
            lp.setCalcDecKwh(c.getString(c.getColumnIndex("calcDecKwh")));

            lp.setCalcEncPeriodo(c.getString(c.getColumnIndex("calcEncPeriodo")));
            lp.setCalcEncTempo(c.getString(c.getColumnIndex("calcEncTempo")));
            lp.setCalcEncCorrente(c.getString(c.getColumnIndex("calcEncCorrente")));
            lp.setCalcEncTensao(c.getString(c.getColumnIndex("calcEncTensao")));
            lp.setCalcEncKwh(c.getString(c.getColumnIndex("calcEncKwh")));

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

    public List<lpModel> getGPSList(int id) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM lpTable WHERE id != ? AND userObservacao==''  ORDER BY id DESC";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(id)});

        List<lpModel> lpList = new ArrayList<>();

        while (c.moveToNext()) {
            lpModel lp = new lpModel();

            lp.setOrdem(c.getString(c.getColumnIndex("ordem")));
            lp.setEtapa(c.getString(c.getColumnIndex("etapa")));

            lp.setLatitude(c.getString(c.getColumnIndex("latitude")));
            lp.setLongitude(c.getString(c.getColumnIndex("longitude")));

            lpList.add(lp);
        }

        c.close();

        return lpList;
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

        queryData.put("userObservacao", lp.getUserObservacao());

        queryData.put("tipo_ordem", lp.getTipoOrdem());
        queryData.put("etapa", lp.getEtapa());
        queryData.put("localidade", lp.getLocalidade());

        queryData.put("latitude", lp.getLatitude());
        queryData.put("longitude", lp.getLongitude());



        if(lp.getAutoLat()==null){queryData.put("autoLat", "");}
        else{queryData.put("autoLat", lp.getAutoLat());}

        if(lp.getAutoLong()==null){queryData.put("autoLong", "");}
        else{queryData.put("autoLong", lp.getAutoLong());}



        if(lp.getCalcDecValor()==null){queryData.put("calcDecValor", "");}
        else{queryData.put("calcDecValor", lp.getCalcDecValor());}

        if(lp.getCalcDecFatorPotencia()==null){queryData.put("calcDecFatorPotencia", "");}
        else{queryData.put("calcDecFatorPotencia", lp.getCalcDecFatorPotencia());}

        if(lp.getCalcDecPeriodo()==null){queryData.put("calcDecPeriodo", "");}
        else{queryData.put("calcDecPeriodo", lp.getCalcDecPeriodo());}

        if(lp.getCalcDecTensao()==null){queryData.put("calcDecTensao", "");}
        else{queryData.put("calcDecTensao", lp.getCalcDecTensao());}

        if(lp.getCalcDecTempo()==null){queryData.put("calcDecTempo", "");}
        else{queryData.put("calcDecTempo", lp.getCalcDecTempo());}

        if(lp.getCalcDecKwh()==null){queryData.put("calcDecKwh", "");}
        else{queryData.put("calcDecKwh", lp.getCalcDecKwh());}



        if(lp.getCalcEncPeriodo()==null){queryData.put("calcEncPeriodo", "");}
        else{queryData.put("calcEncPeriodo", lp.getCalcEncPeriodo());}

        if(lp.getCalcEncTempo()==null){queryData.put("calcEncTempo", "");}
        else{queryData.put("calcEncTempo", lp.getCalcEncTempo());}

        if(lp.getCalcEncCorrente()==null){queryData.put("calcEncCorrente", "");}
        else{queryData.put("calcEncCorrente", lp.getCalcEncCorrente());}

        if(lp.getCalcEncTensao()==null){queryData.put("calcEncTensao", "");}
        else{queryData.put("calcEncTensao", lp.getCalcEncTensao());}

        if(lp.getCalcEncKwh()==null){queryData.put("calcEncKwh", "");}
        else{queryData.put("calcEncKwh", lp.getCalcEncKwh());}

        return queryData;
    }

    public int lastLPID(){
        String sql = "SELECT MAX(id) AS LAST FROM lpTable";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int ID = c.getInt(0);
        c.close();

        return ID;
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
    public void insertClandestino(lpClandestinoModel clandest, String LPOrdem) {
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
        queryData.put("obs", clandest.getObs());

        if(clandest.getAutoLat()==null){queryData.put("autoLat", "");}
        else{queryData.put("autoLat", clandest.getAutoLat());}

        if(clandest.getAutoLong()==null){queryData.put("autoLong", "");}
        else{queryData.put("autoLong", clandest.getAutoLong());}

        queryData.put("NumeroOrdemLP", LPOrdem);

        db.insert("lpClandestinoModel", null, queryData);
    }

    public List<lpClandestinoModel> getClandestinoList() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT " +
                "id as idClandest, " +
                "endereco as enderecoClandest, " +
                "transformador as transformadorClandest, " +
                "tensao as tensaoClandest, " +
                "corrente as correnteClandest, " +
                "protecao as protecaoClandest, " +
                "fatorPotencia as fpClandest, " +
                "carga as cargaClandest, " +
                "descricao as descricaoClandest, " +
                "obs as obsClandest, " +
                "NumeroOrdemLP as ordemLP, " +
                "autoLat as latitude, " +
                "autoLong as longitude " +
                "FROM lpClandestino " +
                "ORDER BY idClandest DESC";

        Cursor c = db.rawQuery(sql, null);
        List<lpClandestinoModel> lpClandestList = new ArrayList<>();

        while (c.moveToNext()) {
            lpClandestinoModel clandest = new lpClandestinoModel();

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
            clandest.setObs(c.getString(c.getColumnIndex("obsClandest")));

            clandest.setOrdem(c.getString(c.getColumnIndex("ordemLP")));

            clandest.setAutoLat(c.getString(c.getColumnIndex("latitude")));
            clandest.setAutoLong(c.getString(c.getColumnIndex("longitude")));


            lpClandestList.add(clandest);
        }

        c.close();

        return lpClandestList;
    }

    public void deleteClandestino(lpClandestinoModel clandest) {
            SQLiteDatabase db = getWritableDatabase();
            String[] params = {String.valueOf(clandest.getId())};
            db.delete("lpClandestinoModel","id = ?", params);
    }

    public void updateClandestino(lpClandestinoModel clandest) {
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
        queryData.put("obs", clandest.getObs());

        if(clandest.getAutoLat()==null){queryData.put("autoLat", "");}
        else{queryData.put("autoLat", clandest.getAutoLat());}

        if(clandest.getAutoLong()==null){queryData.put("autoLong", "");}
        else{queryData.put("autoLong", clandest.getAutoLong());}

        String[] params = {String.valueOf(clandest.getId())};
        db.update("lpClandestinoModel", queryData, "id=?", params);
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


    // POTENCIA
    private void fillPotenciaTable(SQLiteDatabase db){
        db.execSQL("INSERT INTO lpPotencia VALUES ('1','OUTROS','-')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('2','ALARME DE SEGURANÇA','150')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('3','APARELHO DE ELETROCARDIOGRAMA','2000')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('4','APARELHO DE RAIO X','1020')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('5','APARELHO DE SOM','120')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('6','APARELHO DE TESTE ERGOMÉTRICO','1900')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('7','APARELHO ECOCARDIOGRAMA','660')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('8','APLAINADEIRA (SERRARIA)','5520')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('9','AR CONDICIONADO 7.000 BTU','1128')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('10','AR CONDICIONADO 8.000 BTUS','1289')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('11','AR CONDICIONADO 9.000 BTUS','1450')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('12','AR CONDICIONADO 10.000 BTUS','1650')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('13','AR CONDICIONADO 11.000 BTUS','1705')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('14','AR CONDICIONADO 12.000 BTUS','1900')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('15','AR CONDICIONADO 14.000 BTUS','1950')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('16','AR CONDICIONADO 15.000 BTUS','2475')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('17','AR CONDICIONADO 21.000 BTUS','3100')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('18','AR CONDICIONADO 30.000 BTUS','4651')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('19','ASPIRADOR DE PÓ','1000')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('20','BALANÇA ELÉTRICA','150')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('21','BALCÃO VITRINE 1000 W','1000')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('22','BALCÃO VITRINE 1800 W','1800')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('23','BALCÃO VITRINE 368 W','368')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('24','BANHEIRA COM AQUECEDOR','1540')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('25','BANHEIRA COM HIDROMASSAGEM','1200')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('26','BANHO MARIA (PUDIM)','2500')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('27','BETONEIRA','2208')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('28','BOILER 50 e 60 L','1500')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('29','BOILER 100 L','2030')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('30','BOILER 200 a 500 L','3000')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('31','BOMBA DE COMBUSTÍVEL','1104')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('32','BOMBA DE INFUSSÃO','1815')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('33','CAÇA NIQUEL','200')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('34','CADEIRA ODONTOLÓGICA','100')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('35','CAFETEIRA','1000')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('36','CAIXA 24 HORAS - BANCOS','2200')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('37','CÂMARA','2208')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('38','CAMPAINHA','100')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('39','CARREGADOR DE CELULAR','50')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('40','CARRO DE ANESTESIA','5000')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('41','CARRO DE EMERGÊNCIA','2360')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('42','CENTRIFUGA INDUSTRIAL','3680')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('43','CHOPEIRA','2650')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('44','CHURRASQUEIRA','3800')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('45','CHUVEIRO ELÉTRICO 4400 W','4400')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('46','CHUVEIRO ELÉTRICO 5500 W','5500')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('47','CHUVEIRO ELÉTRICO 6500 W','6500')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('48','CILINDRO (PADARIA)','736')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('49','COMPRESSOR 10 CV','7360')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('50','CORTADOR DE GRAMA','500')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('51','CORTADOR DE GRAMA GRANDE','1140')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('52','CORTADOR DE GRAMA PEQUENO','500')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('53','DEBULHADOR DE MILHO','2208')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('54','DESTILADOR','4000')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('55','DVD PLAYER','50')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('56','ESPREMEDOR DE FRUTAS','80')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('57','ESTABILIZADOR','3000')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('58','ESTEIRA ELÉTRICA','746')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('59','ESTERELIZADOR','200')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('60','ESTUFA','350')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('61','ESTUFA (PINTURA DE CARRO)','2208')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('62','EXAUSTOR PARA FOGÃO','170')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('63','EXAUSTOR PAREDE','110')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('64','FACA ELÉTRICA','220')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('65','FATIADOR DE FRIOS','245')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('66','FERRO ELÉTRICO','1000')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('67','FILTRO DE PISCINA','800')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('68','FOGÃO ELÉTRICO','90')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('69','FOGÃO ELÉTRICO 4 CHAPAS','9120')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('70','FORNO À RESISTÊNCIA GRANDE','1500')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('71','FORNO À RESISTÊNCIA PEQUENO','800')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('72','FORRAGEIRA','7360')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('73','FREEZER BALCAO','1472')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('74','FREEZER VERTICAL','220')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('75','FREEZER VITRINE','600')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('76','FRIGOBAR','70')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('77','FRITADEIRA','1200')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('78','FURADEIRA','350')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('79','GELADEIRA 1 PORTA','90')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('80','GELADEIRA 2 PORTAS','130')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('81','IMPRESSORA','100')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('82','IMPRESSORA COMERCIAL','1340')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('83','IMPRESSORA PARA BALANÇA','220')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('84','LIQUIDIFICADOR','350')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('85','LIXADEIRA','2500')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('86','MACENEIRA','2208')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('87','MÁQ. DE CALCULAR','50')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('88','MÁQ. DE CARTÃO DE CREDITO','25')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('89','MAQ. DE LAVAR LOUÇAS','1350')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('90','MÁQ. DE LAVAR ROUPA C/ AQUECEDOR','1800')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('91','MÁQ. DE LAVAR ROUPA S/ AQUECEDOR','600')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('92','MÁQ. DE LAVAR ROUPA INDÚSTRIAL','7360')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('93','MÁQ. DE REFRIGERANTE','2650')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('94','MÁQ. DE SOLDA','5000')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('95','MAQUITA','250')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('96','MASSEIRA (PADARIA)','2800')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('97','MESA TÉRMICA (SERIGRAFIA)','8800')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('98','MICROCOMPUTADOR (CPU)','300')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('99','MICROCOMPUTADOR (MONITOR)','300')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('100','MICROONDAS','1200')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('101','MODEM DE INTERNET','12')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('102','MOEDOR DE CANA','2208')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('103','MOTOR BOMBA DE 1/4 CV','184')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('104','MOTOR BOMBA DE 1/3 CV','245')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('105','MOTOR BOMBA DE 1/2 CV','368')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('106','MOTOR BOMBA DE 1 CV','736')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('107','MOTOR BOMBA DE 2 CV','1472')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('108','MOTOR BOMBA DE 3 CV','2238')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('109','MOTOR BOMBA DE 4 CV','2944')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('110','MOTOR BOMBA DE 5 CV','3680')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('111','MOTOR BOMBA DE 7,5 CV','5520')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('112','MOTOR DE VENTILAÇÃO','5152')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('113','NOBREAK','6000')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('114','POLIMERISSADERA (SERIGRAFIA)','3960')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('115','RADIO','40')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('116','RETIFICADOR DE POTÊNCIA','9000')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('117','SAUNA','5000')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('118','SCANNER','100')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('119','SECADOR DE CABELO GRANDE','1400')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('120','SECADOR DE CABELOS PEQUENO','600')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('121','SECADORA DE ROUPA','2000')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('122','SECADORA INDÚSTRIAL','2208')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('123','SERRA ELÉTRICA','240')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('124','TELEFONE SEM FIO','100')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('125','TELEVISOR LCD 32','120')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('126','TELEVISOR LCD 40','170')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('127','TELEVISOR LCD 47','230')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('128','TELEVISOR LED 32','70')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('129','TELEVISOR LED 39','100')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('130','TELEVISOR LED 46','140')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('131','TELEVISOR LED 55','190')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('132','TORNEIRA ELÉTRICA','3500')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('133','TORNO (SERRARIA)','184')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('134','TORNO MECÂNICO','736')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('135','TORRADEIRA','1500')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('136','TUPIA (SERRARIA)','3680')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('137','VENTILADOR DE TETO','200')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('138','VENTILADOR GRANDE','150')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('139','VENTILADOR PEQUENO','65')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('140','VÍDEO CASSETE','18')");
        db.execSQL("INSERT INTO lpPotencia VALUES ('141','VIDEO GAME','15')");

    }

    public List<lpPotenciaModel> getLPPotenciaList(int lpID){
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM lpInterPotencia WHERE lpID = ?";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(lpID)});

        List<lpPotenciaModel> lpPotList = new ArrayList<>();

        while (c.moveToNext()){
            lpPotenciaModel lp = new lpPotenciaModel();

            lp.setId(c.getInt(c.getColumnIndex("id")));
            lp.setQuantidade(c.getString(c.getColumnIndex("quantidade")));
            lp.setDescricao(c.getString(c.getColumnIndex("descricao")));
            lp.setPotencia(c.getString(c.getColumnIndex("potencia")));


            lpPotList.add(lp);
        }
        c.close();

        return lpPotList;
    }

    public void deletePotencia(int oPotenciaID){
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(oPotenciaID)};
        db.delete("lpInterPotencia", "id=?", params);
    }

    public List<String> getDescList() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT descricao FROM lpPotencia";
        Cursor c = db.rawQuery(sql, null);

        List<String> descList = new ArrayList<>();

        while (c.moveToNext()){
            String aString = c.getString(c.getColumnIndex("descricao"));

            descList.add(aString);
        }
        c.close();

        return descList;
    }

    public String getPotenciaFromItemID(int position) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT potencia FROM lpPotencia WHERE id = ?";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(position+1)});

        String aString = "";
        while (c.moveToNext()){
            aString = c.getString(c.getColumnIndex("potencia"));
        }
        c.close();

        return aString;
    }

    public void insertPotencia(lpPotenciaModel lpPot, int lpID) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues queryData = new ContentValues();

        queryData.put("lpID", lpID);
        queryData.put("quantidade", lpPot.getQuantidade());
        queryData.put("descricao", lpPot.getDescricao());
        queryData.put("potencia", lpPot.getPotencia());

        db.insert("lpInterPotencia", null, queryData);
    }

    public lpPotenciaModel getPotInfo(int i) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM lpPotencia WHERE id=?";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(i)});

        lpPotenciaModel pot = new lpPotenciaModel();
        while (c.moveToNext()){
            pot.setDescricao(c.getString(c.getColumnIndex("descricao")));
            pot.setPotencia(c.getString(c.getColumnIndex("potencia")));
        }
        c.close();

        return pot;
    }

    public int getLPPotenciaTotal(int id) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT SUM(potencia * quantidade) as potenciaTotal FROM lpInterPotencia WHERE lpID=?";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(id)});

        int potencia = 0;

        while (c.moveToNext()) {
            if (!c.isNull(c.getColumnIndex("potenciaTotal"))) {
                potencia = Integer.parseInt(c.getString(c.getColumnIndex("potenciaTotal")));
            }
        }
        c.close();

        return potencia;
    }
}
