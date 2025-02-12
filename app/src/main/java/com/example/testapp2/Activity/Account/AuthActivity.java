package com.example.testapp2.Activity.Account;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.testapp2.fragments.LoginFragment;
import com.example.testapp2.fragments.RegisterFragment;
import com.example.testapp2.R;
import com.example.testapp2.ui.AuthNavigator;

public class AuthActivity extends AppCompatActivity implements AuthNavigator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (savedInstanceState == null) {
            loadFragment(new LoginFragment()); // Загружаем экран входа по умолчанию
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
}

