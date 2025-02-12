package com.example.testapp2.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.testapp2.Activity.MainActivity;
import com.example.testapp2.R;
import com.google.firebase.auth.FirebaseUser;
import androidx.fragment.app.Fragment;
import com.example.testapp2.Data.Firebase.FirebaseAuthManager;
import com.example.testapp2.Activity.Account.AccountActivity;
import com.example.testapp2.Activity.Account.AuthActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterFragment extends Fragment {

    private EditText emailField, passwordField, confirmPasswordField,nickName;
    private Button registerButton;

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Инициализация FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Привязка элементов UI
        nickName = view.findViewById(R.id.nick_name);
        emailField = view.findViewById(R.id.email_reg);
        passwordField = view.findViewById(R.id.password_1);
        confirmPasswordField = view.findViewById(R.id.password_2);
        Button registerButton = view.findViewById(R.id.button_register);

        // Установка клика на кнопку регистрации
        registerButton.setOnClickListener(v -> registerUser());

        return view;
    }

    private void registerUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        // Проверка на пустые поля
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getActivity(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка на совпадение паролей
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getActivity(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }

        // Регистрация через Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Переход в главный экран после успешной регистрации
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish(); // Закрыть текущую активность
                    } else {
                        // Если ошибка
                        Toast.makeText(getActivity(), "Ошибка регистрации: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

