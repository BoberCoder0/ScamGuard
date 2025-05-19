package com.example.testapp2.Activity.Account;

import android.content.Intent;
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
    private static final int CROP_IMAGE_REQUEST = 2;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAccountBinding binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Инициализация элементов UI
        nickNameInput = binding.nickNameInput;
        Button saveButton = binding.saveButton;
        Button resetButton = binding.saveButton2;
        Button dellAcount = binding.dellAccount;
        Button login = binding.buttonLogin;
        Button register = binding.buttonRegister;
        avaButton = binding.avaButton;
        progressBar = binding.progressBar;
        progressView = binding.progressView;
        emailInput = binding.emailInput;
        passwordInput = binding.passwordInput;
        googleStatusText = binding.textView5;
        githubStatusText = binding.textView8;

        // Инициализация Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");

        // Проверка авторизации пользователя
        if (user == null) {
            showUnauthorizedUI(login, register);
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

        // Обработчики для кнопок входа через соцсети
        findViewById(R.id.signInWithGoogle).setOnClickListener(v -> signInWithGoogle());
        findViewById(R.id.signInWithGitHub).setOnClickListener(v -> signInWithGitHub());

        // Обработчик для кнопки сброса изменений
        resetButton.setOnClickListener(v -> resetChanges());

        // Обработчик для кнопки смены аватарки
        avaButton.setOnClickListener(v -> openImageChooser());

        // Обработчик для кнопки закрытия
        findViewById(R.id.imageButton2).setOnClickListener(v -> finish());
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /*private void startCropActivity(Uri sourceUri) {
        CropImage.activity(sourceUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setRequestedSize(500, 500)
                .setOutputCompressQuality(90)
                .start(this);
    }*/

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
                                Picasso.get()
                                        .load(imageUrl)
                                        .transform(new CircleTransform())
                                        .into(avaButton);
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

        /*if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri sourceUri = data.getData();
            //startCropActivity(sourceUri);
        } else if (requestCode == CROP_IMAGE_REQUEST) {
            //CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //imageUri = result.getUri();
                avaButton.setImageURI(imageUri);
                uploadImageToFirebase();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Ошибка обрезки: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }*/
        /*} else if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    // Класс для круглого преобразования изображения
    public static class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
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

    // Остальные методы класса остаются без изменений
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
        findViewById(R.id.save_button2).setVisibility(View.GONE);
        if (nickNameInput != null) nickNameInput.setVisibility(View.GONE);
        findViewById(R.id.dell_account).setVisibility(View.GONE);
        emailInput.setVisibility(View.GONE);
        passwordInput.setVisibility(View.GONE);

        login.setVisibility(View.VISIBLE);
        register.setVisibility(View.VISIBLE);

        login.setOnClickListener(v -> navigateToLogin());
        register.setOnClickListener(v -> navigateToRegister());
    }

    private void showAuthorizedUI() {
        nickNameInput.setVisibility(View.VISIBLE);
        findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        findViewById(R.id.save_button2).setVisibility(View.VISIBLE);
        if (nickNameInput != null) nickNameInput.setVisibility(View.VISIBLE);
        findViewById(R.id.dell_account).setVisibility(View.VISIBLE);
        emailInput.setVisibility(View.VISIBLE);
        passwordInput.setVisibility(View.VISIBLE);

        findViewById(R.id.buttonLogin).setVisibility(View.GONE);
        findViewById(R.id.buttonRegister).setVisibility(View.GONE);

        findViewById(R.id.save_button).setOnClickListener(v -> saveUserData());
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
                            String nickname = (String) userData.get("nickname");
                            if (nickname != null && !nickname.isEmpty()) {
                                nickNameInput.setText(nickname);
                            } else {
                                nickNameInput.setText("введите никнеим");
                            }

                            String email = (String) userData.get("email");
                            if (email != null) {
                                emailInput.setText(email);
                            }

                            String photoUrl = (String) userData.get("photoUrl");
                            if (photoUrl != null && !photoUrl.isEmpty()) {
                                Picasso.get()
                                        .load(photoUrl)
                                        .transform(new CircleTransform())
                                        .into(avaButton);
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
                        nickNameInput.setText(nickname);
                        Toast.makeText(this, "Данные успешно сохранены!", Toast.LENGTH_SHORT).show();

                        if (!email.isEmpty() && !email.equals(user.getEmail())) {
                            user.updateEmail(email)
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            Toast.makeText(this, "Email обновлен", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "Ошибка обновления email", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        if (!password.isEmpty()) {
                            user.updatePassword(password)
                                    .addOnCompleteListener(passwordTask -> {
                                        if (passwordTask.isSuccessful()) {
                                            Toast.makeText(this, "Пароль обновлен", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "Ошибка обновления пароля", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Не удалось обновить данные", Toast.LENGTH_SHORT).show();
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
}