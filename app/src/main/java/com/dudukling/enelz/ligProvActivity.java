package com.dudukling.enelz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
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
import android.os.PersistableBundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.dudukling.enelz.adapter.ligProv_recyclerAdapter;
import com.dudukling.enelz.dao.lpDAO;
import com.dudukling.enelz.model.lpModel;
import com.dudukling.enelz.util.OpenCSVReader;
import com.opencsv.CSVWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ligProvActivity extends AppCompatActivity {
    private static final int FILE_SELECT_CODE = 000;
    private static final int WRITE_PERMISSION_CODE = 111;
    private static final int EXPORT_WRITE_PERMISSION_CODE = 222;

    private RecyclerView recyclerView;
    private ligProv_recyclerAdapter RecyclerAdapter;
    private List<lpModel> lpList;
    private View textViewNoRecord;
    private View buttonImportFile;

    private Spinner spinnerLocalidade;
    private Spinner spinnerTipo;
    private Spinner spinnerEtapa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Ligações Provisórias");
        setContentView(R.layout.activity_lig_prov);
        recyclerView = findViewById(R.id.recyclerViewLP);

        registerForContextMenu(recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));


        lpDAO dao = new lpDAO(this);
        textViewNoRecord = this.findViewById(R.id.textViewNoRecords);
        buttonImportFile = this.findViewById(R.id.buttonImportFile);

        if (dao.lastID() > 0) {
//            textViewNoRecord.setVisibility(View.GONE);
            buttonImportFile.setVisibility(View.GONE);
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        lpDAO dao = new lpDAO(this);
        textViewNoRecord = this.findViewById(R.id.textViewNoRecords);
        buttonImportFile = this.findViewById(R.id.buttonImportFile);

        if (dao.lastID() == 0) {
            textViewNoRecord.setVisibility(View.VISIBLE);
            buttonImportFile.setVisibility(View.VISIBLE);

            Button importButton = findViewById(R.id.buttonImportFile);
            importButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    if (ligProvActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ligProvActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_CODE);
                    } else {
                        callFileChooser();
                    }
                }
            });
        }else{
            textViewNoRecord.setVisibility(View.GONE);
            RecyclerAdapter.refreshList();
        }

        dao.close();
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

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
        inflater.inflate(R.menu.menu_lig_prov, menu);

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
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
                case R.id.menu_delete_all:
                    if(lpList!=null) {
                        if(!lpList.isEmpty()) {
                            deleteAll();
                        }
                    }
                    break;
                case R.id.menu_export_file:
                    checkPermissionBeforeExport();
                    break;
                case R.id.menu_clandestinos:
                    Intent goToClandestinoList = new Intent(ligProvActivity.this, clandestinoActivity.class);
                    startActivity(goToClandestinoList);
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

                        if(!lpList.isEmpty()) {
                            for (int i = 0; i < lpList.size(); i++) {
                                lpModel lp = lpList.get(i);
                                List<String> lpImages = lp.getImagesList();
                                if (!lpImages.isEmpty()) {
                                    ligProvActivity.deleteImagesFromPhoneMemory(lp);
                                }
                            }
                        }

                        lpDAO dao = new lpDAO(ligProvActivity.this);
                        dao.truncateDBs();
                        dao.close();

                        lpList.clear();
                        RecyclerAdapter = new ligProv_recyclerAdapter(ligProvActivity.this);
                        recyclerView.setAdapter(RecyclerAdapter);

                        textViewNoRecord.setVisibility(View.VISIBLE);
                        buttonImportFile.setVisibility(View.VISIBLE);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tem certeza que deseja deletar tudo (Ligações provisórias e Clandestinos)?").setPositiveButton("Confirmar", dialogClickListener)
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



    private void exportDB(String type) {
        lpDAO dao = new lpDAO(this);

        SQLiteDatabase db = dao.getReadableDatabase();
        long qtdCSV = DatabaseUtils.queryNumEntries(db, "lpTable","userObservacao<>'' AND userCargaMedida!=''");
        if(qtdCSV != 0) {
            exportLPs(type, db);
        }else if(!type.equals("backup")){
            Toast.makeText(this, "Não há LPs cadastradas para exportar.", Toast.LENGTH_SHORT).show();
        }

        long qtdCSVClandest = DatabaseUtils.queryNumEntries(db, "lpClandestino");
        if(qtdCSVClandest != 0) {
            exportClandests(type, db);
        }else if(!type.equals("backup")){
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
        if(type.equals("backup")){
            file = new File(exportDir, "EnelBackupClandestinos_" + timeStamp + ".csv");
        }else{
            file = new File(exportDir, "EnelExportClandestinos_" + timeStamp + ".csv");
        }


        try {
            boolean fileCreated = file.createNewFile();
            Log.d("TAG2", "createNewFile() called: " + fileCreated);
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            Cursor curCSV = db.rawQuery("SELECT lpClandestino.id, ordem, lpClandestino.endereco, transformador, tensao, corrente, protecao, fatorPotencia, carga, descricao FROM lpClandestino " +
                    "INNER JOIN lpTable ON lpClandestino.lpID=lpTable.id", null);

            String[] str = {"ID", "ordem", "endereco", "transformador", "tensao", "corrente", "protecao", "fator de potencia", "carga", "descricao"};

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

                String arrStr[] = {id, ordem, endereco, transformador, tensao, corrente, protecao, fatorPotencia, carga, descricao};

                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();

            if(!type.equals("backup")) {
                Toast.makeText(this, "Clandestinos Exportados!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    private void exportLPs(String type, SQLiteDatabase db) {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            boolean dirCreated = exportDir.mkdirs();
            Log.d("TAG1", "exportDB() called: " + dirCreated);
        }

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        File file;
        if(type.equals("backup")){
            file = new File(exportDir, "EnelBackupLP_" + timeStamp + ".csv");
        }else{
            file = new File(exportDir, "EnelExportLP_" + timeStamp + ".csv");
        }


        try {
            boolean fileCreated = file.createNewFile();
            Log.d("TAG2", "createNewFile() called: " + fileCreated);
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));


            Cursor curCSV = db.rawQuery("SELECT id, ordem, userObservacao, userCargaMedida FROM lpTable " +
                    "WHERE userObservacao <> '' OR userCargaMedida != ''", null);

            String[] str = {"ID", "ordem", "userObservacao", "userCargaMedida"};

            csvWrite.writeNext(str);

            while (curCSV.moveToNext()) {
                // Column you want get from DB:
                String id = curCSV.getString(0);
                String ordem = curCSV.getString(1);
                String userObservacao = stripAccents(curCSV.getString(2));
                String userCargaMedida = stripAccents(curCSV.getString(3));

                String arrStr[] = {id, ordem, userObservacao, userCargaMedida};

                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();

            if(!type.equals("backup")) {
                Toast.makeText(this, "LPs Exportadas!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    public static void deleteImagesFromPhoneMemory(lpModel lp) {
        List<String> imagesListToDelete = lp.getImagesList();
        for(int i = 0; i < imagesListToDelete.size(); i++){
            File file = new File(imagesListToDelete.get(i));
            boolean deleted = file.delete();
            Log.d("TAG4", "delete() called: "+deleted);
        }
    }

    private void callFileChooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Selecione um arquivo para carregar"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Favor instalar um Gerenciador de Arquivos!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == WRITE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Não é possível utilizar esta função sem permissão!", Toast.LENGTH_SHORT).show();
            } else {
                callFileChooser();
            }
        }
        if(requestCode == EXPORT_WRITE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Não é possível utilizar esta função sem permissão!", Toast.LENGTH_SHORT).show();
            } else{
                exportDB("");
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedURI = data.getData();
                    String path = getPathFromUri(this, selectedURI);

                    try {
                        OpenCSVReader.readCSVFile(this, path);
                    }catch(IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Não foi possível carregar este arquivo!", Toast.LENGTH_LONG).show();
                    }

                    lpDAO dao = new lpDAO(this);
                    lpList = dao.getLPList();

//                    RecyclerAdapter = new ligProv_recyclerAdapter(lpList, this);
                    RecyclerAdapter = new ligProv_recyclerAdapter(this);
                    recyclerView.setAdapter(RecyclerAdapter);
                    RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layout);

                    buttonImportFile.setVisibility(View.GONE);
//                    textViewNoRecord.setVisibility(View.GONE);
                    setFilters();

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
