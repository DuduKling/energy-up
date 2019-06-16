package com.dudukling.enelup.fiscalizacao_ilumunacao_publica;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dudukling.enelup.R;
import com.dudukling.enelup.adapter.fiscaIP_recyclerAdapter;
import com.dudukling.enelup.dao.fiscaIPDAO;
import com.dudukling.enelup.model.fiscaIPModel;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class fiscaIPActivity extends AppCompatActivity {
    private static final int EXPORT_WRITE_PERMISSION_CODE = 222;

    private RecyclerView recyclerView;
    private TextView textViewNoRecord;
    private List<fiscaIPModel> fiscaList;
    private fiscaIP_recyclerAdapter RecyclerAdapter;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("Iluminação Pública");
        setContentView(R.layout.fisca_ip_activity_main);

        FloatingActionButton floatingActionButtonFiscaIPAdd = this.findViewById(R.id.floatingActionButtonFiscaIPAdd);
        floatingActionButtonFiscaIPAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fiscaIPModel fisca = new fiscaIPModel();
                Intent goToFormActivity = new Intent(fiscaIPActivity.this, fiscaIPFormActivity.class);
                goToFormActivity
                        .putExtra("fisca", fisca)
                        .putExtra("type", "new");
                fiscaIPActivity.this.startActivity(goToFormActivity);
            }
        });


        recyclerView = findViewById(R.id.recyclerViewFiscaIP);
        registerForContextMenu(recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menu_delete:
                if (fiscaList != null) {
                    if (!fiscaList.isEmpty()) {
                        deleteAll();
                    }
                }
                break;

            case R.id.menu_export:
                checkPermissionBeforeExport();
                break;

            case R.id.menu_upload:
//                uploadToCloud();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        fiscaIPDAO dao = new fiscaIPDAO(this);
        textViewNoRecord = this.findViewById(R.id.textViewFiscaIPNoRecords);

        fiscaList = dao.getFiscaList();
        if (fiscaList.size() > 0) {
            RecyclerAdapter = new fiscaIP_recyclerAdapter(this);
            recyclerView.setAdapter(RecyclerAdapter);
            RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layout);

            textViewNoRecord.setVisibility(View.GONE);
        }
        dao.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete_export_upload, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXPORT_WRITE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Não é possível utilizar esta função sem permissão!", Toast.LENGTH_SHORT).show();
            } else {
                exportDB("");
            }
        }
    }



    // DELETE
    private void deleteAll() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        exportDB("backup");

                        fiscaIPDAO dao = new fiscaIPDAO(fiscaIPActivity.this);
                        dao.truncateFiscalizacoes();
                        dao.close();

                        fiscaList.clear();
                        RecyclerAdapter = new fiscaIP_recyclerAdapter(fiscaIPActivity.this);
                        recyclerView.setAdapter(RecyclerAdapter);

                        textViewNoRecord.setVisibility(View.VISIBLE);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tem certeza que deseja deletar todas as Fiscalizações?").setPositiveButton("Confirmar", dialogClickListener)
                .setNegativeButton("Cancelar", dialogClickListener).show();
    }



    // HELPERS
    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }



    // EXPORT
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissionBeforeExport() {
        if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXPORT_WRITE_PERMISSION_CODE);
        } else {
            exportDB("");
        }
    }

    private void exportDB(String type) {
        fiscaIPDAO dao = new fiscaIPDAO(this);

        SQLiteDatabase db = dao.getReadableDatabase();
        long qtdCSV = DatabaseUtils.queryNumEntries(db, "fiscaIPTable",null);
        if (qtdCSV != 0) {
            exportFiscalizacoes(type, db);
        } else if (!type.equals("backup")) {
            Toast.makeText(this, "Não há Fiscalizações cadastradas para exportar.", Toast.LENGTH_SHORT).show();
        }

        dao.close();
    }

    private void exportFiscalizacoes(String type, SQLiteDatabase db) {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            boolean dirCreated = exportDir.mkdirs();
            Log.d("TAG1", "exportDB() called: " + dirCreated);
        }

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        File file;
        if (type.equals("backup")) {
            file = new File(exportDir, "EnelBackupFiscalizacaoIluminacaoPublica_" + timeStamp + ".csv");
        } else {
            file = new File(exportDir, "EnelExportFiscalizacaoIluminacaoPublica_" + timeStamp + ".csv");
        }


        try {
            boolean fileCreated = file.createNewFile();
            Log.d("TAG2", "createNewFile() called: " + fileCreated);
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            Cursor curCSV = db.rawQuery("SELECT" +
                    " id,"+
                    " funcionario,"+
                    " municipio,"+
                    " endereco,"+
                    " bairro,"+
                    " latitude,"+
                    " longitude,"+
                    " area_risco,"+
                    " acesa_24h,"+
                    " quebrado,"+
                    " tipo_luminaria,"+
                    " potencia,"+
                    " observacao,"+
                    " horario,"+
                    " plaqueta_prefeitura,"+
                    " area_urbano_rural" +
                    " FROM fiscaIPTable", null);

            String[] str = {"id",
                    "funcionario",
                    "municipio",
                    "endereco",
                    "bairro",
                    "latitude",
                    "longitude",
                    "area_risco",
                    "acesa_24h",
                    "quebrado",
                    "tipo_luminaria",
                    "potencia",
                    "observacao",
                    "horario",
                    "plaqueta_prefeitura",
                    "area_urbano_rural"
            };

            csvWrite.writeNext(str);

            while (curCSV.moveToNext()) {
                String id = stripAccents(curCSV.getString(0));
                String funcionario = stripAccents(curCSV.getString(1));
                String municipio = stripAccents(curCSV.getString(2));
                String endereco = stripAccents(curCSV.getString(3));
                String bairro = stripAccents(curCSV.getString(4));
                String latitude = stripAccents(curCSV.getString(5));
                String longitude = stripAccents(curCSV.getString(6));
                String area_risco = stripAccents(curCSV.getString(7));
                String acesa_24h = stripAccents(curCSV.getString(8));
                String quebrado = stripAccents(curCSV.getString(9));
                String tipo_luminaria = stripAccents(curCSV.getString(10));
                String potencia = stripAccents(curCSV.getString(11));
                String observacao = stripAccents(curCSV.getString(12));
                String horario = stripAccents(curCSV.getString(13));
                String plaqueta_prefeitura = stripAccents(curCSV.getString(14));
                String area_urbano_rural = stripAccents(curCSV.getString(15));

                String arrStr[] = {
                        id,
                        funcionario,
                        municipio,
                        endereco,
                        bairro,
                        latitude,
                        longitude,
                        area_risco,
                        acesa_24h,
                        quebrado,
                        tipo_luminaria,
                        potencia,
                        observacao,
                        horario,
                        plaqueta_prefeitura,
                        area_urbano_rural
                };

                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();

            if (!type.equals("backup")) {
                Toast.makeText(this, "Fiscalizações Exportadas!", Toast.LENGTH_SHORT).show();
            }

            Intent intent =
                    new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            sendBroadcast(intent);

        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }


    // UPLOAD
//    public boolean isInternetConnectionOk() {
//        // ICMP
//        Runtime runtime = Runtime.getRuntime();
//        try {
//            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
//            int     exitValue = ipProcess.waitFor();
//            return (exitValue == 0);
//        }
//        catch (IOException e)          { e.printStackTrace(); }
//        catch (InterruptedException e) { e.printStackTrace(); }
//
//        return false;
//    }
//
//    public boolean checkOnlineState() {
//        ConnectivityManager CManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo NInfo = CManager.getActiveNetworkInfo();
//        if (NInfo != null && NInfo.isConnectedOrConnecting()) {
//            return true;
//        }
//        return false;
//    }
//
//    private void uploadToCloud() {
//        fiscaClandDAO dao = new fiscaClandDAO(this);
//        List<fiscaClandModel> list = dao.getFiscaListNotUploadedYet();
//        dao.close();
//
//        if(list.size() > 0){
//            Toast.makeText(this, "Verificando conexão com o servidor..", Toast.LENGTH_SHORT).show();
//            boolean internetTest1 = isInternetConnectionOk();
//            boolean internetTest2 = checkOnlineState();
//
//            if(internetTest1 || internetTest2){
//                Toast.makeText(this, "Conexão existente, preparando para enviar os dados..", Toast.LENGTH_SHORT).show();
//                ProgressDialog dialog = ProgressDialog.show(this, "Enviar dados",
//                        "Enviando. Favor aguarde...", true);
//
//                sendGoogleScripts extDao = new sendGoogleScripts(this);
//                extDao.sendFiscalizacaoClandestinoExternal(list, dialog);
//
//            }else{
//                Toast.makeText(this, "Não foi possível realizar o envio dos dados para o servidor. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
//            }
//        }else{
//            Toast.makeText(this, "Não há mudanças para serem enviadas.", Toast.LENGTH_SHORT).show();
//        }
//
//    }
}
