package com.example.testapp2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.testapp2.R;
import com.squareup.picasso.Picasso;
import com.example.testapp2.Activity.Account.AccountActivity.CircleTransform;

public class AvatarFragment extends Fragment {
    private ImageButton avatarButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_avatar, container, false);
        avatarButton = view.findViewById(R.id.avatarButton);

        // Пример: загрузка аватара (замени на свой url)
        String photoUrl = getArguments() != null ? getArguments().getString("photoUrl") : null;
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Picasso.get().load(photoUrl).transform(new CircleTransform()).into(avatarButton);
        }

        // Обработка клика
        avatarButton.setOnClickListener(v -> {
            // Вызови нужный метод в Activity, например, открыть выбор фото
            if (getActivity() instanceof com.example.testapp2.Activity.Account.AccountActivity) {
                ((com.example.testapp2.Activity.Account.AccountActivity) getActivity()).checkAndRequestPermissions();
            }
        });

        return view;
    }
}