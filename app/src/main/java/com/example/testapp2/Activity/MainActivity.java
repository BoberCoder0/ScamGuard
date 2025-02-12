package com.example.testapp2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.testapp2.Data.database.DatabaseHelper;
import com.example.testapp2.NavigationManager;
import com.example.testapp2.R;
import com.example.testapp2.app.dataBaseApp;
import com.example.testapp2.databinding.ActivityMainBinding;
import com.example.testapp2.fragments.LoginFragment;
import com.google.android.material.navigation.NavigationView;
import com.example.testapp2.fragments.LoginFragment;

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
        databaseHelper = dataBaseApp.getDatabaseHelper();

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
        /*View headerView = navigationView.getHeaderView(0);

        // Обработчики кнопок в навигационной шапке
        headerView.findViewById(R.id.button5).setOnClickListener(v -> startAccountActivity());
        headerView.findViewById(R.id.button6).setOnClickListener(v -> startAccountActivity());
        headerView.findViewById(R.id.account_button_go).setOnClickListener(v -> startAccountActivity());*/
        // Обработчики кнопок в main layout
        findViewById(R.id.start_search).setOnClickListener(v -> {
            Intent intent = new Intent(this, Search.class);
            startActivity(intent);
        });

        findViewById(R.id.learn_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, Learn.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Получение email из LoginFragment или другого источника
        String email = getUserEmail();

        // Передача email в метод onNavigationSelected
        NavigationManager.onNavigationSelected(this, item.getItemId(), email);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String getUserEmail() {
        // Логика для получения email пользователя
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.email);
        if (loginFragment != null) {
            return loginFragment.getEmail();
        }
        return null; // или возвращайте пустую строку, если email не найден
    }



    /*private void startAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        try {
            startActivity(intent);
        } catch (Exception e) {
            // Log the exception to investigate
            android.util.Log.e("MainActivity", "Error starting AccountActivity", e);
            // Toast notification to show error
            android.widget.Toast.makeText(this, "AccountActivity not found", android.widget.Toast.LENGTH_SHORT).show();

        }
    }*/
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}