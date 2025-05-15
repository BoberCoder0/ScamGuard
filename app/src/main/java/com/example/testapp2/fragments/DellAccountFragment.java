package com.example.testapp2.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.testapp2.Activity.MainActivity;
import com.example.testapp2.Data.Firebase.FirestoreManager;
import com.example.testapp2.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DellAccountFragment extends DialogFragment {

    private EditText passwordInput;

    public Dialog onCreateDialog (Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dell_account, null);

        passwordInput = view.findViewById(R.id.passwordInputText);

        builder.setView(view)
                .setTitle("Удалить аккаунт")
                .setPositiveButton("Удалить", (dialog, id) -> {
                    String password = passwordInput.getText().toString().trim();
                    deleteAccount(password);
                })
                .setNegativeButton("Отмена", (dialog, id) -> {
                    DellAccountFragment.this.getDialog().cancel();
                });

        return builder.create();
    }

    private void deleteAccount (String password) {
        // подключение к бд
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirestoreManager firestoreManager = new FirestoreManager();

        if ( user == null || password.isEmpty() ) {
            Toast.makeText(getContext(), "Неправильные данные", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = user.getEmail();
        if (email == null) {
            Toast.makeText(getContext(), "Отсутсвует Email", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        user.reauthenticate(credential)
                .addOnSuccessListener(authResult -> {
                    // Удаление из Firestore
                    db.collection("users").document(user.getUid())
                            .delete() // намерение
                            .addOnSuccessListener(unused -> {
                                // Удаление из Auth
                                user.delete() // функция
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Аккаунт удалён", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getContext(), MainActivity.class));
                                            requireActivity().finish();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Ошибка удаления аккаунта", Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Ошибка удаления данных", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Неверный пароль", Toast.LENGTH_SHORT).show());
    }
}
