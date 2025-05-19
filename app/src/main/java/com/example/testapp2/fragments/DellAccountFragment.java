package com.example.testapp2.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.testapp2.Activity.Account.AccountActivity;
import com.example.testapp2.Activity.MainActivity;
import com.example.testapp2.Data.Firebase.FirestoreManager;
import com.example.testapp2.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class DellAccountFragment extends DialogFragment {

    private EditText passwordInput;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir != null && dir.delete();
    }

    private void deleteAccount(String password) {
        Context context = getContext();
        if (context == null) return; // Без контекста показать Toast невозможно, прерываем

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirestoreManager firestoreManager = new FirestoreManager();

        if (user == null || password.isEmpty()) {
            if (isAdded()) {
                Toast.makeText(context, "Неправильные данные", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        String email = user.getEmail();
        if (email == null) {
            if (isAdded()) {
                Toast.makeText(context, "Отсутствует Email", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        user.reauthenticate(credential)
                .addOnSuccessListener(authResult -> {
                    db.collection("users").document(user.getUid())
                            .delete()
                            .addOnSuccessListener(unused -> {
                                user.delete()
                                        .addOnSuccessListener(aVoid -> {
                                            if (isAdded()) {
                                                Toast.makeText(context, "Аккаунт удалён", Toast.LENGTH_SHORT).show();
                                            }
                                            // Вызвать метод из Activity
                                            if (getActivity() instanceof AccountActivity) {
                                                ((AccountActivity) getActivity()).onAccountDeleted();
                                            }

                                        })
                                        .addOnFailureListener(e -> {
                                            if (isAdded()) {
                                                Toast.makeText(context, "Ошибка удаления аккаунта", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            })
                            .addOnFailureListener(e -> {
                                if (isAdded()) {
                                    Toast.makeText(context, "Ошибка удаления данных", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(context, "Неверный пароль", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
