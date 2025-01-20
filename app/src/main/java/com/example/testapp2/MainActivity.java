package com.example.testapp2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private ImageButton accountButtonGo;
    private Button button5;
    private Button button6;
    //private SwipeDetectorView swipeDetector;
    private boolean swipeStarted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setContentView(R.layout.activity_main);

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


        /*drawer = findViewById(R.id.drawer_layout); // Замени на ID своего DrawerLayout
        swipeDetector = findViewById(R.id.swipeDetector); // Замени на ID своего SwipeDetectorView
        swipeDetector.attachDrawerLayout(drawer);
        swipeDetector.setSwipeSensitivity(400);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);*/

        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this); // Слушатель нажатий на элементы меню

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Button startSearchButton = findViewById(R.id.start_search);
        Button learnSchemesButton = findViewById(R.id.learn_button);

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

        View headerView = navigationView.getHeaderView(0);
        button5 = headerView.findViewById(R.id.button5);
        button6 = headerView.findViewById(R.id.button6);
        accountButtonGo = headerView.findViewById(R.id.account_button_go);

        // Назначаем обработчики нажатий
        setupClickListeners();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Добавляем кнопку "назад"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }


    private void setupClickListeners() {
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAccountActivity();
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAccountActivity();
            }
        });

        accountButtonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAccountActivity();
            }
        });
    }


    private void startAccountActivity() {
        Intent intent = new Intent(MainActivity.this, AccountActivity.class);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "AccountActivity не найдена", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d("MainActivity", "onNavigationItemSelected: Item ID = " + id);
        if (id == R.id.nav_home) { //**Исправлен порядок меню**
            Log.d("MainActivity", "onNavigationItemSelected: nav_home selected");
            Intent intent = new Intent(this, Search.class);
            startActivity(intent);
        } else if (id == R.id.nav_search) { //**Исправлен порядок меню**
            Log.d("MainActivity", "onNavigationItemSelected: nav_search selected");
            Intent intent = new Intent(this, Learn.class);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Замени на свой layout


        drawer = findViewById(R.id.drawer_layout); // Замени на ID своего DrawerLayout
        swipeDetector = findViewById(R.id.swipeDetector); // Замени на ID своего SwipeDetectorView
        swipeDetector.attach (drawer);
    }*/


   /* private void openAccountActivity(){
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }*/
}