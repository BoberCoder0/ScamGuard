package com.example.testapp2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.testapp2.databinding.ActivitySearchBinding;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {

    private static final String DATABASE_NAME = "scammer_database.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "scammer_numbers";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        // Установка лейбла
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.search));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Найдите элементы интерфейса
        EditText phoneNumberInput = findViewById(R.id.phone_number_input);
        Button searchButton = findViewById(R.id.check_button);
        TextView searchResult = findViewById(R.id.result_view);
        dbHelper = new DBHelper(this);

        // Загрузка данных из excel в базу данных (один раз при первом запуске)
        if (isFirstRun()) {
            loadScammerNumbersFromExcelToDB();
            setFirstRunCompleted();
        }

        // Установите TextWatcher для форматирования номера
        phoneNumberInput.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // Обработчик кнопки поиска
        searchButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberInput.getText().toString().trim();

            // Проверяем ввод на пустоту
            if (TextUtils.isEmpty(phoneNumber)) {
                searchResult.setText("Введите номер телефона!");
                return;
            }

            // Приводим номер к стандартному формату для поиска
            String normalizedNumber = normalizePhoneNumber(phoneNumber);

            if (normalizedNumber == null) {
                searchResult.setText("Введите корректный номер телефона в формате: +7 (xxx) xxx-xx-xx");
                return;
            }

            // Проверяем номер в базе данных
            String resultMessage = searchInDatabase(normalizedNumber);
            searchResult.setText(resultMessage);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Метод нормализации номера телефона
    private String normalizePhoneNumber(String phoneNumber) {
        // Убираем все нецифровые символы
        String digits = phoneNumber.replaceAll("[^\\d]", "");

        // Проверяем, что номер корректен (должен начинаться с +7 и иметь 11 цифр)
        if (digits.length() == 11 && digits.startsWith("7")) {
            return "+7" + digits.substring(1); // Возвращаем в формате +7xxxxxxxxxx
        }
        return null; // Если номер некорректен, возвращаем null
    }

    // Поиск в базе данных
    private String searchInDatabase(String phoneNumber) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {COLUMN_PHONE_NUMBER};
        String selection = COLUMN_PHONE_NUMBER + " = ?";
        String[] selectionArgs = {phoneNumber};

        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            if (cursor != null && cursor.moveToFirst()) {
                return "Этот номер отмечен как мошенник!";
            } else {
                return "Номер не найден в базе.";
            }
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // Загрузка номеров мошенников из Excel в базу данных
    private void loadScammerNumbersFromExcelToDB() {
        List<String> scammerNumbers = loadScammerNumbersFromExcel();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction(); // Открываем транзакцию
        try {
            for (String phoneNumber : scammerNumbers) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_PHONE_NUMBER, phoneNumber);
                db.insert(TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful(); // Устанавливаем транзакцию как успешную
        } finally {
            db.endTransaction(); // Завершаем транзакцию
        }
    }

    private boolean isFirstRun() {
        return getPreferences(MODE_PRIVATE).getBoolean("is_first_run", true);
    }

    private void setFirstRunCompleted() {
        getPreferences(MODE_PRIVATE).edit().putBoolean("is_first_run", false).apply();
    }

    // Загрузка номеров мошенников из Excel файла
    private List<String> loadScammerNumbersFromExcel() {
        List<String> scammerNumbers = new ArrayList<>();
        try (InputStream inputStream = getAssets().open("ScammerNumber.xlsx")) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell cell = row.getCell(0); // Предположим, что номера телефонов в первом столбце
                if (cell != null) {
                    scammerNumbers.add(cell.getStringCellValue().trim());
                }
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scammerNumbers;
    }

    // Класс для форматирования номера
    private static class PhoneNumberFormattingTextWatcher implements TextWatcher {
        private boolean isUpdating = false;
        private String previousText = "";

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            previousText = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isUpdating) {
                return;
            }
            isUpdating = true;

            String currentText = s.toString();
            String digits = currentText.replaceAll("[^\\d]", "");
            String formatted = formatPhoneNumber(digits);

            if (!formatted.equals(currentText)) {
                s.replace(0, s.length(), formatted);
            }

            isUpdating = false;
        }

        private String formatPhoneNumber(String digits) {
            StringBuilder formatted = new StringBuilder();
            if (digits.length() > 0) {
                formatted.append("+7 ");
            }
            if (digits.length() > 1) {
                formatted.append("(").append(digits.substring(1, Math.min(4, digits.length())));
            }
            if (digits.length() > 4) {
                formatted.append(") ").append(digits.substring(4, Math.min(7, digits.length())));
            }
            if (digits.length() > 7) {
                formatted.append("-").append(digits.substring(7, Math.min(9, digits.length())));
            }
            if (digits.length() > 9) {
                formatted.append("-").append(digits.substring(9, Math.min(11, digits.length())));
            }

            return formatted.toString();
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null)
            dbHelper.close();
        super.onDestroy();
    }

    // Класс помощник для базы данных
    private static class DBHelper extends android.database.sqlite.SQLiteOpenHelper {

        DBHelper(android.content.Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_PHONE_NUMBER + " TEXT)";
            db.execSQL(SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
