package com.example.testapp2.app;

import android.app.Application;
import android.util.Log;

import com.example.testapp2.Data.database.DatabaseHelper;

public class dataBaseApp extends Application {
    private static DatabaseHelper databaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = new DatabaseHelper(this);
        try {
            databaseHelper.copyDatabaseIfNeeded();
        } catch (Exception e) {
            Log.e("MyApp", "Ошибка при копировании БД", e);
        }
    }

    public static DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }
}
