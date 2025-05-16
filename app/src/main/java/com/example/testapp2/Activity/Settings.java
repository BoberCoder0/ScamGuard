package com.example.testapp2.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.example.testapp2.Activity.Account.AccountActivity;
import com.example.testapp2.R; // Убедись, что путь до R правильный
import com.example.testapp2.databinding.ActivitySettingsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

public class Settings extends AppCompatActivity {

    private Switch themeSwitch;
    private SharedPreferences sharedPreferences;
    private Button cleanCacheButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Устанавливаем верхний тулбар
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Скрыть кнопку "Назад"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        // Инициализация элементов UI
        themeSwitch = findViewById(R.id.themeSwitch);
        cleanCacheButton = findViewById(R.id.CleanCashButton);
        sharedPreferences = getSharedPreferences("theme_pref", Context.MODE_PRIVATE);

        // Загружаем сохраненное состояние темы
        boolean isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false);
        themeSwitch.setChecked(isDarkMode);
        setThemeMode(isDarkMode);

        // Обработчик кнопки очистки кеша
        cleanCacheButton.setOnClickListener(v -> {
            clearApplicationCache();
            Toast.makeText(this, "Кеш очищен", Toast.LENGTH_SHORT).show();
            restartApplication();
        });


        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setThemeMode(isChecked);
            // Сохраняем состояние темы в SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_dark_mode", isChecked);
            editor.apply();
        });

        // Переход по кнопкам в нижнем тулбаре
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.nav_settings);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.nav_learn) {
                    startActivity(new Intent(getApplicationContext(), Learn.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_search) {
                    startActivity(new Intent(getApplicationContext(), Search.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_settings) {
                    return true;
                } else if (id == R.id.nav_info) {
                    startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });

    }

    // Для перехода по иконке аккаунта
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.up_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_accoutn) {
            startActivity(new Intent(getApplicationContext(), AccountActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
// Метод для очистки кеша приложения
private void clearApplicationCache() {
    try {
        File cacheDir = getCacheDir();
        File applicationDir = new File(cacheDir.getParent());
        if (applicationDir.exists()) {
            String[] children = applicationDir.list();
            for (String child : children) {
                if (!child.equals("lib")) {
                    deleteDir(new File(applicationDir, child));
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    // Вспомогательный метод для рекурсивного удаления директории
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir != null && dir.delete();
    }

    // Метод для перезапуска приложения
    private void restartApplication() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        Runtime.getRuntime().exit(0); // Гарантированное завершение процесса
    }

    protected int getSelectedMenuItemId() {
        return R.id.nav_settings;
    }
}