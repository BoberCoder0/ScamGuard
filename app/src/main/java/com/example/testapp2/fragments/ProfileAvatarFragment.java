package com.example.testapp2.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.testapp2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfileAvatarFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 101;
    private ImageButton avatarButton;
    private Uri imageUri;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_avatar, container, false);
        avatarButton = view.findViewById(R.id.avatarButton);

        avatarButton.setOnClickListener(v -> openGallery());

        // Загрузка фото из БД (пример)
        db.collection("users").document("user_id")
                .get()
                .addOnSuccessListener(document -> {
                    String url = document.getString("photoUrl");
                    if (url != null && !url.isEmpty()) {
                        Picasso.get().load(url).into(avatarButton);
                    }
                });

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            avatarButton.setImageURI(imageUri);

            // Пример сохранения URI в Firestore
            db.collection("users").document("FirebaseAuth.getInstance().getCurrentUser().getUid()")
                    .update("photoUrl", imageUri.toString())
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Фото сохранено", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show());
        }
    }
}