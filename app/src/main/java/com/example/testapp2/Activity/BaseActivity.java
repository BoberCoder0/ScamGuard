package com.example.testapp2.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testapp2.NavigationManager;
import com.example.testapp2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.testapp2.databinding.ActivityBaseBinding;

public abstract class BaseActivity extends AppCompatActivity {

    private ActivityBaseBinding binding; // Используем View Binding
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Используем View Binding для загрузки разметки
        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // ОБЯЗАТЕЛЬНО!
        setupBottomNavigation();
    }
    protected abstract int getSelectedMenuItemId();
    private void setupBottomNavigation() {
        Log.e("BaseActivity", "setupBottomNavigation: НАЧАЛО ФУНКЦИИ!");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (bottomNavigationView == null) {
            Log.e("BaseActivity", "setupBottomNavigation: bottom_navigation == NULL!");
            return;
        }

        Log.e("BaseActivity", "setupBottomNavigation: bottom_navigation найден!");

        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        bottomNavigationView.setSelectedItemId(getSelectedMenuItemId());
    }


    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d("Navigation", "Clicked item ID: " + item.getItemId());
        NavigationManager.onNavigationSelected(this, item.getItemId());
        return true;
    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//        if (bottomNavigationView != null) {
//            bottomNavigationView.setSelectedItemId(getSelectedMenuItemId()); // Подсвечиваем нужную кнопку
//        }
//    }
}
