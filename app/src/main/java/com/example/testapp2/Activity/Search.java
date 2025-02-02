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
import androidx.lifecycle.ViewModelProvider;
import com.example.testapp2.R;
import com.example.testapp2.Data.models.ScamInfo;
import com.example.testapp2.SearchViewModel;
import com.example.testapp2.utils.PhoneNumberFormattingTextWatcher;

public class Search extends AppCompatActivity {

    private SearchViewModel searchViewModel;
    private EditText phoneNumberInput;
    private TextView searchResult, categoryView, complaintsView, commentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //тулбар
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.search));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                searchResult.setText("Введите номер телефона!");
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
            } else {
                categoryView.setText("Категория: -");
                complaintsView.setText("Жалобы: -");
                commentView.setText("Комментарий: -");
                searchResult.setText("Номер не найден в базе.");
            }
        });
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
