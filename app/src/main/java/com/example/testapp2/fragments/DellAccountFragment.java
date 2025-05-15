package com.example.testapp2.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

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

    // TODO: Глобально: сделать при нажатии сброса аккаунта сброс кеша такой же как и в настройках
    private EditText passwordInput;

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

    // Метод для очистки кеша приложения (расширенная версия)
    private void clearApplicationCache() {
        try {
            // Очистка внутреннего кеша
            File cacheDir = requireContext().getCacheDir();
            File applicationDir = new File(cacheDir.getParent());
            if (applicationDir.exists()) {
                String[] children = applicationDir.list();
                for (String child : children) {
                    if (!child.equals("lib")) {
                        deleteDir(new File(applicationDir, child));
                    }
                }
            }

            // Очистка внешнего кеша (если есть)
            File externalCacheDir = requireContext().getExternalCacheDir();
            if (externalCacheDir != null) {
                deleteDir(externalCacheDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Вспомогательный метод для рекурсивного удаления директории
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

    // Метод для перезапуска приложения
    private void restartApplication() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
        Runtime.getRuntime().exit(0);
    }

    private void deleteAccount(String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirestoreManager firestoreManager = new FirestoreManager();

        if (user == null || password.isEmpty()) {
            Toast.makeText(getContext(), "Неправильные данные", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = user.getEmail();
        if (email == null) {
            Toast.makeText(getContext(), "Отсутствует Email", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(requireContext(), "Аккаунт удалён", Toast.LENGTH_SHORT).show();

                                            // Очистка данных
                                            FirebaseAuth.getInstance().signOut();
                                            clearApplicationCache();
                                            clearSharedPreferences();

                                            // Перезапуск приложения
                                            restartApplication();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(
                                                getContext(),
                                                "Ошибка удаления аккаунта",
                                                Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e -> Toast.makeText(
                                    getContext(),
                                    "Ошибка удаления данных",
                                    Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(
                        getContext(),
                        "Неверный пароль",
                        Toast.LENGTH_SHORT).show());
    }

    // Новый метод для очистки SharedPreferences
    private void clearSharedPreferences() {
        try {
            SharedPreferences prefs = requireContext().getSharedPreferences(
                    "user_data",
                    MODE_PRIVATE
            );
            prefs.edit().clear().apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}