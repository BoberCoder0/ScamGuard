package com.example.testapp2;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.example.testapp2.Activity.Learn;
import com.example.testapp2.Activity.MainActivity;
import com.example.testapp2.Activity.Search;
import com.example.testapp2.Activity.Settings;
import com.example.testapp2.Activity.InfoActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationManager {

    public static void onNavigationSelected(Context context, int id) {
        Log.d("NavigationManager", "Switching to: " + id);

        if (isCurrentActivity(context, id)) {
            Log.d("NavigationManager", "Страница уже открыта, не переключаемся.");
            return;
        }

        Intent intent = null;
        if (id == R.id.nav_home) {
            intent = new Intent(context, MainActivity.class);
        } else if (id == R.id.nav_learn) {
            intent = new Intent(context, Learn.class);
        } else if (id == R.id.nav_search) {
            intent = new Intent(context, Search.class);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(context, Settings.class);
        } else if (id == R.id.nav_info) {
            intent = new Intent(context, InfoActivity.class);
        }

        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        }
    }

    // Функция проверяет, открыта ли уже нужная активити
    private static boolean isCurrentActivity(Context context, int id) {
        if (context instanceof MainActivity && id == R.id.nav_home) return true;
        if (context instanceof Learn && id == R.id.nav_learn) return true;
        if (context instanceof Search && id == R.id.nav_search) return true;
        if (context instanceof Settings && id == R.id.nav_settings) return true;
        if (context instanceof InfoActivity && id == R.id.nav_info) return true;
        return false;
    }

    public static void resetNavigationSelection(BottomNavigationView bottomNav) {
        if (bottomNav != null) {
            Menu menu = bottomNav.getMenu();
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setChecked(false);
            }
        }
    }

}
