package com.example.testapp2.Activity.Account;

import static androidx.core.app.PendingIntentCompat.getActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.testapp2.Activity.Learn;
import com.example.testapp2.Activity.MainActivity;
import com.example.testapp2.Data.Firebase.FirestoreManager;
import com.example.testapp2.R;
import com.example.testapp2.databinding.ActivityAccountBinding;
import com.example.testapp2.fragments.DellAccountFragment;
import com.example.testapp2.fragments.EmptyActivity;
import com.example.testapp2.fragments.LoginFragment;
import com.example.testapp2.fragments.RegisterFragment;
import com.example.testapp2.ui.AuthNavigator;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity implements AuthNavigator {

    private EditText nickNameInput;
    private TextView currentNickName;

    // для бд

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private FirestoreManager firestoreManager;

  /*  private static final String PREF_NICKNAME = "nickname";
    private SharedPreferences sharedPreferences;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAccountBinding binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // тулбар
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        // Скрыть кнопку "Назад"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        // Находим элементы UI
        nickNameInput = binding.nickNameInput;
        Button saveButton = binding.saveButton;
        currentNickName = binding.currentNickName;
        Button dellAcount = binding.dellAccount;
        Button login = binding.buttonLogin;
        Button register = binding.buttonRegister;

        // подключение к бд
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firestoreManager = new FirestoreManager();

        // Проверяем, зарегистрирован ли пользователь
        if (auth.getCurrentUser() == null) {
            // Пользователь не авторазован, показываем кнопки
            nickNameInput.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            currentNickName.setVisibility(View.GONE);
            dellAcount.setVisibility(View.GONE);

            login.setVisibility(View.VISIBLE);
            register.setVisibility(View.VISIBLE);

            // прямой переход на фрагмент с помощью интерфейса
            login.setOnClickListener(v -> this.navigateToLogin());
            register.setOnClickListener(v -> this.navigateToRegister());

            // TODO: код ниже не нужен + сделать вход (сейшас не воркает)

            /*login.setOnClickListener(v -> {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .addToBackStack(null) // чтобы можно было вернуться нажатием "Назад"
                        .commit();

                // Переход на AuthActivity
                Intent intent = new Intent(AccountActivity.this, EmptyActivity.class);
                startActivity(intent);
                finish(); // Закрываем текущую активность
            });*/

            /*register.setOnClickListener(v2 -> {
                // Переход на AuthActivity
                Intent intent = new Intent(AccountActivity.this, EmptyActivity.class);
                startActivity(intent);
                finish(); // Закрываем текущую активность

            });*/

            // TODO: finish(); (если добавть не работает)
        } else {
            // Пользователь авторизован, показываем фрагмент регистрации
            nickNameInput.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            currentNickName.setVisibility(View.VISIBLE);
            dellAcount.setVisibility(View.VISIBLE);

            login.setVisibility(View.GONE);
            register.setVisibility(View.GONE);

            // Загрузка сохраненного ника
            loadNickName();

            // Обработчик кнопки сохранения
            saveButton.setOnClickListener(v -> {
                saveNickName();
            });

            dellAcount.setOnClickListener(v -> {
                DellAccountFragment dialog = new DellAccountFragment();
                dialog.show(getSupportFragmentManager(),"DellAccountFragment");
            });
        }

    }

    private void saveNickName(){
        String nickname = nickNameInput.getText().toString().trim();
        if (!nickname.isEmpty()) {
            // фронт
            currentNickName.setText("Ваш ник: " + nickname);
            Toast.makeText(this, "Ник сохранён", Toast.LENGTH_SHORT).show();
            //бек
            Map<String, Object> updates = new HashMap<>();
            updates.put("nickname",nickname);
            db.collection("users").document(user.getUid())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Ник успешно сохранен!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                        Toast.makeText(this, "Не удалось обновить никнеим!", Toast.LENGTH_SHORT).show());

        }else {
            Toast.makeText(this, "Введите ник", Toast.LENGTH_SHORT).show();
        }
    }
    private void loadNickName(){
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(document->{
                    if (document.exists()) {
                        String nickname = document.getString("nickname");
                        if (nickname != null) { currentNickName.setText(nickname); }
                        else {currentNickName.setText("Вы не указали свой никнеим");}
                    }

                })
                .addOnFailureListener(e ->
                    Toast.makeText(this, "Ошибка загрузки ника", Toast.LENGTH_SHORT).show());
    }

    // для фрагмента
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
}