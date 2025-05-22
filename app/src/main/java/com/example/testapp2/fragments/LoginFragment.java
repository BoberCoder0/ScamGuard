package com.example.testapp2.fragments;

import android.content.Context;
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
import com.example.testapp2.ui.AuthNavigator;
import com.example.testapp2.Data.Firebase.FirebaseAuthManager;
import com.example.testapp2.R;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private FirebaseAuthManager authManager;
    private AuthNavigator navigator;
    private EditText emailInput;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            navigator = (AuthNavigator) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AuthNavigator");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        authManager = new FirebaseAuthManager();
        navigator = (AuthNavigator) getActivity();

        EditText emailInput = view.findViewById(R.id.email);
        EditText passwordInput = view.findViewById(R.id.password);
        Button loginButton = view.findViewById(R.id.loginButton);
        Button missButton = view.findViewById(R.id.missButton);
        TextView goToRegister = view.findViewById(R.id.goToRegister);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            authManager.loginUser(email, password, new FirebaseAuthManager.AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    // Переход в MainActivity после успешного входа
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish(); // Закрываем текущую активность
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getActivity(), "Ошибка: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Обработчик нажатий для missButton
        missButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish(); // Закрываем текущую активность
        });



        goToRegister.setOnClickListener(v -> navigator.navigateToRegister());


        return view;
    }
    public String getEmail() {
        return emailInput.getText().toString().trim();
    }

    public static void onLoginSelected(Context context, int id) {
        Intent intent = null;

        if (id == R.id.missButton) {
            intent = new Intent(context, MainActivity.class); // Переход к поиску
        }

    }
}

