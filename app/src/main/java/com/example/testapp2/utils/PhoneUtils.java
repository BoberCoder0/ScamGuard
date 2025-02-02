package com.example.testapp2.utils;

public class PhoneUtils {
    // Метод нормализации номера телефона
    public static String normalizePhoneNumber(String phoneNumber) {
        // Убираем все нецифровые символы
        String digits = phoneNumber.replaceAll("[^\\d]", "");

        // Проверяем, что номер корректен (должен начинаться с +7 и иметь 11 цифр)
        if (digits.length() == 11 && digits.startsWith("7")) {
            return "+7" + digits.substring(1); // Возвращаем в формате +7xxxxxxxxxx
        }
        return null; // Если номер некорректен, возвращаем null
    }
}
