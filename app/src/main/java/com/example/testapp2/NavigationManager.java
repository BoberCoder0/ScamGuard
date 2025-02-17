package com.example.testapp2;

import android.content.Context;
import android.content.Intent;
import com.example.testapp2.Activity.Account.AccountActivity;
import com.example.testapp2.Activity.Account.History;
import com.example.testapp2.Activity.Learn;
import com.example.testapp2.Activity.Search;
import com.example.testapp2.Activity.Settings;

public class NavigationManager {

    public static void onNavigationSelected(Context context, int id) {
        Intent intent = null;

        if (id == R.id.nav_search) {
            intent = new Intent(context, Search.class);
        } else if (id == R.id.nav_account) {
            intent = new Intent(context, AccountActivity.class);
        } else if (id == R.id.nav_history) {
            intent = new Intent(context, History.class);
        } else if (id == R.id.nav_learn) {
            intent = new Intent(context, Learn.class);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(context, Settings.class);
        }

        if (intent != null) {
            context.startActivity(intent);
        }
    }
}
