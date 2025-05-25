package com.example.testapp2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.testapp2.Activity.Account.AccountActivity;
import com.example.testapp2.Activity.Account.SearchHistoryActivity;
import com.example.testapp2.Data.models.SearchHistoryItem;
import com.example.testapp2.R;
import com.example.testapp2.SearchViewModel;
import com.example.testapp2.databinding.ActivitySearchBinding;
import com.example.testapp2.utils.LocaleHelper;
import com.example.testapp2.utils.PhoneNumberFormattingTextWatcher;
import com.example.testapp2.utils.ThemeHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Search extends AppCompatActivity {

    private SearchViewModel searchViewModel;
    private EditText phoneNumberInput;
    private TextView searchResult, categoryView, complaintsView, commentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        LocaleHelper.loadLocale(this);

        setTitle(R.string.search);
        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        phoneNumberInput = findViewById(R.id.phone_number_input);
        Button searchButton = findViewById(R.id.check_button);
        searchResult = findViewById(R.id.result_view);
        categoryView = findViewById(R.id.category_view);
        complaintsView = findViewById(R.id.complaints_view);
        commentView = findViewById(R.id.comment_view);

        phoneNumberInput.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        searchViewModel = new SearchViewModel(this);

        // Обработка интента из истории поиска
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("SELECTED_PHONE_NUMBER")) {
            String selectedPhoneNumber = intent.getStringExtra("SELECTED_PHONE_NUMBER");
            phoneNumberInput.setText(selectedPhoneNumber);
            if (searchButton != null) {
                searchButton.performClick();
            }
        }

        // Обработчик кнопки поиска
        searchButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberInput.getText().toString().trim();
            Log.d("SearchHistory", "Search Activity запущен и я попала сюда при нажатии кнопки Поиск");

            if (TextUtils.isEmpty(phoneNumber)) {
                searchResult.setText("Введите корректный номер!");
                return;
            }

            // Запускаем поиск
            searchViewModel.searchPhoneNumber(phoneNumber);
        });

        // Наблюдение за результатом поиска (асинхронно)
        searchViewModel.getSearchResult().observe(this, scamInfo -> {
            String phoneNumber = phoneNumberInput.getText().toString().trim();
            String result;

            if (scamInfo != null) {
                // Показываем подробности
                categoryView.setVisibility(View.VISIBLE);
                complaintsView.setVisibility(View.VISIBLE);
                commentView.setVisibility(View.VISIBLE);
                searchResult.setVisibility(View.VISIBLE);

                categoryView.setText("Категория: " + scamInfo.getCategory());
                complaintsView.setText("Кол-во жалоб: " + scamInfo.getComplaints());
                commentView.setText("Комментарий от пользователя: " + scamInfo.getComment());
                searchResult.setText("Внимание!\n\nНомер находится в базе, возможно, это мошенники.");
                searchResult.setBackgroundResource(R.drawable.red_border);

                result = "Мошенник"; // или можно динамически в зависимости от категории
            } else {
                // Скрываем подробности
                categoryView.setVisibility(View.GONE);
                complaintsView.setVisibility(View.GONE);
                commentView.setVisibility(View.GONE);
                searchResult.setVisibility(View.VISIBLE);

                searchResult.setText("Номер в базе отсутствует.");
                searchResult.setBackgroundResource(R.drawable.green_border);

                result = "Не найден";
            }

            // Сохраняем в историю вместе с результатом
            saveSearchToHistory(phoneNumber, result);
        });

        // Нижний тулбар (нижнее меню)
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_search);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_learn) {
                startActivity(new Intent(getApplicationContext(), Learn.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_search) {
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), Settings.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(getApplicationContext(), SearchHistoryActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    // Опции верхнего меню
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

    private void saveSearchToHistory(String phoneNumber, String searchResult) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && !TextUtils.isEmpty(phoneNumber)) {
            String uid = user.getUid();
            Log.d("SearchHistory", "Current UID: " + uid);
            Log.d("SearchHistory", "Сохраняем номер: " + phoneNumber + ", результат: " + searchResult);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> item = new HashMap<>();
            item.put("phoneNumber", phoneNumber);
            item.put("timestamp", System.currentTimeMillis());
            item.put("searchResult", searchResult);

            db.collection("users")
                    .document(uid)
                    .collection("search_history")
                    .add(item)
                    .addOnSuccessListener(documentReference -> Log.d("SearchHistory", "Успешно сохранено в Firestore"))
                    .addOnFailureListener(e -> Log.e("SearchHistory", "Ошибка сохранения: " + e.getMessage()));
        } else {
            Log.w("SearchHistory", "Пользователь не авторизован или номер пустой");
        }
    }
}