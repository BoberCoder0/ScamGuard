package com.example.testapp2.Activity.Account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.testapp2.Activity.MainActivity;
import com.example.testapp2.fragments.LoginFragment;
import com.example.testapp2.fragments.RegisterFragment;
import com.example.testapp2.R;
import com.example.testapp2.ui.AuthNavigator;

public class AuthActivity extends AppCompatActivity implements AuthNavigator {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Проверка, вошел ли пользователь ранее
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = settings.getBoolean(IS_LOGGED_IN, false);

        if (isLoggedIn) {
            // Если пользователь уже вошел, переходим к MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Закрываем AuthActivity
        } else {
            // Иначе загружаем экран входа по умолчанию
            if (savedInstanceState == null) {
                loadFragment(new LoginFragment());
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.auth_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void navigateToRegister() {
        loadFragment(new RegisterFragment());
    }

    @Override
    public void navigateToLogin() {
        loadFragment(new LoginFragment());
    }

    // Метод для сохранения состояния входа
    public void saveLoginState(boolean isLoggedIn) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    // Вызовите этот метод после успешного входа
    public void onLoginSuccess() {
        saveLoginState(true);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Закрываем AuthActivity
    }
}

