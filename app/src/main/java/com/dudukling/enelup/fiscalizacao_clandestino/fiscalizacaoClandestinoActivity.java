package com.dudukling.enelup.fiscalizacao_clandestino;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.dudukling.enelup.adapter.fiscalizacao_recyclerAdapter;
import com.dudukling.enelup.util.sendGoogleScripts;
import com.dudukling.enelup.dao.fiscalizaDAO;
import com.dudukling.enelup.model.fiscaModel;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class fiscalizacaoClandestinoActivity extends AppCompatActivity {
    private static final int EXPORT_WRITE_PERMISSION_CODE = 222;

    private RecyclerView recyclerView;
    private TextView textViewNoRecord;
    private List<fiscaModel> fiscaList;
    private fiscalizacao_recyclerAdapter RecyclerAdapter;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("Cadastro de Clandestinos");
        setContentView(R.layout.fisc_clandest_activity_main);

        FloatingActionButton floatingActionButtonFiscalizaClandestinoAdd = this.findViewById(R.id.floatingActionButtonFiscalizaClandestinoAdd);
        floatingActionButtonFiscalizaClandestinoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fiscaModel fisca = new fiscaModel();
                Intent goToFormActivity = new Intent(fiscalizacaoClandestinoActivity.this, fiscalizacaoClandestinoFormActivity.class);
                goToFormActivity
                        .putExtra("fisca", fisca)
                        .putExtra("type", "new");
                fiscalizacaoClandestinoActivity.this.startActivity(goToFormActivity);
            }
        });


        recyclerView = findViewById(R.id.recyclerViewFiscalizacao);
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
                uploadToCloud();
