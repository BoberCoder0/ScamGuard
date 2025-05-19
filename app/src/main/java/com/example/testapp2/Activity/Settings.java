package com.example.testapp2.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.testapp2.Activity.Account.AccountActivity;
import com.example.testapp2.Activity.Account.SearchHistoryActivity;
import com.example.testapp2.R; // Убедись, что путь до R правильный
import com.example.testapp2.utils.ThemeHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

public class Settings extends AppCompatActivity {

    private Switch themeSwitch;
    private SharedPreferences sharedPreferences;
    private Button cleanCacheButton;
    private ImageButton imageButtonDark, imageButtonLight;
    private TextView textViewDarkThemeLabel, textViewLightThemeLabel;
    private View lineDark, lineLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this); // 👈 обязательно ДО super.onCreate
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
        // Инициализация элементов
        imageButtonDark = findViewById(R.id.imageDark);
        imageButtonLight = findViewById(R.id.imageLight);
        textViewDarkThemeLabel = findViewById(R.id.DarkLabel);
        textViewLightThemeLabel = findViewById(R.id.LightLabel);
        lineDark = findViewById(R.id.line_dark);
        lineLight = findViewById(R.id.line_light);

        // Загружаем сохраненное состояние темы
        boolean isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false);
        themeSwitch.setChecked(isDarkMode);
        setThemeMode(isDarkMode);  /** нужон? */
        updateThemeUI(isDarkMode); /** нужон? */ // Обновляем UI сразу
        // Показываем соответствующую линию в зависимости от темы
        updateThemeLines(isDarkMode);

        // Обработчик кнопки очистки кеша
        cleanCacheButton.setOnClickListener(v -> {
            clearApplicationCache();
            Toast.makeText(this, "Кеш очищен", Toast.LENGTH_SHORT).show();
            restartApplication();
        });

        /*// Обработчики кликов на ImageButton
        imageButtonDark.setOnClickListener(v -> {
            setThemeMode(true);
            updateThemeLabels(true);
            restartActivity();
        });

        imageButtonLight.setOnClickListener(v -> {
            setThemeMode(false);
            updateThemeLabels(false);
            restartActivity();
        });*/

        // Обработчики кликов
        imageButtonDark.setOnClickListener(v -> setDarkTheme(true));
        imageButtonLight.setOnClickListener(v -> setDarkTheme(false));


//        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            setThemeMode(isChecked);
//            // Сохраняем состояние темы в SharedPreferences
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("is_dark_mode", isChecked);
//            editor.apply();
//            recreate(); // Просто пересоздаём активность
//        });
        /*themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Сохраняем состояние темы
            ThemeHelper.saveThemeChoice(this, isChecked);
            ThemeHelper.applyTheme(this); // применяем тему сразу

            // Перезапускаем активити без анимации (мягко, без мерцания)
            Intent intent = getIntent();
            finish();
            overridePendingTransition(0, 0); // убрать анимацию при выходе
            startActivity(intent);
            overridePendingTransition(0, 0); // убрать анимацию при входе
        });*/

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
                } else if (id == R.id.nav_history) {
                    startActivity(new Intent(getApplicationContext(), SearchHistoryActivity.class));
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

    // обводка
    private void updateThemeLines(boolean isDarkMode) {
        if (isDarkMode) {
            lineDark.setVisibility(View.VISIBLE);
            lineLight.setVisibility(View.GONE);
        } else {
            lineDark.setVisibility(View.GONE);
            lineLight.setVisibility(View.VISIBLE);
        }
    }

    private void setDarkTheme(boolean isDarkMode) {
        // Сохраняем выбор темы
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_dark_mode", isDarkMode);
        editor.apply();

        // Применяем тему
        ThemeHelper.applyTheme(this);

        // Обновляем UI
        updateThemeUI(isDarkMode);

        // Перезапускаем активность
        restartActivity();
    }

    private void updateThemeUI(boolean isDarkMode) {
        // Обновляем цвет текста
        int activeColor = ContextCompat.getColor(this, R.color.center_color_app);
        int inactiveColor = ContextCompat.getColor(this, R.color.light_gray);

        textViewDarkThemeLabel.setTextColor(isDarkMode ? activeColor : inactiveColor);
        textViewLightThemeLabel.setTextColor(isDarkMode ? inactiveColor : activeColor);
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

    // Перезапуск активности без анимации
    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d("LAYOUT", "DarkLabel position: " + textViewDarkThemeLabel.getTop());
    }

    protected int getSelectedMenuItemId() {
        return R.id.nav_settings;
    }
}