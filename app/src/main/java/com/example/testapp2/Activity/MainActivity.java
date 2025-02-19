package com.example.testapp2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.example.testapp2.Activity.Account.AccountActivity;
import com.example.testapp2.Data.database.DatabaseHelper;
import com.example.testapp2.R;
import com.example.testapp2.app.dataBaseApp;
import com.example.testapp2.databinding.ActivityMainBinding;
import com.example.testapp2.databinding.ActivitySearchBinding;
import com.example.testapp2.databinding.ActivitySettingsBinding;
import com.example.testapp2.fragments.LoginFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // Экземпляр DatabaseHelper
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Правильный setContentView
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Устанавливаем верхний тулбар
        //Toolbar toolbar = binding.toolbar;
        //setSupportActionBar(toolbar);

        // Устанавливаем верхний тулбар
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHelper = dataBaseApp.getDatabaseHelper();

        findViewById(R.id.start_search).setOnClickListener(v -> {
            Intent intent = new Intent(this, Search.class);
            startActivity(intent);
        });

        findViewById(R.id.learn_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, Learn.class);
            startActivity(intent);
        });

//        // Скрыть кнопку "Назад"
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//            getSupportActionBar().setHomeButtonEnabled(false);
//        }

        // Переход по кнопкам в нижнем тулбаре
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.nav_learn) {
                    startActivity(new Intent(getApplicationContext(), Learn.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (id == R.id.nav_search) {
                    startActivity(new Intent(getApplicationContext(), Search.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (id == R.id.nav_settings) {
                    startActivity(new Intent(getApplicationContext(), Settings.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (id == R.id.nav_info) {
                    startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (id == R.id.nav_home) {
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
    private String getUserEmail() {
        // Логика для получения email пользователя
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.email);
        if (loginFragment != null) {
            return loginFragment.getEmail();
        }
        return null; // или возвращайте пустую строку, если email не найден
    }

}
