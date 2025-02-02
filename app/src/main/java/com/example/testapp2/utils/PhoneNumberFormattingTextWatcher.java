package com.example.testapp2.utils;

import android.text.Editable;

// Класс для форматирования номера
public class PhoneNumberFormattingTextWatcher implements android.text.TextWatcher {
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

    public static String formatPhoneNumber(String digits) {
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

