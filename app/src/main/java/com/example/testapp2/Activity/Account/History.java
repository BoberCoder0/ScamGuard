package com.example.testapp2.Activity.Account;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.testapp2.Activity.BaseActivity;
import com.example.testapp2.R;
import com.example.testapp2.databinding.ActivityHistoryBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class History extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActivityHistoryBinding binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Устанавливаем верхний тулбар
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Настраиваем нижний тулбар
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);


        CharSequence label = "";
        try {
            PackageManager packageManager = getPackageManager();
            ComponentName componentName = new ComponentName(this, this.getClass());
            ActivityInfo activityInfo = packageManager.getActivityInfo(componentName, 0);

            label = activityInfo.loadLabel(packageManager);
            // label теперь содержит твой текст
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // Установка лейбла
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(label);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}