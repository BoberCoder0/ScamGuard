package com.example.testapp2.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.testapp2.Activity.MainActivity;
import com.example.testapp2.Data.Firebase.FirestoreManager;
import com.example.testapp2.R;

import com.example.testapp2.ui.AuthNavigator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;

// TODO: Implement proper email verification (e.g., using Firebase Functions to send a verification email or a unique link).
public class RegisterFragment extends Fragment {

    private EditText emailField, passwordField, confirmPasswordField,nickName;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private AuthNavigator navigator;

    /*@Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            navigator = (AuthNavigator) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AuthNavigator");
        }
    }*/

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AuthNavigator) {
            navigator = (AuthNavigator) context;
        } else {
            throw new ClassCastException(context + " must implement AuthNavigator");
        }
    }

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
        confirmPasswordField = view.findViewById(R.id.password_2);
        // codeField = view.findViewById(R.id.email_check); // Removed

        registerButton = view.findViewById(R.id.button_register);
        // sendCodeButton = view.findViewById(R.id.button_send_code); // Removed
        ImageButton backButton = view.findViewById(R.id.back_to_login_button);
        // verifyCodeButton = view.findViewById(R.id.verify_code_button); // Removed

        // registerButton.setVisibility(View.GONE); // No longer hidden by default
        // verifyCodeButton.setVisibility(View.GONE); // Removed

        backButton.setOnClickListener(v -> {
            if (navigator != null) {
                navigator.navigateToLogin();
            }
        });

        // Установка клика на кнопку регистрации
        registerButton.setOnClickListener(v -> {
            // Perform validation before calling registerUser
            String email = emailField.getText().toString().trim();
            String nicknameText = nickName.getText().toString().trim(); // Corrected variable name
            String password = passwordField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();

            // Проверка на пустые поля
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || nicknameText.isEmpty()) {
                Toast.makeText(getActivity(), "Заполните все поля", Toast.LENGTH_SHORT).show(); // TODO: Use string resource
                return;
            }

            // Проверка совпадения паролей
            if (!password.equals(confirmPassword)) {
                Toast.makeText(getActivity(), "Пароли не совпадают", Toast.LENGTH_SHORT).show(); // TODO: Use string resource
                return;
            }
            // TODO: Add password strength validation if desired (e.g., minimum length)

            registerUser();
        });




        return view;
    }

    private void registerUser() {
        String email = emailField.getText().toString().trim();
        // String nick_name = nickName.getText().toString().trim(); // Validation moved to OnClickListener
        String password = passwordField.getText().toString().trim();
        // String confirmPassword = confirmPasswordField.getText().toString().trim(); // Validation moved

        // Client-side validation is now done in the OnClickListener for registerButton.
        // Proceed directly with Firebase registration.

        // Регистрация через Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // обявление переменных
                        String uid = mAuth.getCurrentUser().getUid();
                        String nicknameText = nickName.getText().toString().trim(); // Ensure this is the corrected variable name
                        // оттправка данных
                        FirestoreManager firestoreManager = new FirestoreManager();
                        firestoreManager.saveUserToFirestore(uid, email, nicknameText);

                        // Переход в главный экран после успешной регистрации
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        if (getActivity() != null) {
                            getActivity().finish(); // Закрыть текущую активность
                        }

                    } else {
                        // Если ошибка
                        // TODO: Use string resource for error message prefix
                        Toast.makeText(getActivity(), "Ошибка регистрации: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // Removed generateCode() and sendCodeToEmail() methods
}

