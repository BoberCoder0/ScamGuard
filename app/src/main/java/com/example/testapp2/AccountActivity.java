package com.example.testapp2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.testapp2.databinding.ActivityAccountBinding;

public class AccountActivity extends AppCompatActivity {

    private ActivityAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        //Убираем автоматическую настройку кнопки "Назад"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Аккаунт"); //Устанавливаем пустой заголовок
        }
    }
}