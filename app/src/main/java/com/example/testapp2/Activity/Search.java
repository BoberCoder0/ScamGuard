package com.example.testapp2.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.testapp2.R;
import com.example.testapp2.SearchViewModel;
import com.example.testapp2.databinding.ActivitySearchBinding;
import com.example.testapp2.utils.PhoneNumberFormattingTextWatcher;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Search extends BaseActivity {

    private SearchViewModel searchViewModel;
    private EditText phoneNumberInput;
    private TextView searchResult, categoryView, complaintsView, commentView;
    private TextView formatPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search);
//
//        // Устанавливаем верхний тулбар
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

//        // Скрыть кнопку "Назад"
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//            getSupportActionBar().setHomeButtonEnabled(false);
//        }

//        // Настраиваем нижний тулбар
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);


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
    }

    protected int getSelectedMenuItemId() {
        return R.id.nav_search;
    }

}
