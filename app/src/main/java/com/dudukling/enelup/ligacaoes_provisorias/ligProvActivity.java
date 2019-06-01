package com.dudukling.enelup.ligacaoes_provisorias;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.dudukling.enelup.R;
import com.dudukling.enelup.adapter.ligProv_recyclerAdapter;
import com.dudukling.enelup.dao.lpDAO;
import com.dudukling.enelup.model.lpModel;
import com.opencsv.CSVWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ligProvActivity extends AppCompatActivity {
    private static final int EXPORT_WRITE_PERMISSION_CODE = 222;

    private RecyclerView recyclerView;
    private ligProv_recyclerAdapter RecyclerAdapter;
    private List<lpModel> lpList;
    private View textViewNoRecord;

    private Spinner spinnerLocalidade;
    private Spinner spinnerTipo;
    private Spinner spinnerEtapa;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("Ligações Provisórias");
        setContentView(R.layout.activity_lig_prov);
        recyclerView = findViewById(R.id.recyclerViewLP);

        registerForContextMenu(recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));


        lpDAO dao = new lpDAO(this);
        textViewNoRecord = this.findViewById(R.id.textViewNoRecords);

        if (dao.lastLPID() > 0) {
//            textViewNoRecord.setVisibility(View.GONE);
            lpList = dao.getLPList();

//            RecyclerAdapter = new ligProv_recyclerAdapter(lpList, this);
            RecyclerAdapter = new ligProv_recyclerAdapter(this);
            recyclerView.setAdapter(RecyclerAdapter);

            RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layout);
            setFilters();
        }
        dao.close();
    }

    private void setFilters() {
        spinnerLocalidade = findViewById(R.id.spinnerLPLocalidades);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.localidades, android.R.layout.simple_spinner_item); // Create an ArrayAdapter using the string array and a default spinner layout
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        spinnerLocalidade.setAdapter(adapter);    // Apply the adapter to the spinner
        spinnerLocalidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                RecyclerAdapter.updateStaticFilter(spinnerLocalidade, "localidade", position);
                checkQtdListed();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        spinnerTipo = findViewById(R.id.spinnerLPTipoOrdem);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.tipoOrdem, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter1);
        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                RecyclerAdapter.updateStaticFilter(spinnerTipo, "tipo", position);
                checkQtdListed();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        spinnerEtapa = findViewById(R.id.spinnerLPEtapa);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.etapas, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEtapa.setAdapter(adapter2);
        spinnerEtapa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                RecyclerAdapter.updateStaticFilter(spinnerEtapa, "etapa", position);
                checkQtdListed();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void checkQtdListed() {
        if (RecyclerAdapter.getItemCount() == 0) {
            textViewNoRecord.setVisibility(View.VISIBLE);
        } else {
            textViewNoRecord.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        lpDAO dao = new lpDAO(this);

        if (dao.lastLPID() == 0) {
            textViewNoRecord.setVisibility(View.VISIBLE);
        } else {
            textViewNoRecord.setVisibility(View.GONE);
            RecyclerAdapter.refreshList();
        }

        dao.close();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String[] filtros = RecyclerAdapter.getListaFiltros();
        outState.putSerializable("filtros", filtros);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String[] filtros = (String[]) savedInstanceState.getSerializable("filtros");
        RecyclerAdapter.setListaFiltros(filtros);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete_export_search, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Pesquisar...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                RecyclerAdapter.getFilter().filter(newText);
                RecyclerAdapter.updateSearchFilter(newText);
                checkQtdListed();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;

            case R.id.menu_delete:
                if (lpList != null) {
                    if (!lpList.isEmpty()) {
                        deleteAll();
                    }
                }
                break;

            case R.id.menu_export:
                checkPermissionBeforeExport();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAll() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        exportDB("backup");

                        if (!lpList.isEmpty()) {
                            for (int i = 0; i < lpList.size(); i++) {
                                lpModel lp = lpList.get(i);
                                List<String> lpImages = lp.getImagesList();
                                if (!lpImages.isEmpty()) {
                                    deleteImagesFromPhoneMemory(lp);
                                }
                            }
                        }

                        lpDAO dao = new lpDAO(ligProvActivity.this);
                        dao.truncateLPs();
                        dao.close();

                        lpList.clear();
                        RecyclerAdapter = new ligProv_recyclerAdapter(ligProvActivity.this);
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
        builder.setMessage("Tem certeza que deseja deletar todas as Ligações provisórias?").setPositiveButton("Confirmar", dialogClickListener)
                .setNegativeButton("Cancelar", dialogClickListener).show();
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

    private void exportDB(String type) {
        lpDAO dao = new lpDAO(this);

        SQLiteDatabase db = dao.getReadableDatabase();
        long qtdCSV = DatabaseUtils.queryNumEntries(db, "lpTable", "userObservacao<>''");
        if (qtdCSV != 0) {
            exportLPs(type, db);
        } else if (!type.equals("backup")) {
            Toast.makeText(this, "Não há LPs cadastradas para exportar.", Toast.LENGTH_SHORT).show();
        }

        dao.close();
    }

    private void exportLPs(String type, SQLiteDatabase db) {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            boolean dirCreated = exportDir.mkdirs();
            Log.d("TAG1", "exportDB() called: " + dirCreated);
        }

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        File file;
        if (type.equals("backup")) {
            file = new File(exportDir, "EnelBackupLP_" + timeStamp + ".csv");
        } else {
            file = new File(exportDir, "EnelExportLP_" + timeStamp + ".csv");
        }


        try {
            boolean fileCreated = file.createNewFile();
            Log.d("TAG2", "createNewFile() called: " + fileCreated);
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));


            Cursor curCSV = db.rawQuery("SELECT id, ordem, userObservacao, autoLat, autoLong, calcDecValor, calcDecFatorPotencia, calcDecPeriodo, calcDecTempo, calcDecTensao, calcDecKwh, calcEncPeriodo, calcEncTempo, calcEncCorrente, calcEncTensao, calcEncKwh " +
                    "FROM lpTable " +
                    "WHERE userObservacao <> ''", null);

            String[] str = {"ID", "ordem", "Observacao", "latitudeGPS", "longitudeGPS", "ValorDeclarado", "FatorPotenciaDeclarado", "PeriodoDeclarado", "TempoDeclarado", "TensaoDeclarado", "KwhTotalDeclarado", "PeriodoExcedenteEncontrado", "TempoExcedenteEncontrado", "CorrenteEncontrado", "TensaoEncontrado", "KwhTotalEncontrado"};

            csvWrite.writeNext(str);

            while (curCSV.moveToNext()) {
                // Column you want get from DB:
                String id = curCSV.getString(0);
                String ordem = curCSV.getString(1);
                String userObservacao = stripAccents(curCSV.getString(2));
                String autoLat = stripAccents(curCSV.getString(3));
                String autoLong = stripAccents(curCSV.getString(4));

                String calcDecValor = stripAccents(curCSV.getString(5));
                String calcDecFatorPotencia = stripAccents(curCSV.getString(6));
                String calcDecPeriodo = stripAccents(curCSV.getString(7));
                String calcDecTempo = stripAccents(curCSV.getString(8));
                String calcDecTensao = stripAccents(curCSV.getString(9));
                String calcDecKwh = stripAccents(curCSV.getString(10));

                String calcEncPeriodo = stripAccents(curCSV.getString(11));
                String calcEncTempo = stripAccents(curCSV.getString(12));
                String calcEncCorrente = stripAccents(curCSV.getString(13));
                String calcEncTensao = stripAccents(curCSV.getString(14));
                String calcEncKwh = stripAccents(curCSV.getString(15));

                String arrStr[] = {id, ordem, userObservacao, autoLat, autoLong, calcDecValor, calcDecFatorPotencia, calcDecPeriodo, calcDecTempo, calcDecTensao, calcDecKwh, calcEncPeriodo, calcEncTempo, calcEncCorrente, calcEncTensao, calcEncKwh};

                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();

            if (!type.equals("backup")) {
                Toast.makeText(this, "LPs Exportadas!", Toast.LENGTH_SHORT).show();
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

    public void deleteImagesFromPhoneMemory(lpModel lp) {
        List<String> imagesListToDelete = lp.getImagesList();
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




    private void exportImages() {
        /* Example: zipFileAtPath("downloads/myfolder", "downloads/myFolder.zip"); */

        String sourcePath = "/Download";
        //Environment.getExternalStorageDirectory()
        String toLocation = this.getFilesDir().getPath() + "/mysFolder.zip";
        // java.io.FileNotFoundException: /myFolder.zip (Read-only file system)

        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);

        Toast.makeText(this, "BB: " + sourcePath, Toast.LENGTH_SHORT).show();

        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                entry.setTime(sourceFile.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();

            Toast.makeText(this, "AAAAAA!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
//          return false;
        }
//       return true;
    }

    /* Zips a subfolder */
    private void zipSubFolder(ZipOutputStream out, File folder, int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(file.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    /*Example: getLastPathComponent("downloads/example/fileToZip"); Result: "fileToZip" */
    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }




}
