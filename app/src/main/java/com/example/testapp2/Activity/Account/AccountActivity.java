package com.example.testapp2.Activity.Account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

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

        // подключение к бд
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firestoreManager = new FirestoreManager();

        // Загрузка сохраненного ника
        loadNickName();

        // Обработчик кнопки сохранения
        saveButton.setOnClickListener(v -> {
            saveNickName();
        });

        dellAcount.setOnClickListener(v ->{
            db.collection("users").document(user.getUid())
                    .delete()
                    .addOnSuccessListener(unused -> {
                        user.delete();
                        Toast.makeText(this, "Аккаунт Удален!", Toast.LENGTH_SHORT).show();
                        currentNickName.setText("");
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        //overridePendingTransition(0, 0);
                        finish();
                    })
                    .addOnFailureListener(e ->
                        Toast.makeText(this, "Аккаут не был удален!", Toast.LENGTH_SHORT).show());

        });
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
}