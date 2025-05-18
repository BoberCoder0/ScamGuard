package com.example.testapp2.Activity.Account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testapp2.R;
import com.example.testapp2.databinding.ActivityAccountBinding;
import com.example.testapp2.fragments.DellAccountFragment;
import com.example.testapp2.fragments.LoginFragment;
import com.example.testapp2.fragments.RegisterFragment;
import com.example.testapp2.ui.AuthNavigator;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity implements AuthNavigator {

    private EditText nickNameInput,emailInput, passwordInput;
    private ImageView profileIcon;
    private TextView currentNickName;
    private ProgressBar progressBar;
    private View progressView;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_GOOGLE_SIGN_IN = 123;
    private static final int RC_GITHUB_SIGN_IN = 420;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAccountBinding binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Инициализация элементов UI
        nickNameInput = binding.nickNameInput;
        Button saveButton = binding.saveButton;
        currentNickName = binding.currentNickName;
        Button dellAcount = binding.dellAccount;
        Button login = binding.buttonLogin;
        Button register = binding.buttonRegister;
        progressBar = binding.progressBar;
        progressView = binding.progressView;
        emailInput = binding.emailInput;
        passwordInput = binding.passwordInput;
        profileIcon = binding.imageView;

        // Инициализация Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Проверка авторизации пользователя
        if (user == null) {
            showUnauthorizedUI(login, register);
        } else {
            showAuthorizedUI();
            loadUserData();
        }

        // Настройка Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Обработчики для кнопок входа через соцсети
        findViewById(R.id.signInWithGoogle).setOnClickListener(v -> signInWithGoogle());
        findViewById(R.id.signInWithGitHub).setOnClickListener(v -> signInWithGitHub());

        // Инициализация Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Проверка авторизации пользователя
        if (user == null) {
            showUnauthorizedUI(login, register);
        } else {
            showAuthorizedUI();
            loadUserData();
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    private void signInWithGitHub() {
        // Создайте Intent для аутентификации через GitHub
        // Это может быть через WebView или через библиотеку OAuth
        String githubClientId = "ваш_client_id";
        String githubRedirectUri = "ваш_redirect_uri";

        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme("https")
                .authority("github.com")
                .appendPath("login")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", githubClientId)
                .appendQueryParameter("redirect_uri", githubRedirectUri)
                .appendQueryParameter("scope", "user:email");

        Intent intent = new Intent(Intent.ACTION_VIEW, uriBuilder.build());
        startActivityForResult(intent, RC_GITHUB_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            // Обработка Google Sign-In
        } else if (requestCode == RC_GITHUB_SIGN_IN) {
            // Обработка GitHub Sign-In
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                if (uri.toString().startsWith("ваш_redirect_uri")) {
                    String code = uri.getQueryParameter("code");
                    if (code != null) {
                        exchangeCodeForToken(code);
                    } else if (uri.getQueryParameter("error") != null) {
                        Toast.makeText(this, "GitHub login failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void exchangeCodeForToken(String code) {
        // Здесь реализуйте обмен кода на токен доступа
        // Затем используйте токен для аутентификации в Firebase
        // AuthCredential credential = GithubAuthProvider.getCredential(token);
        // auth.signInWithCredential(credential)...
    }

    private void firebaseAuthWithGoogle(String idToken) {
        progressBar.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.VISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    progressView.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        user = auth.getCurrentUser();
                        saveUserToFirestore(user);
                        showAuthorizedUI();
                        loadUserData();
                    } else {
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        Log.w("AccountActivity", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void saveUserToFirestore(FirebaseUser user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", user.getEmail());
        userData.put("name", user.getDisplayName());
        userData.put("photoUrl", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");

        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> Log.d("AccountActivity", "User data saved to Firestore"))
                .addOnFailureListener(e -> Log.e("AccountActivity", "Error saving user data", e));
    }

    private void showUnauthorizedUI(Button login, Button register) {
        nickNameInput.setVisibility(View.GONE);
        findViewById(R.id.save_button).setVisibility(View.GONE);
        currentNickName.setVisibility(View.GONE);
        findViewById(R.id.dell_account).setVisibility(View.GONE);

        login.setVisibility(View.VISIBLE);
        register.setVisibility(View.VISIBLE);

        login.setOnClickListener(v -> navigateToLogin());
        register.setOnClickListener(v -> navigateToRegister());
    }

    private void showAuthorizedUI() {
        nickNameInput.setVisibility(View.VISIBLE);
        findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        currentNickName.setVisibility(View.VISIBLE);
        findViewById(R.id.dell_account).setVisibility(View.VISIBLE);

        findViewById(R.id.buttonLogin).setVisibility(View.GONE);
        findViewById(R.id.buttonRegister).setVisibility(View.GONE);

        findViewById(R.id.save_button).setOnClickListener(v -> saveNickName());
        findViewById(R.id.dell_account).setOnClickListener(v -> {
            DellAccountFragment dialog = new DellAccountFragment();
            dialog.show(getSupportFragmentManager(), "DellAccountFragment");
        });
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.VISIBLE);
        //Log.d("ProgressDebug1", "Progress visibility: " + progressView.getVisibility()); // 0 (VISIBLE), 4 (INVISIBLE) или 8 (GONE)

        db.collection("users").document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    progressView.setVisibility(View.GONE);
                    //Log.d("ProgressDebug2", "Progress visibility: " + progressView.getVisibility()); // 0 (VISIBLE), 4 (INVISIBLE) или 8 (GONE)
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String nickname = task.getResult().getString("nickname");
                            if (nickname != null && !nickname.isEmpty()) {
                                currentNickName.setText("Ваш ник: " + nickname);
                                nickNameInput.setText(nickname);
                            } else {
                                currentNickName.setText("Вы не указали свой никнейм");
                            }
                        }
                    } else {
                        Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                        Log.e("AccountActivity", "Ошибка загрузки данных", task.getException());
                    }
                });
    }

    private void saveNickName() {
        String nickname = nickNameInput.getText().toString().trim();
        if (nickname.isEmpty()) {
            Toast.makeText(this, "Введите ник", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.GONE);
        //Log.d("ProgressDebug3", "Progress visibility: " + progressView.getVisibility()); // 0 (VISIBLE), 4 (INVISIBLE) или 8 (GONE)

        Map<String, Object> updates = new HashMap<>();
        updates.put("nickname", nickname);

        db.collection("users").document(user.getUid())
                .update(updates)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    progressView.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        currentNickName.setText("Ваш ник: " + nickname);
                        Toast.makeText(this, "Ник успешно сохранен!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Не удалось обновить никнейм", Toast.LENGTH_SHORT).show();
                        Log.e("AccountActivity", "Ошибка сохранения никнейма", task.getException());
                    }
                });
    }

    @Override
    public void navigateToLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void navigateToRegister() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RegisterFragment())
                .addToBackStack(null)
                .commit();
    }

    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_accoutn) {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            bottomNav.post(() -> {
                Menu menu = bottomNav.getMenu();
                for (int i = 0; i < menu.size(); i++) {
                    menu.getItem(i).setChecked(false);
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}