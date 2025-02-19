package com.example.testapp2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.testapp2.Activity.Account.AccountActivity;
import com.example.testapp2.R;
import com.example.testapp2.SearchViewModel;
import com.example.testapp2.databinding.ActivitySearchBinding;
import com.example.testapp2.utils.PhoneNumberFormattingTextWatcher;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Search extends AppCompatActivity {

    private SearchViewModel searchViewModel;
    private EditText phoneNumberInput;
    private TextView searchResult, categoryView, complaintsView, commentView;
    private TextView formatPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search);
//
        // Устанавливаем верхний тулбар
        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Устанавливаем верхний тулбар
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Скрыть кнопку "Назад"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        // Инициализация UI элементов
        phoneNumberInput = findViewById(R.id.phone_number_input);
        Button searchButton = findViewById(R.id.check_button);
        searchResult = findViewById(R.id.result_view);
        categoryView = findViewById(R.id.category_view);
        complaintsView = findViewById(R.id.complaints_view);
        commentView = findViewById(R.id.comment_view);


        phoneNumberInput.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // Инициализация ViewModel
        searchViewModel = new SearchViewModel(this);


        // Обработчик кнопки поиска
        searchButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberInput.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                searchResult.setText("Введите корректный номер!");
                return;
            }
            searchViewModel.searchPhoneNumber(phoneNumber);
        });

        // Наблюдение за LiveData
        searchViewModel.getSearchResult().observe(this, scamInfo -> {
            if (scamInfo != null) {
                categoryView.setText("Категория: " + scamInfo.getCategory());
                complaintsView.setText("Жалобы: " + scamInfo.getComplaints());
                commentView.setText("Комментарий: " + scamInfo.getComment());
                searchResult.setText("Этот номер мошенник!");
            } else searchResult.setText("Номер не найден в базе.");
        });
        // Переход по кнопкам в нижнем тулбаре
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.nav_search);
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
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }
    // Подключаем меню к Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.up_toolbar_menu, menu);
        return true;
    }

    // Обрабатываем нажатие на кнопку аккаунта
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_accoutn) {
            // Запускаем активити аккаунта
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
