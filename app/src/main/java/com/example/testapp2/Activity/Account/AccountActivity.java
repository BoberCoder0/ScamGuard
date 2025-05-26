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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.example.testapp2.utils.LocaleHelper; // Импорт для управления локалью

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

// AccountActivity реализует интерфейс AuthNavigator для навигации между фрагментами аутентификации
public class AccountActivity extends AppCompatActivity implements AuthNavigator {

    // Элементы пользовательского интерфейса
    private EditText nickNameInput, emailInput, passwordInput, currentPasswordInput;
    private TextView googleStatusText, githubStatusText;
    private ProgressBar progressBar; // Индикатор прогресса
    private View progressView; // Затемняющий фон для индикатора прогресса
    private ImageButton avaButton; // Кнопка/изображение для аватара пользователя

    // Экземпляры Firebase
    private FirebaseAuth auth; // Для аутентификации пользователей
    private FirebaseUser user; // Текущий авторизованный пользователь
    private FirebaseFirestore db; // Для работы с базой данных Firestore
    private StorageReference storageRef; // Для работы с хранилищем Firebase Storage (например, аватары)
    private GoogleSignInClient googleSignInClient; // Для входа через Google

    // Коды запросов для startActivityForResult
    private static final int RC_GOOGLE_SIGN_IN = 123; // Код для входа через Google
    private static final int RC_GITHUB_SIGN_IN = 420; // Код для входа через GitHub (пока не полностью реализован)
    private static final int PICK_IMAGE_REQUEST = 1; // Код для выбора изображения из галереи

    private Uri imageUri; // URI выбранного изображения для аватара

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.loadLocale(this); // Загрузка сохраненной локали (языка)

        setTitle(R.string.account); // Установите нужную строку из ресурсов

        // Использование View Binding для доступа к элементам макета
        ActivityAccountBinding binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Инициализация элементов UI из binding
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

        /// НЕ ТРОГАТЬ ОПАСНО ДЛЯ РОБОТОСПОСОБНОСТИ КОДА
        //currentPasswordInput = binding.currentPasswordInput; // Initialize currentPasswordInput

        googleStatusText = binding.textView5;
        githubStatusText = binding.textView8;

        // Инициализация экземпляров Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser(); // Получаем текущего пользователя
        storageRef = FirebaseStorage.getInstance().getReference("profile_images"); // Ссылка на корневую папку для изображений профилей

        // Проверка авторизации пользователя при запуске Activity
        if (user == null) {
            // Если пользователь не авторизован, показываем UI для входа/регистрации
            showUnauthorizedUI();
        } else {
            // Если пользователь авторизован, показываем UI аккаунта
            showAuthorizedUI();
            loadUserData(); // Загружаем данные пользователя
            checkProviderStatus(); // Проверяем подключенные провайдеры (Google, GitHub)
        }

        // Настройка Google Sign-In Options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Запрашиваем токен для аутентификации в Firebase
                .requestEmail() // Запрашиваем адрес электронной почты
                .build();
        // Получаем клиент Google Sign-In
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Обработка клика по кнопке входа через Google
        binding.signInWithGoogle.setOnClickListener(v -> signInWithGoogle());
        // Обработка клика по кнопке входа через GitHub (не полностью реализовано)
        binding.signInWithGitHub.setOnClickListener(v -> signInWithGitHub());
        // Обработка клика по кнопке "Сбросить"
        resetButton.setOnClickListener(v -> resetChanges());
        // Обработка клика по кнопке аватара
        avaButton.setOnClickListener(v -> openImageChooser());
        // Обработка клика по кнопке закрытия Activity
        binding.crossButton.setOnClickListener(v -> finish());
        // Обработка клика по кнопке "Сохранить"
        saveButton.setOnClickListener(v -> saveUserData());

        // Обработка клика по кнопке "Выход"
        logOut.setOnClickListener(v -> {
            auth.signOut(); // Выход из Firebase Auth
            googleSignInClient.signOut(); // Выход из Google Sign-In
            showUnauthorizedUI(); // Показываем UI для неавторизованных
        });

        // Обработка кликов по кнопкам "Вход" и "Регистрация"
        login.setOnClickListener(v -> {
            Log.d("AccountActivity", "Login button clicked");
            navigateToLogin(); // Переходим к фрагменту входа
        });
        register.setOnClickListener(v -> {
            Log.d("AccountActivity", "Register button clicked");
            navigateToRegister(); // Переходим к фрагменту регистрации
        });

