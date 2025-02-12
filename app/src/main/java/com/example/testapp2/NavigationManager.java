package com.example.testapp2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.example.testapp2.Activity.Account.AccountActivity;
import com.example.testapp2.Activity.Account.AuthActivity;
import com.example.testapp2.Activity.Account.History;
import com.example.testapp2.Activity.Learn;
import com.example.testapp2.Activity.Search;
import com.example.testapp2.Activity.Settings;
//import com.example.testapp2.fragments.LoginFragment;

import android.content.Context;
import android.content.Intent;

public class NavigationManager {



    //взять емеил адресс

    public static void onNavigationSelected(Context context, int id, String email) {
        Intent intent = null;

        if (id == R.id.nav_search) {
            intent = new Intent(context, Search.class); // Переход к поиску
        }
        else if (id == R.id.nav_account) {
            if (email != null && !email.isEmpty()) {
                intent = new Intent(context, AccountActivity.class);
            } else {
                intent = new Intent(context, AuthActivity.class); // Запуск активности с LoginFragment
            }
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