//                uploadImagesToCloud();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        fiscalizaDAO dao = new fiscalizaDAO(this);
        textViewNoRecord = this.findViewById(R.id.textViewFiscaNoRecords);

        fiscaList = dao.getFiscaList();
        if (fiscaList.size() > 0) {
            RecyclerAdapter = new fiscalizacao_recyclerAdapter(this);
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
        fiscalizaDAO dao = new fiscalizaDAO(this);

        SQLiteDatabase db = dao.getReadableDatabase();
        long qtdCSV = DatabaseUtils.queryNumEntries(db, "fiscaTable",null);
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
            file = new File(exportDir, "EnelBackupFiscalizacaoClandestino_" + timeStamp + ".csv");
        } else {
            file = new File(exportDir, "EnelExportFiscalizacaoClandestino_" + timeStamp + ".csv");
        }


        try {
            boolean fileCreated = file.createNewFile();
            Log.d("TAG2", "createNewFile() called: " + fileCreated);
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));


            Cursor curCSV = db.rawQuery("SELECT" +
                        " id,"+
                        " funcionario,"+
                        " nome,"+
                        " endereco,"+
                        " bairro,"+
                        " municipio,"+
                        " cpf,"+
                        " cpf_status,"+
                        " nis,"+
                        " rg,"+
                        " data_nascimento,"+
                        " medidor_vizinho_1,"+
                        " medidor_vizinho_2,"+
                        " telefone,"+
                        " celular,"+
                        " email,"+
                        " latitude,"+
                        " longitude,"+
                        " preservacao_ambiental,"+
                        " area_invadida,"+
                        " tipo_ligacao,"+
                        " rede_local,"+
                        " padrao_montado,"+
                        " faixa_servidao,"+
                        " pre_indicacao,"+
                        " cpf_pre_indicacao,"+
                        " existe_ordem,"+
                        " numero_ordem,"+
                        " estado_ordem,"+
                        " servico_direcionado,"+
                        " frente_trabalho"+
                    " FROM fiscaTable "
                    , null);

            String[] str = {"id",
                    "funcionario",
                    "nome",
                    "endereco",
                    "bairro",
                    "municipio",
                    "cpf",
                    "cpf_status",
                    "nis",
                    "rg",
                    "data_nascimento",
                    "medidor_vizinho_1",
                    "medidor_vizinho_2",
                    "telefone",
                    "celular",
                    "email",
                    "latitude",
                    "longitude",
                    "preservacao_ambiental",
                    "area_invadida",
                    "tipo_ligacao",
                    "rede_local",
                    "padrao_montado",
                    "faixa_servidao",
                    "pre_indicacao",
                    "cpf_pre_indicacao",
                    "existe_ordem",
                    "numero_ordem",
                    "estado_ordem",
                    "servico_direcionado",
                    "frente_trabalho"
            };

            csvWrite.writeNext(str);

            while (curCSV.moveToNext()) {
                String id = stripAccents(curCSV.getString(0));
                String funcionario = stripAccents(curCSV.getString(1));
                String nome = stripAccents(curCSV.getString(2));
                String endereco = stripAccents(curCSV.getString(3));
                String bairro = stripAccents(curCSV.getString(4));
                String municipio = stripAccents(curCSV.getString(5));
                String cpf = stripAccents(curCSV.getString(6));
                String cpf_status = stripAccents(curCSV.getString(7));
                String nis = stripAccents(curCSV.getString(8));
                String rg = stripAccents(curCSV.getString(9));
                String data_nascimento = stripAccents(curCSV.getString(10));
                String medidor_vizinho_1 = stripAccents(curCSV.getString(11));
                String medidor_vizinho_2 = stripAccents(curCSV.getString(12));
                String telefone = stripAccents(curCSV.getString(13));
                String celular = stripAccents(curCSV.getString(14));
                String email = stripAccents(curCSV.getString(15));
                String latitude = stripAccents(curCSV.getString(16));
                String longitude = stripAccents(curCSV.getString(17));
                String preservacao_ambiental = stripAccents(curCSV.getString(18));
                String area_invadida = stripAccents(curCSV.getString(19));
                String tipo_ligacao = stripAccents(curCSV.getString(20));
                String rede_local = stripAccents(curCSV.getString(21));
                String padrao_montado = stripAccents(curCSV.getString(22));
                String faixa_servidao = stripAccents(curCSV.getString(23));
                String pre_indicacao = stripAccents(curCSV.getString(24));
                String cpf_pre_indicacao = stripAccents(curCSV.getString(25));
                String existe_ordem = stripAccents(curCSV.getString(26));
                String numero_ordem = stripAccents(curCSV.getString(27));
                String estado_ordem = stripAccents(curCSV.getString(28));
                String servico_direcionado = stripAccents(curCSV.getString(29));
                String frente_trabalho = stripAccents(curCSV.getString(30));

                String arrStr[] = {
                        id,
                        funcionario,
                        nome,
                        endereco,
                        bairro,
                        municipio,
                        cpf,
                        cpf_status,
                        nis,
                        rg,
                        data_nascimento,
                        medidor_vizinho_1,
                        medidor_vizinho_2,
                        telefone,
                        celular,
                        email,
                        latitude,
                        longitude,
                        preservacao_ambiental,
                        area_invadida,
                        tipo_ligacao,
                        rede_local,
                        padrao_montado,
                        faixa_servidao,
                        pre_indicacao,
                        cpf_pre_indicacao,
                        existe_ordem,
                        numero_ordem,
                        estado_ordem,
                        servico_direcionado,
                        frente_trabalho
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


    // DELETE
    private void deleteAll() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        exportDB("backup");

                        if (!fiscaList.isEmpty()) {
                            for (int i = 0; i < fiscaList.size(); i++) {
                                fiscaModel fisca = fiscaList.get(i);
                                List<String> fiscaImages = fisca.getImagesList();
                                if (!fiscaImages.isEmpty()) {
                                    deleteImagesFromPhoneMemory(fisca);
                                }
                            }
                        }

                        fiscalizaDAO dao = new fiscalizaDAO(fiscalizacaoClandestinoActivity.this);
                        dao.truncateFiscalizacoes();
                        dao.close();

                        fiscaList.clear();
                        RecyclerAdapter = new fiscalizacao_recyclerAdapter(fiscalizacaoClandestinoActivity.this);
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

    public void deleteImagesFromPhoneMemory(fiscaModel fisca) {
        List<String> imagesListToDelete = fisca.getImagesList();
        for (int i = 0; i < imagesListToDelete.size(); i++) {
            File file = new File(imagesListToDelete.get(i));
            boolean deleted = file.delete();
            Log.d("TAG4", "delete() called: " + deleted);
        }

        String path = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (File file : files) {
            boolean deleted = file.delete();
            Log.d("TAG4", "delete() called: " + deleted);
        }
    }


    // HELPERS
    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }


    // UPLOAD
    public boolean isInternetConnectionOk() {
        // ICMP
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    public boolean checkOnlineState() {
        ConnectivityManager CManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo NInfo = CManager.getActiveNetworkInfo();
        if (NInfo != null && NInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private void uploadToCloud() {
        fiscalizaDAO dao = new fiscalizaDAO(this);
        sendGoogleScripts extDao = new sendGoogleScripts(this);

        List<fiscaModel> list = dao.getFiscaListNotUploadedYet();
        dao.close();

        if(list.size() > 0){
            Toast.makeText(this, "Verificando conexão com o servidor..", Toast.LENGTH_SHORT).show();
            boolean internetTest1 = isInternetConnectionOk();
            boolean internetTest2 = checkOnlineState();

            if(internetTest1 || internetTest2){
                ProgressDialog dialog = ProgressDialog.show(this, "Enviar dados",
                        "Enviando. Favor aguarde...", true);

                extDao.sendFiscalizacaoClandestinoExternal(list, dialog);

            }else{
                Toast.makeText(this, "Não foi possível realizar o envio dos dados para o servidor. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Não há mudanças para serem enviadas.", Toast.LENGTH_SHORT).show();
        }

    }
}