        // Обработка клика по кнопке "Удалить аккаунт"
        dellAcount.setOnClickListener(v -> {
            Log.d("AccountActivity", "Delete account button clicked");
            // Показать диалог подтверждения удаления аккаунта
            DellAccountFragment fragment = new DellAccountFragment();
            fragment.show(getSupportFragmentManager(), "DellAccountDialog");
        });
    }

    // Открывает chooser для выбора изображения из галереи
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // Ограничиваем выбор только изображениями
        startActivityForResult(intent, PICK_IMAGE_REQUEST); // Запускаем Activity для выбора изображения
    }

    // Загружает выбранное изображение в Firebase Storage
    private void uploadImageToFirebase() {
        if (imageUri == null || user == null) {
            // Показываем Toast сообщение, если изображение или пользователь отсутствуют
            Toast.makeText(this, getString(R.string.image_or_user_missing), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Пробуем открыть InputStream для проверки доступности файла
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Toast.makeText(this, getString(R.string.unable_to_open_file), Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (FileNotFoundException e) {
            // Обработка ошибки "файл не найден"
            Toast.makeText(this, getString(R.string.file_not_found, e.getMessage()), Toast.LENGTH_SHORT).show();
            return;
        }

        // Показываем индикатор прогресса
        showProgressIndicator();

        // Получаем ссылку на Firebase Storage и создаем путь для файла аватара
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference fileReference = storageRef.child("avatars/" + user.getUid() + ".jpg");

        // Загружаем файл
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // При успешной загрузке, получаем URL скачивания
                    fileReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                // Обновляем URL аватара в Firestore для пользователя
                                updateUserAvatar(imageUrl);
                                // Загружаем и отображаем аватар с помощью Picasso и CircleTransform
                                Picasso.get().load(imageUrl).transform(new CircleTransform()).into(avaButton);
                                // Скрываем индикатор прогресса
                                hideProgressIndicator();
                            })
                            .addOnFailureListener(e -> {
                                // Обработка ошибки получения URL
                                Toast.makeText(this, getString(R.string.url_error, e.getMessage()), Toast.LENGTH_SHORT).show();
                                hideProgressIndicator();
                            });
                })
                .addOnFailureListener(e -> {
                    // Обработка ошибки загрузки файла
                    Toast.makeText(this, getString(R.string.upload_error, e.getMessage()), Toast.LENGTH_LONG).show();
                    Log.e("FirebaseUpload", "Ошибка", e); // Логируем ошибку
                    hideProgressIndicator();
                });
    }

    // Скрывает индикатор прогресса и затемняющий фон
    public void hideProgressIndicator() { // Renamed and made public
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        if (progressView != null) progressView.setVisibility(View.GONE);
    }

    // Показывает индикатор прогресса и затемняющий фон
    public void showProgressIndicator() { // New public method, effectively replacing showProgress
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (progressView != null) progressView.setVisibility(View.VISIBLE);
    }

    // Обновляет URL аватара пользователя в Firestore
    private void updateUserAvatar(String imageUrl) {
        // Обновляем поле "photoUrl" в документе пользователя в коллекции "users"
        db.collection("users").document(user.getUid())
                .update("photoUrl", imageUrl)
                .addOnCompleteListener(task -> {
                    // progressBar.setVisibility(View.GONE); // Скрываем прогресс - Handled by hideProgressIndicator if still needed
                    // progressView.setVisibility(View.GONE); // Скрываем затемнение - Handled by hideProgressIndicator if still needed
                    // Note: updateUserAvatar is usually called after an upload which already hides progress.
                    // If this method could be called standalone and needs progress, it should call show/hideProgressIndicator.
                    // For now, assuming caller handles progress or it's not needed for this specific callback part.
                    if (task.isSuccessful()) {
                        Toast.makeText(this, getString(R.string.avatar_updated), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.avatar_update_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Обработка результатов Activity (выбор изображения, вход через Google)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Обработка результата выбора изображения
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // Получаем URI выбранного изображения
            avaButton.setImageURI(imageUri); // Устанавливаем изображение на кнопку/ImageView
            uploadImageToFirebase(); // Загружаем изображение в Firebase
        }
        // Обработка результата входа через Google
        else if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                } else {
                    Log.w("AccountActivity", "GoogleSignIn.getSignedInAccountFromIntent returned null account.");
                    Toast.makeText(this, getString(R.string.google_sign_in_error), Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                Log.w("AccountActivity", "Google sign in failed", e);
                Toast.makeText(this, "Google sign in failed: " + e.getStatusCode() +
                                ", message: " + e.getMessage() +
                                ", cause: " + (e.getCause() != null ? e.getCause().getMessage() : "null"),
                        Toast.LENGTH_LONG).show();
            }
        }
        // Обработка результата входа через GitHub
        else if (requestCode == RC_GITHUB_SIGN_IN) {
            showProgressIndicator();
            Task<AuthResult> pendingResultTask = auth.getPendingAuthResult();
            if (pendingResultTask != null) {
                pendingResultTask
                        .addOnSuccessListener(authResult -> {
                            Log.d("AccountActivity", "GitHub sign-in or link successful via getPendingAuthResult.");
                            FirebaseUser returnedUser = authResult.getUser();
                            boolean isNewUser = authResult.getAdditionalUserInfo() != null && authResult.getAdditionalUserInfo().isNewUser();
                            handleGitHubSignInSuccess(returnedUser, isNewUser);
                        })
                        .addOnFailureListener(this::handleGitHubSignInFailure);
            } else {
                Log.w("AccountActivity", "GitHub sign-in: No pending auth result. User might have cancelled.");
                Toast.makeText(this, getString(R.string.sign_in_cancelled), Toast.LENGTH_LONG).show();
                hideProgressIndicator();
            }
        }
    }

    // Handles successful GitHub sign-in or linking
    private void handleGitHubSignInSuccess(FirebaseUser returnedUser, boolean isNewUser) {
        if (returnedUser != null) {
            this.user = returnedUser; // Update current user reference
            saveUserToFirestore(this.user);
            showAuthorizedUI();
            loadUserData();
            checkProviderStatus();
            if (isNewUser) {
                Toast.makeText(this, getString(R.string.github_sign_up_success), Toast.LENGTH_SHORT).show();
            } else {
                // This could be a re-authentication or linking, if we can distinguish linking, use link_with_github_success
                // For now, using general success. More specific context would be needed for precise linking message.
                Toast.makeText(this, getString(R.string.github_sign_in_success), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("AccountActivity", "handleGitHubSignInSuccess called with null user.");
            Toast.makeText(this, getString(R.string.github_sign_in_error_generic), Toast.LENGTH_SHORT).show();
        }
        hideProgressIndicator();
    }

    // Handles GitHub sign-in failure
    private void handleGitHubSignInFailure(Exception e) {
        Log.e("AccountActivity", "GitHub Sign-In Error", e);
        // Here you could check for specific exceptions like FirebaseAuthUserCollisionException for linking issues
        Toast.makeText(this, String.format(getString(R.string.github_sign_in_failed), e.getMessage()), Toast.LENGTH_LONG).show();
        hideProgressIndicator();
    }


    // Класс для трансформации изображения в круглую форму (для аватара)
    public static class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            // Определяем размер квадрата (по меньшей стороне изображения)
            int size = Math.min(source.getWidth(), source.getHeight());
            // Вычисляем координаты начала квадрата
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            // Создаем квадратный Bitmap из исходного изображения
            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            // Если новый Bitmap отличается от исходного, освобождаем память исходного
            if (squaredBitmap != source) source.recycle();

            // Создаем пустой Bitmap того же размера и конфигурации
            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
            // Создаем Canvas для рисования на новом Bitmap
            Canvas canvas = new Canvas(bitmap);
            // Создаем Paint для рисования
            Paint paint = new Paint();
            // Применяем BitmapShader, чтобы использовать квадратный Bitmap как текстуру
            paint.setShader(new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            // Включаем сглаживание
            paint.setAntiAlias(true);

            // Вычисляем радиус круга
            float r = size / 2f;
            // Рисуем круг на Canvas, используя Paint с текстурой
            canvas.drawCircle(r, r, r, paint);
            // Освобождаем память квадратного Bitmap
            squaredBitmap.recycle();
            return bitmap; // Возвращаем круглый Bitmap
        }

        // Ключ трансформации
        @Override
        public String key() {
            return "circle";
        }
    }

    // Проверяет, какие провайдеры аутентификации подключены к аккаунту пользователя
    private void checkProviderStatus() {
        if (user != null) {
            // Проверяем, подключен ли Google
            boolean isGoogleConnected = user.getProviderData().stream()
                    .anyMatch(userInfo -> userInfo.getProviderId().equals("google.com"));
            googleStatusText.setText(isGoogleConnected ? getString(R.string.Connected) : getString(R.string.NotConnected));

            // Проверяем, подключен ли GitHub
            boolean isGithubConnected = user.getProviderData().stream()
                    .anyMatch(userInfo -> userInfo.getProviderId().equals("github.com"));
            githubStatusText.setText(isGithubConnected ? getString(R.string.Connected) : getString(R.string.NotConnected));
        }
    }

    // Сбрасывает изменения, перезагружая данные пользователя
    private void resetChanges() {
        loadUserData(); // Перезагружаем данные из Firestore
        Toast.makeText(this, getString(R.string.changes_reset), Toast.LENGTH_SHORT).show(); // Показываем сообщение
    }

    // Запускает процесс входа через Google
    private void signInWithGoogle() {
        googleSignInClient.signOut();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN); // Запускаем Activity Google Sign-In
    }

    // Запускает процесс входа через GitHub (требует настройки GitHub OAuth)
    private void signInWithGitHub() {
        // TODO: Replace "YOUR_GITHUB_CLIENT_ID" with your actual GitHub Client ID from your GitHub OAuth App settings.
        // This ID should ideally be stored in a secure place, like gradle.properties or build config.
        String githubClientId = "Ov23li09J5mt6cgsYhlK"; // Placeholder

        // TODO: Replace "<PROJECT_ID>" with your actual Firebase project ID.
        // This redirect URI must be configured in your GitHub OAuth App settings under "Authorization callback URL".
        // It follows the pattern Firebase uses to handle authentication redirects: https://<PROJECT_ID>.firebaseapp.com/__/auth/handler
        String githubRedirectUri = "https://scamguard-3daf9.firebaseapp.com/__/auth/handler"; // Placeholder

        // Формируем URI для авторизации GitHub OAuth
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority("github.com")
                .appendPath("login")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", githubClientId)
                .appendQueryParameter("redirect_uri", githubRedirectUri)
                .appendQueryParameter("scope", "user:email") // Запрашиваем доступ к email
                .build();

        // Открываем URI в браузере
        startActivityForResult(new Intent(Intent.ACTION_VIEW, uri), RC_GITHUB_SIGN_IN);
        // После авторизации в браузере, GitHub перенаправит на redirect_uri, который должен быть настроен
        // для перехвата Firebase Authentication. Firebase обработает это и вернет результат через onActivityResult.
        // Это упрощенная версия, полной реализации GitHub Auth здесь нет.
    }

    // Аутентификация в Firebase с помощью Google ID токена
    private void firebaseAuthWithGoogle(String idToken) {
        showProgressIndicator();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        boolean isLinking = this.user != null; // Check if we are linking or signing in

        Task<AuthResult> authTask = isLinking
                ? this.user.linkWithCredential(credential)
                : auth.signInWithCredential(credential);

        authTask.addOnCompleteListener(this, task -> {
            hideProgressIndicator();
            if (task.isSuccessful()) {
                this.user = task.getResult().getUser(); // Update the user reference
                saveUserToFirestore(this.user);
                showAuthorizedUI();
                loadUserData();
                checkProviderStatus();
                if (isLinking) {
                    Toast.makeText(AccountActivity.this, getString(R.string.link_with_google_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AccountActivity.this, getString(R.string.google_sign_in_success), Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w("AccountActivity", "firebaseAuthWithGoogle failed (linking: " + isLinking + ")", task.getException());
                if (isLinking) {
                    Toast.makeText(AccountActivity.this, String.format(getString(R.string.link_with_google_failed), task.getException().getMessage()), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AccountActivity.this, getString(R.string.auth_error) + ": " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Сохраняет или обновляет данные пользователя в коллекции "users" в Firestore
    private void saveUserToFirestore(FirebaseUser user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", user.getEmail()); // Email пользователя
        userData.put("name", user.getDisplayName()); // Отображаемое имя пользователя
        userData.put("photoUrl", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : ""); // URL фото аватара

        // Устанавливаем (или обновляем, если документ уже существует) документ пользователя
        db.collection("users").document(user.getUid())
                .set(userData); // Используем set, чтобы создать документ, если его нет, или перезаписать его
    }

    // Показывает элементы UI для неавторизованного пользователя и скрывает элементы для авторизованного
    private void showUnauthorizedUI() {
        // Скрываем поля и кнопки для авторизованного пользователя
        findViewById(R.id.nick_name_input).setVisibility(View.GONE);
        findViewById(R.id.email_input).setVisibility(View.GONE);
        findViewById(R.id.password_input).setVisibility(View.GONE);
        findViewById(R.id.save_button).setVisibility(View.GONE);
        findViewById(R.id.save_button2).setVisibility(View.GONE);
        findViewById(R.id.dell_account).setVisibility(View.GONE);
        findViewById(R.id.log_out).setVisibility(View.GONE);
        findViewById(R.id.google_layout).setVisibility(View.VISIBLE); // Скрываем Layout для статуса Google
        findViewById(R.id.git_layout).setVisibility(View.VISIBLE); // Скрываем Layout для статуса GitHub
        findViewById(R.id.avaButton).setVisibility(View.GONE);

        // Показываем кнопки "Вход" и "Регистрация"
        findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonRegister).setVisibility(View.VISIBLE);

        // Обновляем отступ для google_layout (170dp)
        LinearLayout googleLayout = findViewById(R.id.google_layout);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) googleLayout.getLayoutParams();
        params.topMargin = (int) (160 * getResources().getDisplayMetrics().density); // Конвертируем dp в пиксели
        googleLayout.setLayoutParams(params);

        // Показываем статусы провайдеров
        findViewById(R.id.google_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.git_layout).setVisibility(View.VISIBLE);

        // Подсказки для полей ввода (если бы они были видимы)
        // emailInput.setHint(getString(R.string.EnterNewEmail)); // Пример: если бы поле email было видно
        // passwordInput.setHint(getString(R.string.EnterNewPassword)); // Пример: если бы поле password было видно

        // Текст кнопок обычно устанавливается в XML через @string.
        // Если нужно установить программатически:
        // ((Button)findViewById(R.id.buttonLogin)).setText(getString(R.string.Entrance));
        // ((Button)findViewById(R.id.buttonRegister)).setText(getString(R.string.Registration));
        hideProgressIndicator(); // Ensure progress is hidden when showing unauthorized UI
    }

    // Показывает элементы UI для авторизованного пользователя и скрывает элементы для неавторизованного
    private void showAuthorizedUI() {
        // Показываем поля и кнопки для авторизованного пользователя
        findViewById(R.id.nick_name_input).setVisibility(View.VISIBLE);
        findViewById(R.id.email_input).setVisibility(View.VISIBLE);
        findViewById(R.id.password_input).setVisibility(View.VISIBLE);
        findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        findViewById(R.id.save_button2).setVisibility(View.VISIBLE);
        findViewById(R.id.dell_account).setVisibility(View.VISIBLE);
        findViewById(R.id.log_out).setVisibility(View.VISIBLE);
        findViewById(R.id.google_layout).setVisibility(View.VISIBLE); // Показываем Layout для статуса Google
        findViewById(R.id.git_layout).setVisibility(View.VISIBLE); // Показываем Layout для статуса GitHub
        findViewById(R.id.avaButton).setVisibility(View.VISIBLE);

        // Скрываем кнопки "Вход" и "Регистрация"
        findViewById(R.id.buttonLogin).setVisibility(View.GONE);
        findViewById(R.id.buttonRegister).setVisibility(View.GONE);

        // Обновляем отступ для google_layout (330dp)
        LinearLayout googleLayout = findViewById(R.id.google_layout);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) googleLayout.getLayoutParams();
        params.topMargin = (int) (330 * getResources().getDisplayMetrics().density); // Конвертируем dp в пиксели
        googleLayout.setLayoutParams(params);

        // Показываем статусы провайдеров
        findViewById(R.id.google_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.git_layout).setVisibility(View.VISIBLE);

        // Установка подсказок/текста с использованием строковых ресурсов, если они не установлены в XML
        // Подсказка для nickNameInput обрабатывается в loadUserData
        emailInput.setHint(getString(R.string.EnterNewEmail)); // Устанавливаем подсказку для поля email
        passwordInput.setHint(getString(R.string.EnterNewPassword)); // Устанавливаем подсказку для поля password
        // Текст кнопок обычно устанавливается в XML через @string.
        // Если нужно установить программатически:
        // ((Button)findViewById(R.id.save_button)).setText(getString(R.string.Safe));
        // ((Button)findViewById(R.id.save_button2)).setText(getString(R.string.Reset));
        // ((Button)findViewById(R.id.dell_account)).setText(getString(R.string.DellAccount));
        // ((Button)findViewById(R.id.log_out)).setText(getString(R.string.Exit));
    }

    // Загружает данные пользователя (никнейм, email, фото) из Firestore
    private void loadUserData() {
        if (user == null) return; // Если пользователя нет, выходим

        showProgressIndicator(); // Показываем прогресс

        // Получаем документ пользователя из коллекции "users" по его UID
        db.collection("users").document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    hideProgressIndicator(); // Скрываем прогресс

                    // Проверяем успешность выполнения задачи и наличие результата
                    if (task.isSuccessful() && task.getResult() != null) {
                        Map<String, Object> userData = task.getResult().getData(); // Получаем данные документа
                        if (userData != null) {
                            // Устанавливаем текст в nickNameInput, используя значение из Firestore или дефолтную строку
                            nickNameInput.setText((String) userData.getOrDefault("nickname", getString(R.string.InputYourNickName)));
                            String emailFromStore = (String) userData.get("email");
                            emailInput.setText(emailFromStore != null ? emailFromStore : (this.user != null ? this.user.getEmail() : ""));

                            String photoUrl = (String) userData.get("photoUrl");
                            if (photoUrl != null && !photoUrl.isEmpty()) {
                                Picasso.get().load(photoUrl).transform(new CircleTransform()).into(avaButton);
                            } else if (this.user != null && this.user.getPhotoUrl() != null && !this.user.getPhotoUrl().toString().isEmpty()) {
                                Picasso.get().load(this.user.getPhotoUrl().toString()).transform(new CircleTransform()).into(avaButton);
                            } else {
                                avaButton.setImageResource(R.drawable.default_avatar);
                            }
                        } else if (this.user != null) {
                            // No data in Firestore, but user exists (e.g. first time social sign in)
                            nickNameInput.setText(this.user.getDisplayName() != null ? this.user.getDisplayName() : getString(R.string.InputYourNickName));
                            emailInput.setText(this.user.getEmail());
                            if (this.user.getPhotoUrl() != null && !this.user.getPhotoUrl().toString().isEmpty()) {
                                Picasso.get().load(this.user.getPhotoUrl().toString()).transform(new CircleTransform()).into(avaButton);
                            } else {
                                avaButton.setImageResource(R.drawable.default_avatar);
                            }
                        } else {
                            // Both userData and this.user are null. Should ideally not happen in authorized state.
                            nickNameInput.setText(getString(R.string.InputYourNickName));
                            emailInput.setText("");
                            avaButton.setImageResource(R.drawable.default_avatar);
                        }
                    } else {
                        Log.w("AccountActivity", "loadUserData:onComplete: Failed to load user data from Firestore.", task.getException());
                        Toast.makeText(this, getString(R.string.data_load_error) + (task.getException() != null ? ": " + task.getException().getMessage() : ""), Toast.LENGTH_LONG).show();
                        if (this.user != null) {
                            // Firestore failed, but we have a Firebase user. Populate with that.
                            nickNameInput.setText(this.user.getDisplayName() != null ? this.user.getDisplayName() : getString(R.string.InputYourNickName));
                            emailInput.setText(this.user.getEmail());
                            if (this.user.getPhotoUrl() != null && !this.user.getPhotoUrl().toString().isEmpty()) {
                                Picasso.get().load(this.user.getPhotoUrl().toString()).transform(new CircleTransform()).into(avaButton);
                            } else {
                                avaButton.setImageResource(R.drawable.default_avatar);
                            }
                        } else {
                            // No data anywhere
                            nickNameInput.setText(getString(R.string.InputYourNickName));
                            emailInput.setText("");
                            avaButton.setImageResource(R.drawable.default_avatar);
                        }
                    }
                });
    }

    // Сохраняет данные пользователя (никнейм, email, пароль)
    private void saveUserData() {
        if (user == null) {
            Toast.makeText(this, getString(R.string.user_not_signed_in), Toast.LENGTH_SHORT).show();
            return;
        }

        String newNickname = nickNameInput.getText().toString().trim();
        String newEmail = emailInput.getText().toString().trim();
        String newPassword = passwordInput.getText().toString().trim();

        boolean nicknameChanged = !newNickname.equals(user.getDisplayName()) && !newNickname.isEmpty();
        boolean emailChanged = !newEmail.equals(user.getEmail()) && !newEmail.isEmpty();
        boolean passwordChanged = !newPassword.isEmpty();

        if (!nicknameChanged && !emailChanged && !passwordChanged) {
            Toast.makeText(this, getString(R.string.no_changes_to_save), Toast.LENGTH_SHORT).show();
            return;
        }

        if (nickNameInput.getText().toString().trim().isEmpty()){
            Toast.makeText(this, getString(R.string.enter_nickname), Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressIndicator();
        Map<String, Object> updates = new HashMap<>();
        updates.put("nickname", newNickname);

        db.collection("users").document(user.getUid()).update(updates)
                .addOnCompleteListener(task -> {
                    hideProgressIndicator();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, getString(R.string.nickname_update_success), Toast.LENGTH_SHORT).show();
                        loadUserData();
                    } else {
                        Toast.makeText(this, String.format(getString(R.string.nickname_update_failed_reason), task.getException().getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void performSensitiveUpdates(String newNickname, boolean nicknameChanged, String newEmail, boolean emailChanged, String newPassword, boolean passwordChanged) {
        Task<Void> nicknameUpdateTask = nicknameChanged ?
                db.collection("users").document(user.getUid()).update("nickname", newNickname) :
                com.google.android.gms.tasks.Tasks.forResult(null);

        nicknameUpdateTask.addOnCompleteListener(firestoreTask -> {
            boolean firestoreSuccess = firestoreTask.isSuccessful();
            if (!firestoreSuccess && nicknameChanged) {
                Log.w("AccountActivity", "Nickname update to Firestore failed.", firestoreTask.getException());
            }

            Task<Void> emailUpdateTask = emailChanged ?
                    user.updateEmail(newEmail) :
                    com.google.android.gms.tasks.Tasks.forResult(null);

            emailUpdateTask.addOnCompleteListener(emailTask -> {
                boolean emailSuccess = emailTask.isSuccessful();
                if (!emailSuccess && emailChanged) {
                    Log.w("AccountActivity", "Email update failed.", emailTask.getException());
                }

                Task<Void> passwordUpdateTask = passwordChanged ?
                        user.updatePassword(newPassword) :
                        com.google.android.gms.tasks.Tasks.forResult(null);

                passwordUpdateTask.addOnCompleteListener(passwordTask -> {
                    boolean passwordSuccess = passwordTask.isSuccessful();
                    if (!passwordSuccess && passwordChanged) {
                        Log.w("AccountActivity", "Password update failed.", passwordTask.getException());
                    }
                    hideProgressIndicator();
                    showUpdateResult(firestoreSuccess || !nicknameChanged, emailSuccess || !emailChanged, passwordSuccess || !passwordChanged,
                                     nicknameChanged, emailChanged, passwordChanged,
                                     firestoreTask.getException(), emailTask.getException(), passwordTask.getException());
                    loadUserData();
                });
            });
        });
    }

    private void showUpdateResult(boolean firestoreSuccess, boolean emailSuccess, boolean passwordSuccess,
                                  boolean nicknameAttempted, boolean emailAttempted, boolean passwordAttempted,
                                  Exception firestoreEx, Exception emailEx, Exception passwordEx) {
        StringBuilder resultMessage = new StringBuilder();
        boolean anyOperationAttempted = nicknameAttempted || emailAttempted || passwordAttempted;

        if (nicknameAttempted) {
            resultMessage.append(getString(firestoreSuccess ? R.string.update_result_nickname_success_details : R.string.update_result_nickname_failed_details));
            if (!firestoreSuccess && firestoreEx != null) resultMessage.append(": ").append(firestoreEx.getMessage());
            resultMessage.append(" ");
        }
        if (emailAttempted) {
            resultMessage.append(getString(emailSuccess ? R.string.update_result_email_success_details : R.string.update_result_email_failed_details));
            if (!emailSuccess && emailEx != null) resultMessage.append(": ").append(emailEx.getMessage());
            resultMessage.append(" ");
        }
        if (passwordAttempted) {
            resultMessage.append(getString(passwordSuccess ? R.string.update_result_password_success_details : R.string.update_result_password_failed_details));
            if (!passwordSuccess && passwordEx != null) resultMessage.append(": ").append(passwordEx.getMessage());
            resultMessage.append(" ");
        }

        String finalMessage = resultMessage.toString().trim();

        if (finalMessage.isEmpty() && !anyOperationAttempted) {
            Toast.makeText(this, getString(R.string.no_changes_attempted), Toast.LENGTH_SHORT).show();
            return;
        }
        
        boolean allSuccess = (!nicknameAttempted || firestoreSuccess) && 
                             (!emailAttempted || emailSuccess) && 
                             (!passwordAttempted || passwordSuccess);

        if (allSuccess && anyOperationAttempted) {
            Toast.makeText(this, getString(R.string.data_saved) + (finalMessage.isEmpty() ? "" : " (" + finalMessage + ")"), Toast.LENGTH_LONG).show();
        } else if (!allSuccess) { // Some failure occurred
            Toast.makeText(this, getString(R.string.data_update_failed) + ": " + finalMessage, Toast.LENGTH_LONG).show();
        } else if (finalMessage.isEmpty() && anyOperationAttempted) { // All attempted operations were successful, but no specific message was generated (e.g. only nickname changed and it was successful)
             Toast.makeText(this, getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
        }


    }

    // Вызывается после успешного удаления аккаунта
    public void onAccountDeleted() {
        // Очистка SharedPreferences (данных локального сеанса)
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE); // "user_data" - имя файла настроек
        prefs.edit().clear().apply(); // Очищаем все данные и применяем изменения

        // Очистка кэша приложения
        deleteCache();

        // "Мягкий" перезапуск приложения (возвращение к MainActivity и очистка стека активностей)
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Очищаем стек и создаем новую задачу
        startActivity(intent); // Запускаем MainActivity

        // Не используйте Runtime.exit(0) — это аварийное завершение процесса.
        finish(); // Закрываем текущую активность
    }

    // Удаляет кэш приложения (внутренний и внешний)
    private void deleteCache() {
        try {
            // Удаляем внутренний кэш
            File dir = getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir); // Рекурсивно удаляем содержимое папки
            }
            // Удаляем внешний кэш
            File extDir = getExternalCacheDir();
            if (extDir != null && extDir.isDirectory()) {
                deleteDir(extDir); // Рекурсивно удаляем содержимое папки
            }
        } catch (Exception e) {
            e.printStackTrace(); // Логируем возможные ошибки при удалении
        }
    }

    // Рекурсивно удаляет папку и все ее содержимое
    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list(); // Получаем список файлов и папок внутри
            if (children != null) {
                for (String child : children) {
                    // Рекурсивно вызываем deleteDir для каждого элемента
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) return false; // Если удаление дочернего элемента не удалось, прекращаем
                }
            }
        }
        // Удаляем саму папку (или файл, если это был файл)
        return dir != null && dir.delete();
    }

    // Реализация метода интерфейса AuthNavigator: Переход к фрагменту входа
    @Override
    public void navigateToLogin() {
        Log.d("AccountActivity", "Navigating to LoginFragment");
        // Заменяем содержимое fragment_container на LoginFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .addToBackStack(null) // Добавляем транзакцию в Back Stack
                .commit(); // Применяем изменения
    }

    // Реализация метода интерфейса AuthNavigator: Переход к фрагменту регистрации
    @Override
    public void navigateToRegister() {
        Log.d("AccountActivity", "Navigating to RegisterFragment");
        // Заменяем содержимое fragment_container на RegisterFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RegisterFragment())
                .addToBackStack(null) // Добавляем транзакцию в Back Stack
                .commit(); // Применяем изменения
        // TODO: что бы сделать комит
    }
}