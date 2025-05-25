package com.example.testapp2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import com.example.testapp2.Activity.Account.AccountActivity;
import com.example.testapp2.Activity.Account.SearchHistoryActivity;
import com.example.testapp2.Activity.MainActivity;
import com.example.testapp2.Activity.Search;
import com.example.testapp2.Activity.Settings;
import com.example.testapp2.R;
import com.example.testapp2.databinding.ActivityLearnBinding;
import com.example.testapp2.utils.LocaleHelper;
import com.example.testapp2.utils.ThemeHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class Learn extends AppCompatActivity {

    private ScrollView scrollView;
    private Button accButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this); // 👈 обязательно ДО super.onCreate
        super.onCreate(savedInstanceState);
        setTitle(R.string.learn); // Установите нужную строку из ресурсов
        LocaleHelper.loadLocale(this); // Должно вызываться ДО инициализации UI

        ActivityLearnBinding binding = ActivityLearnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Устанавливаем верхний тулбар
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Скрыть кнопку "Назад"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        // Находим ScrollView
        scrollView = findViewById(R.id.scrollView);

        // Установка HTML-содержимого для разделов
        setHtmlContent();

        // Установка кликов для перехода по содержанию
        findViewById(R.id.learn_content_1).setOnClickListener(view -> scrollToSection(R.id.section1));
        findViewById(R.id.learn_content_2).setOnClickListener(view -> scrollToSection(R.id.section2));
        findViewById(R.id.learn_content_3).setOnClickListener(view -> scrollToSection(R.id.section3));
        findViewById(R.id.learn_content_4).setOnClickListener(view -> scrollToSection(R.id.section4));
        findViewById(R.id.learn_content_5).setOnClickListener(view -> scrollToSection(R.id.section5));

        // Переход по кнопкам в нижнем тулбаре
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.nav_learn);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.nav_learn) {
                    return true;
                } else if (id == R.id.nav_search) {
                    startActivity(new Intent(getApplicationContext(), Search.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(getApplicationContext(), Settings.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_history) {
                    startActivity(new Intent(getApplicationContext(), SearchHistoryActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }

    // Установка HTML-содержимого для TextView.
    private void setHtmlContent() {
        // Определяем текущий язык устройства (по умолчанию русский)
        boolean isRussian = Locale.getDefault().getLanguage().equals("ru");
        /// context.getResources().flushLayoutCache(); // отчиста кеша  // он не должооон но может в будущем понадобится хз
        // ============= Раздел 1 =============
        TextView section1Content = findViewById(R.id.section1_content);
        String section1Html = isRussian ?
                // Русская версия
                "<p>Мошенники выдают себя за вашего начальника, чтобы обманом заставить вас следовать их инструкциям. Схема мошенничества обычно работает следующим образом:</p>"
                        + "<ol>"
                        + "<li>Вы получаете сообщение, предположительно от вашего менеджера, в котором к вам обращаются по имени и предупреждают о предстоящем звонке из регулирующего органа.</li>"
                        + "<li>'Начальник' инструктирует вас выполнять приказы вышестоящего начальства, возможно, утверждая, что с ним связались по поводу утечки данных.</li>"
                        + "<li>Мошенники звонят с неизвестного номера и запрашивают персональные данные или денежные переводы. Целью этих мошеннических действий являются как нынешние, так и бывшие сотрудники.</li>"
                        + "</ol>"
                        + "<i>Альтернативный сценарий: Мошенники звонят, выдавая себя за вашего начальника в экстренной ситуации, и просят перевести деньги. Голос может быть сгенерирован искусственным интеллектом.</i>"
                :
                // Английская версия
                "<p>Scammers impersonate your boss to trick you into following their instructions. The fraud scheme typically works as follows:</p>"
                        + "<ol>"
                        + "<li>You receive a message appearing to be from your manager, addressing you by name and warning about an upcoming call from a regulatory authority.</li>"
                        + "<li>The 'boss' instructs you to follow orders from a higher authority, possibly claiming they were contacted about a data breach.</li>"
                        + "<li>Scammers call from an unknown number requesting personal data or money transfers. These scams target both current and former employees.</li>"
                        + "</ol>"
                        + "<i>Alternative scenario: Scammers call pretending to be your boss in an emergency situation, asking for money transfers. The voice may be AI-generated.</i>";

        section1Content.setText(HtmlCompat.fromHtml(section1Html, HtmlCompat.FROM_HTML_MODE_LEGACY));
        section1Content.setMovementMethod(LinkMovementMethod.getInstance());

        // ============= Раздел 2 (Мошенничество от имени начальства) =============
        TextView section2Content = findViewById(R.id.section2_content);
        String section2Html = isRussian ?
                // Русская версия
                "<p>Мошенники звонят под видом начальника, чтобы обманом заставить вас выполнить их указания. Схема мошенников в этом случае строится так.</p>"
                        + "<ol>"
                        + "<li>Приходит сообщение от имени руководителя, который обращается по имени и предупреждает о звонке из контролирующей инстанции.</li>"
                        + "<li>Начальник просит следовать дальнейшим указаниям вышестоящего ведомства. Руководитель может сообщить, что к нему обратились из ведомства в связи с утечкой данных.</li>"
                        + "<li>Звонят мошенники с неизвестного номера и просят передать персональные данные и сделать денежный перевод. При этом сообщения могут приходить не только действующим, но и бывшим сотрудникам компании.</li>"
                        + "</ol>"
                        + "<i>Есть альтернативный вариант. Мошенники звонят от лица начальника и просят перевести деньги, потому что он находится в форс-мажорной ситуации. Голос в этом случае генерируется также с помощью нейросетей.</i>"
                :
                // Английская версия
                "<p>Scammers impersonate your boss to trick you into following their instructions. The fraud scheme typically works as follows:</p>"
                        + "<ol>"
                        + "<li>You receive a message appearing to be from your manager, addressing you by name and warning about an upcoming call from a regulatory authority.</li>"
                        + "<li>The 'boss' instructs you to follow orders from a higher authority, possibly claiming they were contacted about a data breach.</li>"
                        + "<li>Scammers call from an unknown number requesting personal data or money transfers. These scams target both current and former employees.</li>"
                        + "</ol>"
                        + "<i>Alternative scenario: Scammers call pretending to be your boss in an emergency situation, asking for money transfers. The voice may be AI-generated.</i>";

        section2Content.setText(HtmlCompat.fromHtml(section2Html, HtmlCompat.FROM_HTML_MODE_LEGACY));
        section2Content.setMovementMethod(LinkMovementMethod.getInstance());

        // ============= Раздел 3 (Мошенничество от имени госорганов) =============
        TextView section3Content = findViewById(R.id.section3_content);
        String section3Html = isRussian ?
                // Русская версия
                "<p>Сценариев в этой категории множество: звонят мошенники из полиции, Следственного комитета, ФСБ, Росфинмониторинга, Центрального банка, налоговой службы и многих других организаций.</p>"
                        + "<p><b>Что говорят аферисты:</b></p>"
                        + "<ol>"
                        + "<li>Сообщают о наличии в производстве материалов уголовного дела, в котором вы фигурируете;</li>"
                        + "<li>Рассказывают о попытке взлома счёта и угрожают заблокировать его из-за сомнительных операций;</li>"
                        + "<li>Предупреждают о наличии задолженности и штрафов, которые нужно срочно погасить, могут даже прислать квитанцию для оплаты.</li>"
                        + "</ol>"
                        + "<i>Другой вариант — звонят мошенники якобы из Социального фонда и рассказывают о новых доступных соцвыплатах, которые нужно оформить прямо сейчас. Для этого просят код из СМС от портала Госуслуг.</i>"
                :
                // Английская версия
                "<p>There are numerous scenarios in this category: scammers impersonating police, Investigative Committee, FSB, Financial Monitoring Service, Central Bank, tax authorities and other government agencies.</p>"
                        + "<p><b>Common fraudster tactics:</b></p>"
                        + "<ol>"
                        + "<li>Claim there's a criminal case pending against you;</li>"
                        + "<li>Allege an attempted account hack and threaten to block it due to suspicious activity;</li>"
                        + "<li>Warn about outstanding debts and fines requiring immediate payment, sometimes sending fake payment receipts.</li>"
                        + "</ol>"
                        + "<i>Another variation: Scammers pretend to be from Social Services offering new benefits that require immediate action, asking for SMS codes from government portals.</i>";

        section3Content.setText(HtmlCompat.fromHtml(section3Html, HtmlCompat.FROM_HTML_MODE_LEGACY));
        section3Content.setMovementMethod(LinkMovementMethod.getInstance());

        // ============= Раздел 4 (Банковское мошенничество) =============
        TextView section4Content = findViewById(R.id.section4_content);
        String section4Html = isRussian ?
                // Русская версия
                "<p>«Здравствуйте, я из службы безопасности банка» — настоящая классика среди схем мошенников."
                        + "Но есть и некоторые новинки: вместо угроз оформления кредита и блокировки счёта злоумышленники звонят и представляются работниками техподдержки, которые беспокоятся о том,"
                        + "обновлено ли у человека банковское приложение и установлена ли программа для поиска вирусов.</p>"
                        + "<p><b>Если оказывается, что человек не обновлял приложение, то аферисты:</b></p>"
                        + "<ol>"
                        + "<li>Скидывают ссылку на скачивание программного обеспечения, которое открывает мошенникам доступ к персональным данным на вашем телефоне;</li>"
                        + "<li>Просят подождать звонка технического специалиста, который поможет в установке. Для этого мошенники сообщат о необходимости идентифицировать человека по биометрии;</li>"
                        + "<li>Объясняют, как включить демонстрацию экрана для идентификации. Это позволит злоумышленникам видеть счета, суммы на них, коды СМС от банка.</li>"
                        + "</ol>"
                :
                // Английская версия
                "<p>'Hello, I'm from your bank's security department' remains a classic scam approach."
                        + "New variations have emerged where instead of threatening loan approvals or account blocks, scammers pose as tech support staff concerned about whether you've:"
                        + "updated your banking app or installed antivirus software.</p>"
                        + "<p><b>If you haven't updated the app, scammers will:</b></p>"
                        + "<ol>"
                        + "<li>Send a link to download malware that gives them access to your personal data;</li>"
                        + "<li>Ask you to wait for a 'technical specialist' to call and assist with installation, claiming they need biometric verification;</li>"
                        + "<li>Instruct how to enable screen sharing for 'identification', allowing them to view your accounts, balances, and banking SMS codes.</li>"
                        + "</ol>";

        section4Content.setText(HtmlCompat.fromHtml(section4Html, HtmlCompat.FROM_HTML_MODE_LEGACY));
        section4Content.setMovementMethod(LinkMovementMethod.getInstance());

        // ============= Раздел 5 (Другие методы мошенничества) =============
        TextView section5Content = findViewById(R.id.section5_content);
        String section5Html = isRussian ?
                // Русская версия
                "<p>Мошенники могут использовать различные методы для обмана, включая звонки, сообщения и фишинговые сайты.</p>"
                        + "<p><b>Некоторые из них:</b></p>"
                        + "<ol>"
                        + "<li>Отправляют поддельные сообщения от имени известных компаний с просьбой перейти по ссылке и ввести личные данные;</li>"
                        + "<li>Создают поддельные сайты, которые выглядят как настоящие, чтобы украсть ваши учетные данные;</li>"
                        + "<li>Используют социальные сети для распространения вредоносных ссылок и обмана пользователей.</li>"
                        + "</ol>"
                        + "<i>Будьте бдительны и всегда проверяйте источник информации, прежде чем предпринимать какие-либо действия.</i>"
                        + "<br><br><br>"
                :
                // Английская версия
                "<p>Scammers employ various deception methods including calls, messages, and phishing websites.</p>"
                        + "<p><b>Common techniques include:</b></p>"
                        + "<ol>"
                        + "<li>Sending fake messages appearing to be from reputable companies, urging you to click links and enter personal data;</li>"
                        + "<li>Creating counterfeit websites mimicking legitimate ones to steal your login credentials;</li>"
                        + "<li>Utilizing social media platforms to spread malicious links and deceive users.</li>"
                        + "</ol>"
                        + "<i>Stay vigilant and always verify information sources before taking any action.</i>"
                        + "<br><br><br>";

        section5Content.setText(HtmlCompat.fromHtml(section5Html, HtmlCompat.FROM_HTML_MODE_LEGACY));
        section5Content.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Прокрутка к определённому разделу.
     */
    private void scrollToSection(int sectionId) {
        View sectionView = findViewById(sectionId);
        if (sectionView != null) {
            scrollView.post(() -> scrollView.smoothScrollTo(0, sectionView.getTop()));
        }
    }

    // Для перехода по иконке аккаунта
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.up_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_accoutn) {
            startActivity(new Intent(getApplicationContext(), AccountActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

