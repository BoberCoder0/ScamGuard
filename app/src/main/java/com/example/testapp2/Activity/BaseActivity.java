package com.example.testapp2.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testapp2.NavigationManager;
import com.example.testapp2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        }
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationManager.onNavigationSelected(this, item.getItemId());
        return true;
    }
}
