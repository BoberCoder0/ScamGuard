package com.example.testapp2.Data.Firebase;

import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthManager {
    private FirebaseAuth auth;

    public FirebaseAuthManager() {
        auth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void registerUser(String email, String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(auth.getCurrentUser());
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void loginUser(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(auth.getCurrentUser());
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void logoutUser() {
        auth.signOut();
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }
}
