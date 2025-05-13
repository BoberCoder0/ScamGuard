package com.example.testapp2.Data.Firebase;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.*;
import java.util.*;

import com.example.testapp2.Data.models.User;

public class FirestoreManager {
    private FirebaseFirestore db;

    public FirestoreManager() {
        db = FirebaseFirestore.getInstance();
    }

    public void saveUserToFirestore(String userId, String email,String nickname) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("search_history", new ArrayList<>()); // Пустая история
        userData.put("nickname", nickname); // имя пользователя
        db.collection("users").document(userId).set(userData);
    }

    public void getUserData(String userId, FirestoreCallback callback) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onSuccess(user);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updateSearchHistory(String userId, String searchQuery) {
        db.collection("users").document(userId)
                .update("search_history", FieldValue.arrayUnion(searchQuery));
    }

    public interface FirestoreCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }
}

