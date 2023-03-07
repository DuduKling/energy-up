package com.dudukling.energyup.fiscalizacao_clandestino.util;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.dudukling.energyup.fiscalizacao_clandestino.fiscaClandAlbumActivity;
import com.dudukling.energyup.model.fiscaClandModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.dudukling.energyup.fiscalizacao_clandestino.fiscaClandAlbumActivity.CAMERA_REQUEST_CODE;

public class fiscaClandCameraController {
    private final fiscaClandModel fisca;
    private File photoFile = null;
    private String photoPath;
    private final fiscaClandAlbumActivity formActivity;
    public File storageDir;

    public fiscaClandCameraController(fiscaClandAlbumActivity formActivity, fiscaClandModel fisca) {
        this.formActivity = formActivity;
        this.fisca = fisca;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setCameraActions() {
        PackageManager pm = formActivity.getPackageManager();
        boolean rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);

        if(rearCam) {
            startCamera();
        }
    }

    public void startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(formActivity.getPackageManager()) != null) {

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("TAG 8", "startCamera: ", ex);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(formActivity,
                        "com.dudukling.energyup.fileProvider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                formActivity.startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("ddMMyyy_HHmmss").format(new Date());

        String imageFileName = fisca.getId() + "_" + timeStamp;

        String path = formActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/fiscalizacao-clandestino/" + fisca.getId();
        storageDir = new File(path);
        if(!storageDir.exists()){
            boolean created = storageDir.mkdirs();
            Log.i("TAG 9", "createImageFile: "+created);
        }
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,   /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        photoPath = image.getAbsolutePath();
        return image;
    }

    public void deleteTempFromPhoneMemory(String photoFilePath) {
        File file = new File(photoFilePath);
        boolean deleted = file.delete();
        Log.d("TAG3", "delete() called: "+deleted);
    }

    public File getPhotoFile() {
        return photoFile;
    }
    public String getPhotoPath() {
        return photoPath;
    }
    public void setPhotoFile(File photoFile) {
        this.photoFile = photoFile;
    }
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

}