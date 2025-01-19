package com.example.testapp2;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.testapp2.databinding.ActivityMainBinding;

import android.content.Intent;
import android.widget.Button;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.appBarMain.toolbar;
        setSupportActionBar(toolbar); // Устанавливаем наш тулбар как экшнбар

        /*binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });*/

        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this); // Слушатель нажатий на элементы меню

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        Button startSearchButton = findViewById(R.id.start_search); // **Исправлено ID**
        Button learnSchemesButton = findViewById(R.id.learn_button);  // **Исправлено ID**

        // первая кнопка
        startSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход к активности SearchActivity
                Intent intent = new Intent(MainActivity.this, Search.class);
                startActivity(intent);
            }
        });

        // Установите обработчик для кнопки "Изучить схемы"
        learnSchemesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход к активности LearnActivity
                Intent intent = new Intent(MainActivity.this, Learn.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d("MainActivity", "onNavigationItemSelected: Item ID = " + id);
        if (id == R.id.nav_home) { //**Исправлен порядок меню**
            Log.d("MainActivity", "onNavigationItemSelected: nav_home selected");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_search) { //**Исправлен порядок меню**
            Log.d("MainActivity", "onNavigationItemSelected: nav_search selected");
            Intent intent = new Intent(this, Search.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) { //**Исправлен порядок меню**
            Log.d("MainActivity", "onNavigationItemSelected: nav_settings selected");
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
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
}