package com.example.testapp2.Activity.Account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentContainerView;

import com.example.testapp2.Activity.MainActivity;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity implements AuthNavigator {

    private EditText nickNameInput, emailInput, passwordInput;
    private TextView googleStatusText, githubStatusText;
    private ProgressBar progressBar;
    private View progressView;
    private ImageButton avaButton;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private GoogleSignInClient googleSignInClient;

    private static final int RC_GOOGLE_SIGN_IN = 123;
    private static final int RC_GITHUB_SIGN_IN = 420;
    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAccountBinding binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Инициализация элементов
        nickNameInput = binding.nickNameInput;

        Button saveButton = binding.saveButton;
        Button resetButton = binding.saveButton2;
        Button dellAcount = binding.dellAccount;
        Button login = binding.buttonLogin;
        Button logOut = binding.logOut;
        Button register = binding.buttonRegister;

        avaButton = binding.avaButton;
        progressBar = binding.progressBar;
        progressView = binding.progressView;
        emailInput = binding.emailInput;
        passwordInput = binding.passwordInput;
        googleStatusText = binding.textView5;
        githubStatusText = binding.textView8;

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");

//        FragmentContainerView fragmentContainer = findViewById(R.id.fragment_container);
//        fragmentContainer.setVisibility(View.VISIBLE);

        // Проверка авторизации пользователя
        if (user == null) {
            showUnauthorizedUI();
        } else {
            showAuthorizedUI();
            loadUserData();
            checkProviderStatus();
        }

        // Настройка Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Обработка входа через Google
        binding.signInWithGoogle.setOnClickListener(v -> signInWithGoogle());
//      // Обработка входа через Git
        binding.signInWithGitHub.setOnClickListener(v -> signInWithGitHub());
        resetButton.setOnClickListener(v -> resetChanges());
//      // Обработка кнопки аватарки
        avaButton.setOnClickListener(v -> openImageChooser());
        binding.imageButton2.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> saveUserData());

        // Обработка кнопки "Выход"
        logOut.setOnClickListener(v -> {
            auth.signOut();
            googleSignInClient.signOut();
            showUnauthorizedUI();
        });

        // обработка кнопок "Вход" и "Регистрация
        login.setOnClickListener(v -> {
            Log.d("AccountActivity", "Login button clicked");
            navigateToLogin();
        });
        register.setOnClickListener(v -> {
            Log.d("AccountActivity", "Register button clicked");
            navigateToRegister();
        });

        dellAcount.setOnClickListener(v -> {
            Log.d("AccountActivity", "Delete account button clicked");

            // Показать диалог
            DellAccountFragment fragment = new DellAccountFragment();
            fragment.show(getSupportFragmentManager(), "DellAccountDialog");
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void uploadImageToFirebase() {
        if (imageUri != null && user != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.VISIBLE);

            StorageReference fileReference = storageRef.child("avatars/" + user.getUid() + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                updateUserAvatar(imageUrl);
                                Picasso.get().load(imageUrl).transform(new CircleTransform()).into(avaButton);
                            }))
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        progressView.setVisibility(View.GONE);
                        Toast.makeText(this, "Ошибка загрузки: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateUserAvatar(String imageUrl) {
        db.collection("users").document(user.getUid())
                .update("photoUrl", imageUrl)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    progressView.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Аватар обновлен", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Ошибка обновления аватара", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            avaButton.setImageURI(imageUri);
            uploadImageToFirebase();
        } else if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Ошибка входа через Google", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) source.recycle();

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

    private void checkProviderStatus() {
        if (user != null) {
            boolean isGoogleConnected = user.getProviderData().stream()
                    .anyMatch(userInfo -> userInfo.getProviderId().equals("google.com"));
            googleStatusText.setText(isGoogleConnected ? "Подключено" : "Не подключено");

            boolean isGithubConnected = user.getProviderData().stream()
                    .anyMatch(userInfo -> userInfo.getProviderId().equals("github.com"));
            githubStatusText.setText(isGithubConnected ? "Подключено" : "Не подключено");
        }
    }

    private void resetChanges() {
        loadUserData();
        Toast.makeText(this, "Изменения сброшены", Toast.LENGTH_SHORT).show();
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    private void signInWithGitHub() {
        String githubClientId = "ваш_client_id";
        String githubRedirectUri = "ваш_redirect_uri";

        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority("github.com")
                .appendPath("login")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", githubClientId)
                .appendQueryParameter("redirect_uri", githubRedirectUri)
                .appendQueryParameter("scope", "user:email")
                .build();

        startActivityForResult(new Intent(Intent.ACTION_VIEW, uri), RC_GITHUB_SIGN_IN);
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
                        checkProviderStatus();
                    } else {
                        Toast.makeText(this, "Ошибка аутентификации", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(FirebaseUser user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", user.getEmail());
        userData.put("name", user.getDisplayName());
        userData.put("photoUrl", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");

        db.collection("users").document(user.getUid())
                .set(userData);
    }
    private void showUnauthorizedUI() {
        findViewById(R.id.nick_name_input).setVisibility(View.GONE);
        findViewById(R.id.email_input).setVisibility(View.GONE);
        findViewById(R.id.password_input).setVisibility(View.GONE);
        findViewById(R.id.save_button).setVisibility(View.GONE);
        findViewById(R.id.save_button2).setVisibility(View.GONE);
        findViewById(R.id.dell_account).setVisibility(View.GONE);
        findViewById(R.id.log_out).setVisibility(View.GONE);
        findViewById(R.id.google_layout).setVisibility(View.GONE);
        findViewById(R.id.git_layout).setVisibility(View.GONE);
        findViewById(R.id.avaButton).setVisibility(View.GONE);

        findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonRegister).setVisibility(View.VISIBLE);
    }

    private void showAuthorizedUI() {
        findViewById(R.id.nick_name_input).setVisibility(View.VISIBLE);
        findViewById(R.id.email_input).setVisibility(View.VISIBLE);
        findViewById(R.id.password_input).setVisibility(View.VISIBLE);
        findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        findViewById(R.id.save_button2).setVisibility(View.VISIBLE);
        findViewById(R.id.dell_account).setVisibility(View.VISIBLE);
        findViewById(R.id.log_out).setVisibility(View.VISIBLE);
        findViewById(R.id.google_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.git_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.avaButton).setVisibility(View.VISIBLE);

        findViewById(R.id.buttonLogin).setVisibility(View.GONE);
        findViewById(R.id.buttonRegister).setVisibility(View.GONE);
    }
    private void loadUserData() {
        if (user == null) return;

        progressBar.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.VISIBLE);

        db.collection("users").document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    progressView.setVisibility(View.GONE);

                    if (task.isSuccessful() && task.getResult() != null) {
                        Map<String, Object> userData = task.getResult().getData();
                        if (userData != null) {
                            nickNameInput.setText((String) userData.getOrDefault("nickname", "введите никнеим"));
                            emailInput.setText((String) userData.get("email"));

                            String photoUrl = (String) userData.get("photoUrl");
                            if (photoUrl != null && !photoUrl.isEmpty()) {
                                Picasso.get().load(photoUrl).transform(new CircleTransform()).into(avaButton);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData() {
        String nickname = nickNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (nickname.isEmpty()) {
            Toast.makeText(this, "Введите ник", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.VISIBLE);

        Map<String, Object> updates = new HashMap<>();
        updates.put("nickname", nickname);
        updates.put("email", email);

        db.collection("users").document(user.getUid())
                .update(updates)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    progressView.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Данные успешно сохранены!", Toast.LENGTH_SHORT).show();

                        if (!email.isEmpty() && !email.equals(user.getEmail())) {
                            user.updateEmail(email);
                        }
                        if (!password.isEmpty()) {
                            user.updatePassword(password);
                        }
                    } else {
                        Toast.makeText(this, "Не удалось обновить данные", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onAccountDeleted() {
        // Очистка SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Очистка кэша
        deleteCache();

        // Мягкий перезапуск активити
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Не вызывай Runtime.exit(0) — это слишком резко
        finish(); // Закроет текущую активити
    }

    private void deleteCache() {
        try {
            File dir = getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
            File extDir = getExternalCacheDir();
            if (extDir != null && extDir.isDirectory()) {
                deleteDir(extDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) return false;
            }
        }
        return dir != null && dir.delete();
    }


    @Override
    public void navigateToLogin() {
        Log.d("AccountActivity", "Navigating to LoginFragment");
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
}

