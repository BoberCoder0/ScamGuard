package com.example.testapp2.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {

    // Получить абсолютный путь к файлу по Uri
    public static String getPath(Context context, Uri uri) {
        // Для файлов из MediaStore (галерея)
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Попробуем получить путь через MediaStore
            String[] projection = { MediaStore.Images.Media.DATA };
            try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    String path = cursor.getString(columnIndex);
                    if (path != null) return path;
                }
            } catch (Exception ignored) {}
            // Если не получилось — скопируем файл во временную папку
            return copyToTempFile(context, uri);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        // Для Android 10+ (scoped storage) — всегда копируем во временный файл
        return copyToTempFile(context, uri);
    }

    // Копирует файл из Uri во временную папку и возвращает путь
    private static String copyToTempFile(Context context, Uri uri) {
        try {
            String fileName = getFileName(context, uri);
            File tempFile = new File(context.getCacheDir(), fileName != null ? fileName : "temp_image");
            try (InputStream in = context.getContentResolver().openInputStream(uri);
                 FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            return tempFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Получить имя файла по Uri
    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (idx != -1) result = cursor.getString(idx);
                }
            }
        }
        if (result == null) {
            String path = uri.getPath();
            int cut = path != null ? path.lastIndexOf('/') : -1;
            if (cut != -1) result = path.substring(cut + 1);
        }
        return result;
    }
}