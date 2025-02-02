package com.example.testapp2;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;

public class NavigationManager {





    public static void onNavigationSelected(Context context, int id) {
        Intent intent = null;

        if (id == R.id.nav_search) {
            intent = new Intent(context,Search.class); // Переход к поиску
        }
        else if (id == R.id.nav_account) {
            intent = new Intent(context, AccountActivity.class); // Переход к профилю
        }
        else if (id == R.id.nav_history) {
            intent = new Intent(context, History.class); // Переход к профилю
        }
        else if (id == R.id.nav_learn) {
            intent = new Intent(context, Learn.class); // Переход к обучению
        }
        else if (id == R.id.nav_settings) {
            intent = new Intent(context, Settings.class); // Переход к настройкам
        }
        if(intent!= null)
            context.startActivity(intent);
    }
}
