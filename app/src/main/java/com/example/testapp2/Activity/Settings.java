package com.example.testapp2.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import com.example.testapp2.R; // Убедись, что путь до R правильный
import com.example.testapp2.databinding.ActivitySettingsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Settings extends AppCompatActivity {

    private Switch themeSwitch;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_settings);
//
//        // Устанавливаем верхний тулбар
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        /*ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);*/

//        // Скрыть кнопку "Назад"
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//            getSupportActionBar().setHomeButtonEnabled(false);
//        }

//        // Настраиваем нижний тулбар
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);


//        // Добавляем кнопку "назад"
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }

        themeSwitch = findViewById(R.id.themeSwitch);
        sharedPreferences = getSharedPreferences("theme_pref", Context.MODE_PRIVATE);

        // Загружаем сохраненное состояние темы
        boolean isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false);
        themeSwitch.setChecked(isDarkMode);
        setThemeMode(isDarkMode);


        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setThemeMode(isChecked);
            // Сохраняем состояние темы в SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_dark_mode", isChecked);
            editor.apply();
        });
    }


    // Метод для установки режима темы
    private void setThemeMode(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }

    protected int getSelectedMenuItemId() {
        return R.id.nav_settings;
    }



}