package com.example.testapp2.Data.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "scammer_database.db"; // Имя вашей базы данных
    private static final int DATABASE_VERSION = 1; // Версия базы данных
    private static String DATABASE_PATH = ""; // Путь к базе данных

    private final Context context;
    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        // Устанавливаем путь к базе данных
        DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        copyDatabaseIfNeeded();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Не используется, так как база данных уже готова
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Не используется, так как база данных уже готова
    }

    /**
     * Копирует базу данных из папки assets в системный путь, если её там ещё нет.
     */
    public void copyDatabaseIfNeeded() {
        File databaseFile = new File(DATABASE_PATH + DATABASE_NAME);
        if (!databaseFile.exists()) {
            try {
                InputStream inputStream = context.getAssets().open(DATABASE_NAME);
                File databaseFolder = new File(DATABASE_PATH);
                if (!databaseFolder.exists()) {
                    databaseFolder.mkdirs(); // Создаем папку, если её нет
                }

                OutputStream outputStream = new FileOutputStream(databaseFile);
                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при копировании базы данных", e);
            }
        }
    }

    /**
     * Открывает базу данных для чтения и записи.
     *
     * @throws SQLException если база данных не может быть открыта
     */
    public void openDatabase() throws SQLException {
        String path = DATABASE_PATH + DATABASE_NAME;
        database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }

    /**
     * Закрывает соединение с базой данных.
     */
    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }

    /**
     * Выполняет запрос к базе данных для получения деталей номера телефона.
     *
     * @param phoneNumber Номер телефона для поиска
     * @return Cursor с результатами запроса
     */
    public Cursor getPhoneNumberDetails(String phoneNumber) {
        if (database == null || !database.isOpen()) {
            openDatabase();
        }

        String query = "SELECT category, comments FROM scammer_table WHERE phone_number = ?";
        return database.rawQuery(query, new String[]{phoneNumber});
    }
}
