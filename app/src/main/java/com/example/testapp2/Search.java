package com.example.testapp2;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testapp2.R;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class Search extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Найдите элементы интерфейса
        EditText phoneNumberInput = findViewById(R.id.phone_number_input);
        Button searchButton = findViewById(R.id.check_button);
        TextView searchResult = findViewById(R.id.result_view);

        // Установите TextWatcher для форматирования номера
        phoneNumberInput.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // Обработчик кнопки поиска
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberInput.getText().toString();
                //System.out.println(phoneNumber);
                Log.d("PhoneNumberInput", "Current input: " + phoneNumber);

                if (!phoneNumber.matches("\\+7 \\(\\d{3}\\) \\d{3}-\\d{2}-\\d{2}")) {
                    searchResult.setText("Введите номер телефона в формате: +7 (ххх) ххх-хх-хх");
                    return;
                }

                // Проверяем номер в базе данных
                if (isScammerNumber(phoneNumber)) {
                    searchResult.setText("Этот номер отмечен как мошенник!");
                } else {
                    searchResult.setText("Номер не найден в базе.");
                }
            }
        });
    }

    //проверка номера (замените на запрос к базе данных)(уже)
    private boolean isScammerNumber(String phoneNumber) {
        List<String> scammerNumbers = loadScammerNumbersFromExcel();
        return scammerNumbers.contains(phoneNumber);
    }


    // Класс для форматирования номера
    private static class PhoneNumberFormattingTextWatcher implements TextWatcher {
        private boolean isUpdating = false; // Флаг для предотвращения рекурсии
        private String previousText = "";  // Предыдущее значение текста

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Сохраняем предыдущее состояние текста перед изменением
            previousText = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Ничего не делаем в этом методе
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isUpdating) {
                return;
            }
            isUpdating = true;

            String currentText = s.toString();

            // Удаляем все символы, кроме цифр
            String digits = currentText.replaceAll("[^\\d]", "");
            String formatted = formatPhoneNumber(digits);

            // Проверяем, изменилось ли содержимое
            if (!formatted.equals(currentText)) {
                s.replace(0, s.length(), formatted);
            }

            isUpdating = false;
        }

        // Метод для форматирования номера
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
    private List<String> loadScammerNumbersFromExcel() {
        List<String> scammerNumbers = new ArrayList<>();
        try (InputStream inputStream = getAssets().open("ScammerNumber.xlsx")) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell cell = row.getCell(0); // Предположим, номера телефонов в первом столбце
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
}
