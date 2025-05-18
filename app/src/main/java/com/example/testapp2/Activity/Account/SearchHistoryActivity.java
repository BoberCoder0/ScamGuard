package com.example.testapp2.Activity.Account;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapp2.Data.Firebase.SearchHistoryAdapter;
import com.example.testapp2.Data.models.SearchHistoryItem;
import com.example.testapp2.R;
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SearchHistoryActivity.this, "Ошибка загрузки истории", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
