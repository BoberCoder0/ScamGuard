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
import com.example.testapp2.utils.LocaleHelper;

import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity implements AuthNavigator {

    // private static final String PREFS_NAME = "MyPrefsFile"; // Removed
    // private static final String IS_LOGGED_IN = "isLoggedIn"; // Removed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.loadLocale(this); // Added locale loading
        setContentView(R.layout.activity_auth);

        // Check Firebase Auth state directly
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // User is signed in, navigate to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Close AuthActivity
        } else {
            // No user signed in, load LoginFragment by default
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
    // Метод для сохранения состояния входа - Removed
    // public void saveLoginState(boolean isLoggedIn) { ... }

    // Вызовите этот метод после успешного входа - Removed
    // public void onLoginSuccess() { ... }
}

