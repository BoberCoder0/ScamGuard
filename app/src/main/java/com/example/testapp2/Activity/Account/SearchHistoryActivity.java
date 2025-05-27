package com.example.testapp2.Activity.Account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchHistoryActivity extends AppCompatActivity implements SearchHistoryAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SearchHistoryAdapter adapter;
    private List<SearchHistoryItem> historyList = new ArrayList<>();
    private static final String PREFS_NAME = "search_history";
    private static final String KEY_HISTORY = "history_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);

        recyclerView = findViewById(R.id.recycler_view);
        adapter = new SearchHistoryAdapter(historyList);
        adapter.setOnItemClickListener(this); // Set the listener
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Сначала загружаем локальную историю
        historyList.addAll(loadLocalHistory());
        adapter.notifyDataSetChanged();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String uid = user.getUid();

            db.collection("users")
                    .document(uid)
                    .collection("search_history")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        historyList.clear(); // Очищаем перед обновлением
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            SearchHistoryItem item = doc.toObject(SearchHistoryItem.class);
                            if (item != null) {
                                historyList.add(item);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        saveLocalHistory(historyList); // Сохраняем в локальный кэш
                    })
                    .addOnFailureListener(e -> {
                        Log.e("SearchHistory", "Ошибка чтения истории: " + e.getMessage());
                        Toast.makeText(this, "Не удалось загрузить историю", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
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

    // Сохранение списка в SharedPreferences
    private void saveLocalHistory(List<SearchHistoryItem> items) {
        Gson gson = new Gson();
        String json = gson.toJson(items);
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(KEY_HISTORY, json)
                .apply();
    }

    // Загрузка списка из SharedPreferences
    private List<SearchHistoryItem> loadLocalHistory() {
        Gson gson = new Gson();
        String json = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(KEY_HISTORY, null);

        Type type = new TypeToken<List<SearchHistoryItem>>(){}.getType();
        return json != null ? gson.fromJson(json, type) : new ArrayList<>();
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

    // Implementation of OnItemClickListener
    @Override
    public void onItemClick(SearchHistoryItem item) {
        if (item != null && item.getPhoneNumber() != null) {
            Intent intent = new Intent(this, Search.class);
            intent.putExtra("SELECTED_PHONE_NUMBER", item.getPhoneNumber());
            startActivity(intent);
        }
    }
}
