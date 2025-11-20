package com.example.testapp2.Activity.Account;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testapp2.R;
import com.example.testapp2.fragments.ProfileAvatarFragment;

public class ProfileActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ProfileAvatarFragment())
                .commit();
    }
}