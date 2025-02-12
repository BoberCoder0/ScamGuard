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

import com.example.testapp2.ui.AuthNavigator;
import com.google.firebase.auth.FirebaseAuth;
import com.example.testapp2.Data.Firebase.FirebaseAuthManager;
import com.example.testapp2.Activity.Account.AccountActivity;
import com.example.testapp2.Activity.Account.AuthActivity;
import com.example.testapp2.R;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private FirebaseAuthManager authManager;
    private AuthNavigator navigator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        authManager = new FirebaseAuthManager();
        navigator = (AuthNavigator) getActivity();

        EditText emailInput = view.findViewById(R.id.email);
        EditText passwordInput = view.findViewById(R.id.password);
        Button loginButton = view.findViewById(R.id.loginButton);
        TextView goToRegister = view.findViewById(R.id.goToRegister);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            authManager.loginUser(email, password, new FirebaseAuthManager.AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    startActivity(new Intent(getActivity(), AccountActivity.class));
                    getActivity().finish();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getActivity(), "Ошибка: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        goToRegister.setOnClickListener(v -> navigator.navigateToRegister());

        return view;
    }
}

