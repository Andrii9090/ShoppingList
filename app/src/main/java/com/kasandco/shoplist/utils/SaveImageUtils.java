package com.kasandco.shoplist.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;


import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

public class SaveImageUtils {
    Context context;
    private String fileName;
    private File storageDir;
    private File fileImage;
    private String currentFilePath;

    @Inject
    public SaveImageUtils(Context context) {
        this.context = context;
    }

    @SuppressLint("SimpleDateFormat")
    public File createImageFile() throws IOException {
        fileName = new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date());
        storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        fileImage = File.createTempFile(
                fileName,
                ".jpg",
                storageDir);
        currentFilePath = fileImage.getAbsolutePath();
        return fileImage;
    }

    public String getCurrentFilePath() {
        return currentFilePath;
    }

    public Uri getPhotoUri() {
        return FileProvider.getUriForFile(context,
                "com.kasandco.familyfinance",
                fileImage);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String wholeID = DocumentsContract.getDocumentId(contentUri);
        String[] splits = wholeID.split(":");
        if (splits.length == 2) {
            String[] projection = {MediaStore.Images.Media.DATA};
            String id = splits[1];
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, sel, new String[]{id}, null);
            int column_index = cursor.getColumnIndex(projection[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(column_index);
            } else {
                return null;
            }
        } else {
            return contentUri.getPath();
        }
    }

    public Uri copyImageToGallery(String pathImage) throws IOException {
        Bitmap img = null;
        FileInputStream inputStream = new FileInputStream(pathImage);
        File fileGallery = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName.concat(".jpg"));
        FileOutputStream outputStream = new FileOutputStream(fileGallery);
        img = BitmapFactory.decodeStream(inputStream);
        Bitmap rotateImage = getCameraOrientation(pathImage, img);
        if(rotateImage!=null){
            rotateImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        }else {
            img.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        }
        outputStream.close();
        File fileOld = new File(pathImage);
        fileOld.delete();
        return Uri.fromFile(fileGallery);
    }

    public Bitmap getCameraOrientation(String photoPath, Bitmap bitmap) throws IOException {
        ExifInterface ei = new ExifInterface(photoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
//            case ExifInterface.ORIENTATION_UNDEFINED:

                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:


            default:
                rotatedBitmap = bitmap;
        }
        return rotatedBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void saveBase64ToImage(String base64Str) throws IOException {
        byte[] bytes = Base64.decode(base64Str, Base64.DEFAULT);
        FileOutputStream os = new FileOutputStream(createImageFile());
        os.write(bytes);
    }
}
