package com.example.testapp2.Activity.Account;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.testapp2.Activity.BaseActivity;
import com.example.testapp2.R;
import com.example.testapp2.databinding.ActivityAccountBinding;

public class AccountActivity extends BaseActivity {

    private EditText nickNameInput;
    private TextView currentNickName;

    private static final String PREF_NICKNAME = "nickname";
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAccountBinding binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        // Установка лейбла
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.account));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Находим элементы UI
        nickNameInput = findViewById(R.id.nick_name_input);
        Button saveButton = findViewById(R.id.save_button);
        currentNickName = findViewById(R.id.current_nick_name);

        sharedPreferences = getPreferences(MODE_PRIVATE);

        // Загрузка сохраненного ника
        loadNickName();

        // Обработчик кнопки сохранения
        saveButton.setOnClickListener(v -> {
            saveNickName();
        });
    }

    private void saveNickName(){
        String nickname = nickNameInput.getText().toString().trim();
        if (!nickname.isEmpty()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PREF_NICKNAME, nickname);
            editor.apply();
            currentNickName.setText("Ваш ник: " + nickname);
            Toast.makeText(this, "Ник сохранён", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, "Введите ник", Toast.LENGTH_SHORT).show();
        }
    }
    private void loadNickName(){
        String savedNickName = sharedPreferences.getString(PREF_NICKNAME, "");
        if (!savedNickName.isEmpty())
            currentNickName.setText("Ваш ник: " + savedNickName);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected int getSelectedMenuItemId() {
        return R.id.nav_home;
    }

}