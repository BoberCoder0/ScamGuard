package com.example.testapp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.testapp2.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private DrawerLayout drawer;

    // Экземпляр DatabaseHelper
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Правильный setContentView

        Toolbar toolbar = binding.appBarMain.toolbar;
        setSupportActionBar(toolbar);

        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Получение View из Navigation Header
        View headerView = navigationView.getHeaderView(0);

        // Обработчики кнопок в навигационной шапке
        headerView.findViewById(R.id.button5).setOnClickListener(v -> startAccountActivity());
        headerView.findViewById(R.id.button6).setOnClickListener(v -> startAccountActivity());
        headerView.findViewById(R.id.account_button_go).setOnClickListener(v -> startAccountActivity());
        // Обработчики кнопок в main layout
        findViewById(R.id.start_search).setOnClickListener(v -> {
            Intent intent = new Intent(this, Search.class);
            startActivity(intent);
        });

        findViewById(R.id.learn_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, Learn.class);
            startActivity(intent);
        });
        // Добавляем кнопку "назад"

        // Инициализация DatabaseHelper и копирование базы данных
        databaseHelper = new DatabaseHelper(this);
        try {
            databaseHelper.copyDatabaseIfNeeded(); // Копирование базы данных, если её нет
//            Log.d("MainActivity", "База данных успешно скопирована или уже существует.");
        } catch (Exception e) {
//            Log.e("MainActivity", "Ошибка при копировании базы данных: ", e);
        }
    }


    private void startAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        try {
            startActivity(intent);
        } catch (Exception e) {
            // Log the exception to investigate
            android.util.Log.e("MainActivity", "Error starting AccountActivity", e);
            // Toast notification to show error
            android.widget.Toast.makeText(this, "AccountActivity not found", android.widget.Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        if (id == R.id.nav_search) {
            intent = new Intent(this, Search.class); // Переход к поиску
        }
        else if (id == R.id.nav_account) {
            intent = new Intent(this, AccountActivity.class); // Переход к профилю
        }
        else if (id == R.id.nav_history) {
            intent = new Intent(this, History.class); // Переход к профилю
        }
        else if (id == R.id.nav_learn) {
            intent = new Intent(this, Learn.class); // Переход к обучению
        }
        else if (id == R.id.nav_settings) {
            intent = new Intent(this, Settings.class); // Переход к настройкам
        }
        if(intent!= null)
            startActivity(intent);
        drawer.closeDrawer(GravityCompat.START);

        NavigationView navigationView = binding.navView;
        navigationView.post(new Runnable() {
            @Override
            public void run() {
                navigationView.setCheckedItem(-1);
            }
        });

        return true;
    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}