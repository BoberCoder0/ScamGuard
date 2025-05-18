package com.example.testapp2.Activity.Account;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapp2.Activity.Learn;
import com.example.testapp2.Activity.MainActivity;
import com.example.testapp2.Activity.Search;
import com.example.testapp2.Activity.Settings;
import com.example.testapp2.Data.Firebase.SearchHistoryAdapter;
import com.example.testapp2.Data.models.SearchHistoryItem;
import com.example.testapp2.R;
import com.example.testapp2.fragments.LoginFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchHistoryAdapter adapter;
    private List<SearchHistoryItem> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);

        recyclerView = findViewById(R.id.recycler_view);
        adapter = new SearchHistoryAdapter(historyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("search_history")
                    .child(user.getUid());

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    historyList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        SearchHistoryItem item = dataSnapshot.getValue(SearchHistoryItem.class);
                        if (item != null) {
                            historyList.add(item);
                        }
                    }
                    Collections.reverse(historyList); // последние сверху
                    adapter.notifyDataSetChanged();

                    Toast.makeText(SearchHistoryActivity.this, "Loaded items: " + historyList.size(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SearchHistoryActivity.this, "Ошибка загрузки истории", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Переход по кнопкам в нижнем тулбаре
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.nav_history);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.nav_learn) {
                    startActivity(new Intent(getApplicationContext(), Learn.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (id == R.id.nav_search) {
                    startActivity(new Intent(getApplicationContext(), Search.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (id == R.id.nav_settings) {
                    startActivity(new Intent(getApplicationContext(), Settings.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (id == R.id.nav_history) {
                    return true;
                }
                else if (id == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }
    // Для перехода по иконке аккаунта
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.up_toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_accoutn) {
            startActivity(new Intent(getApplicationContext(), AccountActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private String getUserEmail() {
        // Логика для получения email пользователя
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.email);
        if (loginFragment != null) {
            return loginFragment.getEmail();
        }
        return null; // или возвращайте пустую строку, если email не найден
    }
}
