package com.fusiotec.servicecenterapi.servicecenter.utilities;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.fusiotec.servicecenterapi.servicecenter.manager.LocalStorage;

import org.joda.time.DateTime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Owner on 1/3/2017.
 */

public class Utils {
    private static final String IMAGE_DIRECTORY_NAME = "serviceapp/images";
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 901;
    public static Uri openImageIntent(Activity act) {
        Uri outputFileUri = Utils.getOutputMediaFileUri();
        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = act.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }
        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        act.startActivityForResult(chooserIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        return outputFileUri;
    }

    private static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }
    private static File getOutputMediaFile() {
        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("IMAGE_DIRECTORY_NAME", "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }
    public static void saveToErrorLogs(String stacktrace){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = "timestamp"+dateFormat.format(new Date());
        String filename = timestamp + ".txt";
        try {
            File directory = new File(Environment.getExternalStorageDirectory()+"/serviceapp/logcat");
            if (!directory.exists()){
                directory.mkdirs();
            }
            String localPath = directory.getAbsolutePath();
            Log.e("errorLogs",localPath + "/" + filename);
            BufferedWriter bos = new BufferedWriter(new FileWriter(localPath + "/" + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        }catch(Exception e){
            Log.e("file",""+e.getMessage());
        }
    }
    public static Date getServerDate(LocalStorage ls){
        DateTime date = new DateTime();
        return date.plusSeconds(ls.getInt(LocalStorage.TIME_DIFFERENCE_IN_SECONDS,0)).toDate();
    }

    public static String getPath(Uri selectedImage , Context context){
        String[] filePathColumn  = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor == null) return null;
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    public static String dateToString(Date date,String format){
        if(date != null){
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        }
        return "";
    }
    public static Date stringToDate(String date,String format){
        try {
            if(date != null){
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                return sdf.parse(date);
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }
    public static long getMax(Realm realm, Class clazz, String column){
        long getMax;
        try {
            getMax = (long)(realm.where(clazz).lessThan(column,0).findAll().min(column));
        }catch(Exception e){
            getMax = 0;
        }
        return getMax;
    }

    public static void errorMessage(Context context,String message,DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(context)
                .setTitle("Warning")
                .setMessage(message)
                .setIcon(android.R.drawable.stat_sys_warning)
                .setPositiveButton("OK",listener)
                .show();
    }
}