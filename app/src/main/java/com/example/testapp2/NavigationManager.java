package com.example.testapp2;

import android.content.Context;
import android.content.Intent;

import com.example.testapp2.Activity.Learn;
import com.example.testapp2.Activity.MainActivity;
import com.example.testapp2.Activity.Search;
import com.example.testapp2.Activity.Settings;
import com.example.testapp2.Activity.InfoActivity;

public class NavigationManager {

    public static void onNavigationSelected(Context context, int id) {
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
            context.startActivity(intent);
        }
    }
}
