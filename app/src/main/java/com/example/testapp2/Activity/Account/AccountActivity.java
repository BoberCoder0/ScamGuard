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
//import com.theartofdev.edmodo.cropper.CropImage; // Закомментированный импорт, возможно, не используется
//import com.theartofdev.edmodo.cropper.CropImageView; // Закомментированный импорт, возможно, не используется
import com.example.testapp2.utils.LocaleHelper; // Импорт для управления локалью

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

// AccountActivity реализует интерфейс AuthNavigator для навигации между фрагментами аутентификации
public class AccountActivity extends AppCompatActivity implements AuthNavigator {

    // Элементы пользовательского интерфейса
    private EditText nickNameInput, emailInput, passwordInput;
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
        // Загрузка сохраненной локали (языка)
        LocaleHelper.loadLocale(this);

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
        progressBar.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.VISIBLE);

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
                                hideProgress();
                            })
                            .addOnFailureListener(e -> {
                                // Обработка ошибки получения URL
                                Toast.makeText(this, getString(R.string.url_error, e.getMessage()), Toast.LENGTH_SHORT).show();
                                hideProgress();
                            });
                })
                .addOnFailureListener(e -> {
                    // Обработка ошибки загрузки файла
                    Toast.makeText(this, getString(R.string.upload_error, e.getMessage()), Toast.LENGTH_LONG).show();
                    Log.e("FirebaseUpload", "Ошибка", e); // Логируем ошибку
                    hideProgress();
                });
    }

    // Скрывает индикатор прогресса и затемняющий фон
    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        progressView.setVisibility(View.GONE);
    }

    // Обновляет URL аватара пользователя в Firestore
    private void updateUserAvatar(String imageUrl) {
        // Обновляем поле "photoUrl" в документе пользователя в коллекции "users"
        db.collection("users").document(user.getUid())
                .update("photoUrl", imageUrl)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE); // Скрываем прогресс
                    progressView.setVisibility(View.GONE); // Скрываем затемнение
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
                // Получаем аккаунт Google из результата
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Аутентифицируемся в Firebase с помощью Google ID токена
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Обработка ошибки входа через Google
                Toast.makeText(this, getString(R.string.google_sign_in_error), Toast.LENGTH_SHORT).show();
            }
        }
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
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN); // Запускаем Activity Google Sign-In
    }

    // Запускает процесс входа через GitHub (требует настройки GitHub OAuth)
    private void signInWithGitHub() {
        // !!! ВАЖНО: client_id и redirect_uri должны быть получены из настроек вашего GitHub OAuth приложения
        // и, возможно, храниться не напрямую в коде, а в ресурсах или секретах приложения.
        String githubClientId = "ваш_client_id"; // Замените на ваш GitHub Client ID
        String githubRedirectUri = "ваш_redirect_uri"; // Замените на ваш GitHub Redirect URI (например, https://YOUR_PROJECT_ID.firebaseapp.com/__/auth/handler)

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
        progressBar.setVisibility(View.VISIBLE); // Показываем прогресс
        progressView.setVisibility(View.VISIBLE);

        // Создаем учетные данные Firebase из Google ID токена
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        // Выполняем вход в Firebase с этими учетными данными
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE); // Скрываем прогресс
                    progressView.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        // Вход успешен, получаем текущего пользователя
                        user = auth.getCurrentUser();
                        // Сохраняем/обновляем данные пользователя в Firestore
                        saveUserToFirestore(user);
                        // Показываем UI для авторизованного пользователя
                        showAuthorizedUI();
                        loadUserData(); // Загружаем данные пользователя
                        checkProviderStatus(); // Проверяем провайдеров
                    } else {
                        // Ошибка аутентификации
                        Toast.makeText(this, getString(R.string.auth_error), Toast.LENGTH_SHORT).show();
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
        findViewById(R.id.google_layout).setVisibility(View.GONE); // Скрываем Layout для статуса Google
        findViewById(R.id.git_layout).setVisibility(View.GONE); // Скрываем Layout для статуса GitHub
        findViewById(R.id.avaButton).setVisibility(View.GONE);

        // Показываем кнопки "Вход" и "Регистрация"
        findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonRegister).setVisibility(View.VISIBLE);

        // Подсказки для полей ввода (если бы они были видимы)
        // emailInput.setHint(getString(R.string.EnterNewEmail)); // Пример: если бы поле email было видно
        // passwordInput.setHint(getString(R.string.EnterNewPassword)); // Пример: если бы поле password было видно

        // Текст кнопок обычно устанавливается в XML через @string.
        // Если нужно установить программатически:
        // ((Button)findViewById(R.id.buttonLogin)).setText(getString(R.string.Entrance));
        // ((Button)findViewById(R.id.buttonRegister)).setText(getString(R.string.Registration));
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

        progressBar.setVisibility(View.VISIBLE); // Показываем прогресс
        progressView.setVisibility(View.VISIBLE);

        // Получаем документ пользователя из коллекции "users" по его UID
        db.collection("users").document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE); // Скрываем прогресс
                    progressView.setVisibility(View.GONE);

                    // Проверяем успешность выполнения задачи и наличие результата
                    if (task.isSuccessful() && task.getResult() != null) {
                        Map<String, Object> userData = task.getResult().getData(); // Получаем данные документа
                        if (userData != null) {
                            // Устанавливаем текст в nickNameInput, используя значение из Firestore или дефолтную строку
                            nickNameInput.setText((String) userData.getOrDefault("nickname", getString(R.string.InputYourNickName)));
                            // Устанавливаем текст в emailInput
                            emailInput.setText((String) userData.get("email"));

                            // Загружаем фото аватара, если оно есть
                            String photoUrl = (String) userData.get("photoUrl");
                            if (photoUrl != null && !photoUrl.isEmpty()) {
                                // Используем Picasso с CircleTransform для загрузки и отображения круглого аватара
                                Picasso.get().load(photoUrl).transform(new CircleTransform()).into(avaButton);
                            }
                        }
                    } else {
                        // Ошибка загрузки данных
                        Toast.makeText(this, getString(R.string.data_load_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Сохраняет данные пользователя (никнейм, email, пароль)
    private void saveUserData() {
        String nickname = nickNameInput.getText().toString().trim(); // Получаем никнейм
        String email = emailInput.getText().toString().trim(); // Получаем email
        String password = passwordInput.getText().toString().trim(); // Получаем пароль

        // Проверка на пустое поле никнейма
        if (nickname.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_nickname), Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE); // Показываем прогресс
        progressView.setVisibility(View.VISIBLE);

        Map<String, Object> updates = new HashMap<>();
        updates.put("nickname", nickname); // Добавляем никнейм для обновления в Firestore

        // Обновляем никнейм в Firestore
        db.collection("users").document(user.getUid())
                .update(updates)
                .addOnCompleteListener(task -> {
                    // После обновления никнейма, обрабатываем обновление email и пароля
                    // Обработка обновления email в Firebase Auth
                    if (!email.isEmpty() && user != null && !email.equals(user.getEmail())) {
                        user.updateEmail(email)
                                .addOnCompleteListener(emailUpdateTask -> {
                                    // Передаем результат обновления email дальше
                                    handleUserDataUpdates(task.isSuccessful(), emailUpdateTask.isSuccessful(), !password.isEmpty());
                                });
                    } else {
                        // Если email не нужно обновлять, переходим сразу к обработке пароля и результата никнейма
                        handleUserDataUpdates(task.isSuccessful(), true, !password.isEmpty());
                    }
                });
    }

    // Вспомогательный метод для обработки результатов нескольких асинхронных операций обновления данных
    private void handleUserDataUpdates(boolean firestoreSuccess, boolean emailSuccess, boolean passwordExists) {
        // Если есть пароль для обновления и пользователь существует
        if (passwordExists && user != null) {
            String password = passwordInput.getText().toString().trim();
            if (!password.isEmpty()) {
                // Обновляем пароль в Firebase Auth
                user.updatePassword(password)
                        .addOnCompleteListener(passwordUpdateTask -> {
                            progressBar.setVisibility(View.GONE); // Скрываем прогресс
                            progressView.setVisibility(View.GONE);
                            // Показываем общий результат всех обновлений
                            showUpdateResult(firestoreSuccess, emailSuccess, passwordUpdateTask.isSuccessful());
                        });
            } else {
                // Если пароль был в поле, но оказался пустым после trim(), считаем, что обновление пароля не требовалось
                progressBar.setVisibility(View.GONE);
                progressView.setVisibility(View.GONE);
                showUpdateResult(firestoreSuccess, emailSuccess, true); // Успех для пароля, т.к. не пытались обновить
            }
        } else {
            // Если пароль не нужно обновлять, показываем результат обновления никнейма и email
            progressBar.setVisibility(View.GONE);
            progressView.setVisibility(View.GONE);
            showUpdateResult(firestoreSuccess, emailSuccess, true); // Успех для пароля, т.к. не пытались обновить
        }
    }

    // Показывает Toast сообщение с результатом обновления данных
    private void showUpdateResult(boolean firestoreSuccess, boolean emailSuccess, boolean passwordSuccess) {
        // Если все обновления прошли успешно
        if (firestoreSuccess && emailSuccess && passwordSuccess) {
            Toast.makeText(this, getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
        } else {
            // Если были ошибки, формируем более специфичное сообщение
            StringBuilder errorMsg = new StringBuilder(getString(R.string.data_update_failed));
            if (!firestoreSuccess) {
                errorMsg.append(" (Firestore)"); // Указываем, что ошибка в Firestore
            }
            if (!emailSuccess) {
                errorMsg.append(" (Email)"); // Указываем, что ошибка обновления email
            }
            if (!passwordSuccess) {
                errorMsg.append(" (Password)"); // Указываем, что ошибка обновления пароля
            }
            Toast.makeText(this, errorMsg.toString(), Toast.LENGTH_SHORT).show();
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
        // for update
    }
}