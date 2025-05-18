package com.example.testapp2.Activity.Account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.testapp2.Activity.InfoActivity;
import com.example.testapp2.Activity.Learn;
import com.example.testapp2.Activity.MainActivity;
import com.example.testapp2.Activity.Search;
import com.example.testapp2.Activity.Settings;
import com.example.testapp2.R;
import com.example.testapp2.databinding.ActivityAccountBinding;
import com.example.testapp2.fragments.DellAccountFragment;
import com.example.testapp2.fragments.LoginFragment;
import com.example.testapp2.fragments.RegisterFragment;
import com.example.testapp2.ui.AuthNavigator;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity implements AuthNavigator {

    private EditText nickNameInput,emailInput, passwordInput;
    private ImageView profileIcon;
    private TextView currentNickName;
    private ProgressBar progressBar;
    private View progressView;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAccountBinding binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Инициализация элементов UI
        nickNameInput = binding.nickNameInput;
        Button saveButton = binding.saveButton;
        currentNickName = binding.currentNickName;
        Button dellAcount = binding.dellAccount;
        Button login = binding.buttonLogin;
        Button register = binding.buttonRegister;
        progressBar = binding.progressBar;
        progressView = binding.progressView;
        emailInput = binding.emailInput;
        passwordInput = binding.passwordInput;
        profileIcon = binding.imageView;

        // Инициализация Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Проверка авторизации пользователя
        if (user == null) {
            showUnauthorizedUI(login, register);
        } else {
            showAuthorizedUI();
            loadUserData();
        }
    }

    private void showUnauthorizedUI(Button login, Button register) {
        nickNameInput.setVisibility(View.GONE);
        findViewById(R.id.save_button).setVisibility(View.GONE);
        currentNickName.setVisibility(View.GONE);
        findViewById(R.id.dell_account).setVisibility(View.GONE);

        login.setVisibility(View.VISIBLE);
        register.setVisibility(View.VISIBLE);

        login.setOnClickListener(v -> navigateToLogin());
        register.setOnClickListener(v -> navigateToRegister());
    }

    private void showAuthorizedUI() {
        nickNameInput.setVisibility(View.VISIBLE);
        findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        currentNickName.setVisibility(View.VISIBLE);
        findViewById(R.id.dell_account).setVisibility(View.VISIBLE);

        findViewById(R.id.buttonLogin).setVisibility(View.GONE);
        findViewById(R.id.buttonRegister).setVisibility(View.GONE);

        findViewById(R.id.save_button).setOnClickListener(v -> saveNickName());
        findViewById(R.id.dell_account).setOnClickListener(v -> {
            DellAccountFragment dialog = new DellAccountFragment();
            dialog.show(getSupportFragmentManager(), "DellAccountFragment");
        });
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.VISIBLE);
        //Log.d("ProgressDebug1", "Progress visibility: " + progressView.getVisibility()); // 0 (VISIBLE), 4 (INVISIBLE) или 8 (GONE)

        db.collection("users").document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    progressView.setVisibility(View.GONE);
                    //Log.d("ProgressDebug2", "Progress visibility: " + progressView.getVisibility()); // 0 (VISIBLE), 4 (INVISIBLE) или 8 (GONE)
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String nickname = task.getResult().getString("nickname");
                            if (nickname != null && !nickname.isEmpty()) {
                                currentNickName.setText("Ваш ник: " + nickname);
                                nickNameInput.setText(nickname);
                            } else {
                                currentNickName.setText("Вы не указали свой никнейм");
                            }
                        }
                    } else {
                        Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                        Log.e("AccountActivity", "Ошибка загрузки данных", task.getException());
                    }
                });
    }

    private void saveNickName() {
        String nickname = nickNameInput.getText().toString().trim();
        if (nickname.isEmpty()) {
            Toast.makeText(this, "Введите ник", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.GONE);
        //Log.d("ProgressDebug3", "Progress visibility: " + progressView.getVisibility()); // 0 (VISIBLE), 4 (INVISIBLE) или 8 (GONE)

        Map<String, Object> updates = new HashMap<>();
        updates.put("nickname", nickname);

        db.collection("users").document(user.getUid())
                .update(updates)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    progressView.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        currentNickName.setText("Ваш ник: " + nickname);
                        Toast.makeText(this, "Ник успешно сохранен!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Не удалось обновить никнейм", Toast.LENGTH_SHORT).show();
                        Log.e("AccountActivity", "Ошибка сохранения никнейма", task.getException());
                    }
                });
    }

    @Override
    public void navigateToLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void navigateToRegister() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RegisterFragment())
                .addToBackStack(null)
                .commit();
    }

    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_accoutn) {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            bottomNav.post(() -> {
                Menu menu = bottomNav.getMenu();
                for (int i = 0; i < menu.size(); i++) {
                    menu.getItem(i).setChecked(false);
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}