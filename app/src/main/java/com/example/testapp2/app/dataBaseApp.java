package com.example.testapp2.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.testapp2.Data.database.DatabaseHelper;

public class dataBaseApp extends Application {
    private static DatabaseHelper databaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        reinitializeDatabaseHelper(this); // Call the new method
    }

    public static void reinitializeDatabaseHelper(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext()); // Use application context
        try {
            databaseHelper.copyDatabaseIfNeeded();
        } catch (Exception e) {
            Log.e("dataBaseApp", "Ошибка при копировании БД при реинициализации", e);
            // Optionally, re-throw or handle more gracefully
        }
    }

    public static DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }
}
