package com.dudukling.enelup.ligacoes_clandestinas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.dudukling.enelup.R;
import com.dudukling.enelup.dao.lpDAO;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

//public class clandestinoActivity extends AppCompatActivity implements clandestinoFrag.OnItemSelectedListener {
public class clandestinoActivity extends AppCompatActivity {
    private static final int EXPORT_WRITE_PERMISSION_CODE = 222;

    private FragmentManager fragmentManager;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clandestino);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("Provisórias Clandestinas");


        fragmentManager = getSupportFragmentManager();
        FragmentTransaction tx = fragmentManager.beginTransaction();
        tx.replace(R.id.fragmentClandestino, new clandestinoFrag());
        tx.commit();
     }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;

            case R.id.menu_delete:
                lpDAO dao = new lpDAO(this);
                SQLiteDatabase db = dao.getReadableDatabase();
                long qtdCSVClandest = DatabaseUtils.queryNumEntries(db, "lpClandestino");

                if (qtdCSVClandest != 0) {
                    deleteAll();
                }
                dao.close();
                break;

            case R.id.menu_export:
                checkPermissionBeforeExport();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete_export, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissionBeforeExport() {
        if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXPORT_WRITE_PERMISSION_CODE);
        } else {
//            exportImages();
            exportDB("");
        }
    }

    private void deleteAll() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        exportDB("backup");

                        lpDAO dao = new lpDAO(clandestinoActivity.this);
                        dao.truncateClandests();
                        dao.close();

                        FragmentTransaction tx = fragmentManager.beginTransaction();
                        tx.replace(R.id.fragmentClandestino, new clandestinoFrag());
                        tx.commit();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tem certeza que deseja deletar todos os Clandestinos?").setPositiveButton("Confirmar", dialogClickListener)
                .setNegativeButton("Cancelar", dialogClickListener).show();
    }

    private void exportDB(String type) {
        lpDAO dao = new lpDAO(this);

        SQLiteDatabase db = dao.getReadableDatabase();
        long qtdCSVClandest = DatabaseUtils.queryNumEntries(db, "lpClandestino");
        if (qtdCSVClandest != 0) {
            exportClandests(type, db);
        } else if (!type.equals("backup")) {
            Toast.makeText(this, "Não há clandestinos para exportar.", Toast.LENGTH_SHORT).show();
        }

        dao.close();
    }

    private void exportClandests(String type, SQLiteDatabase db) {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            boolean dirCreated = exportDir.mkdirs();
            Log.d("TAG1", "exportDB() called: " + dirCreated);
        }

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        File file;
        if (type.equals("backup")) {
            file = new File(exportDir, "EnelBackupClandestinos_" + timeStamp + ".csv");
        } else {
            file = new File(exportDir, "EnelExportClandestinos_" + timeStamp + ".csv");
        }


        try {
            boolean fileCreated = file.createNewFile();
            Log.d("TAG2", "createNewFile() called: " + fileCreated);
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            Cursor curCSV = db.rawQuery("SELECT id, NumeroOrdemLP, lpClandestino.endereco, transformador, tensao, corrente, protecao, fatorPotencia, carga, descricao, obs, lpClandestino.autoLat, lpClandestino.autoLong FROM lpClandestino ", null);

            String[] str = {"ID", "ordem", "endereco", "transformador", "tensao", "corrente", "protecao", "fator de potencia", "carga", "descricao", "obs", "latitude", "longitude"};

            csvWrite.writeNext(str);
            while (curCSV.moveToNext()) {
                String id = curCSV.getString(0);
                String ordem = curCSV.getString(1);
                String endereco = stripAccents(curCSV.getString(2));
                String transformador = stripAccents(curCSV.getString(3));
                String tensao = stripAccents(curCSV.getString(4));
                String corrente = stripAccents(curCSV.getString(5));
                String protecao = stripAccents(curCSV.getString(6));
                String fatorPotencia = stripAccents(curCSV.getString(7));
                String carga = stripAccents(curCSV.getString(8));
                String descricao = stripAccents(curCSV.getString(9));
                String obs = stripAccents(curCSV.getString(10));
                String latitude = stripAccents(curCSV.getString(11));
                String longitude = stripAccents(curCSV.getString(12));

                String arrStr[] = {id, ordem, endereco, transformador, tensao, corrente, protecao, fatorPotencia, carga, descricao, obs, latitude, longitude};

                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();

            if (!type.equals("backup")) {
                Toast.makeText(this, "Clandestinos Exportados!", Toast.LENGTH_SHORT).show();
            }

            Intent intent =
                    new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            sendBroadcast(intent);

        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXPORT_WRITE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Não é possível utilizar esta função sem permissão!", Toast.LENGTH_SHORT).show();
            } else {
                exportDB("");
            }
        }
    }

//    @Override
//    public void onItemSelected(lpClandestino clandest, int lpID, String type) {
//        FragmentTransaction tx = fragmentManager.beginTransaction();
//        tx.replace(R.id.fragmentClandestino, clandestinoFragFormNewInstance(clandest, lpID, type));
//        tx.commit();
//
//        setTitle("Ponto clandestino");
//    }

//    public static clandestinoFormActivity clandestinoFragFormNewInstance(lpClandestino clandest, int lpID, String type) {
//        clandestinoFormActivity f = new clandestinoFormActivity();
//        Bundle args = new Bundle();
//        args.putSerializable("clandest", clandest);
//        args.putSerializable("lpID", lpID);
//        args.putSerializable("type", type);
//        f.setArguments(args);
//        return f;
//    }
}
