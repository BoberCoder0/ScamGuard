package com.example.testapp2.Activity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

import com.example.testapp2.NavigationManager;
import com.example.testapp2.R;
import com.example.testapp2.databinding.ActivityLearnBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.testapp2.Activity.MainActivity;

public class Learn extends BaseActivity {

    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        // Устанавливаем верхний тулбар
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Настраиваем нижний тулбар
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);


        // Установка лейбла
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.learn));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
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

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // Установка HTML-содержимого для TextView.
    private void setHtmlContent() {
        TextView section1Content = findViewById(R.id.section1_content);

        // HTML-содержимое для раздела 1
        String htmlContent = "<p>Мошенники звонят по телефону и присылают голосовые сообщения от имени родственников и друзей. С помощью специальных программ им удаётся менять голос.</p>"
                + "<p><b>Что говорят мошенники в таких звонках и аудиосообщениях:</b></p>"
                + "<ul>"
                + "<li> Просят дать в долг деньги;</li>"
                + "<li> Жалуются, что попали в неприятную ситуацию, и просят перевести деньги;</li>"
                + "<li> Сообщают от имени полицейского, что родственник попал в беду. Этот же родственник просит экстренно перевести определённую сумму.</li>"
                + "</ul>";

        // Установка HTML-контента в TextView
        section1Content.setText(HtmlCompat.fromHtml(htmlContent, HtmlCompat.FROM_HTML_MODE_LEGACY));
        section1Content.setMovementMethod(LinkMovementMethod.getInstance()); // Если есть ссылки

        // Установка содержимого для раздела 2
        TextView section2Content = findViewById(R.id.section2_content);
        String section2Html = "<p>Мошенники звонят под видом начальника, чтобы обманом заставить вас выполнить их указания. Схема мошенников в этом случае строится так.</p>"
                + "<ol>"
                + "<li> Приходит сообщение от имени руководителя, который обращается по имени и предупреждает о звонке из контролирующей инстанции.</li>"
                + "<li> Начальник просит следовать дальнейшим указаниям вышестоящего ведомства. Руководитель может сообщить, что к нему обратились из ведомства в связи с утечкой данных.</li>"
                + "<li> Звонят мошенники с неизвестного номера и просят передать персональные данные и сделать денежный перевод. При этом сообщения могут приходить не только действующим, но и бывшим сотрудникам компании.</li>"
                + "</ol>"
                + "<i>Есть альтернативный вариант. Мошенники звонят от лица начальника и просят перевести деньги, потому что он находится в форс-мажорной ситуации. Голос в этом случае генерируется также с помощью нейросетей.</i>";
        section2Content.setText(HtmlCompat.fromHtml(section2Html, HtmlCompat.FROM_HTML_MODE_LEGACY));
        section2Content.setMovementMethod(LinkMovementMethod.getInstance());

        // Установка содержимого для раздела 3
        TextView section3Content = findViewById(R.id.section3_content);
        String section3Html = "<p>Сценариев в этой категории множество: звонят мошенники из полиции, Следственного комитета, ФСБ, Росфинмониторинга, Центрального банка, налоговой службы и многих других организаций.</p>"
                + "<p><b>Что говорят аферисты:</b></p>"
                + "<ol>"
                + "<li> Сообщают о наличии в производстве материалов уголовного дела, в котором вы фигурируете;</li>"
                + "<li> Рассказывают о попытке взлома счёта и угрожают заблокировать его из-за сомнительных операций;</li>"
                + "<li> Предупреждают о наличии задолженности и штрафов, которые нужно срочно погасить, могут даже прислать квитанцию для оплаты.</li>"
                + "</ol>"
                + "<i>Другой вариант — звонят мошенники якобы из Социального фонда и рассказывают о новых доступных соцвыплатах, которые нужно оформить прямо сейчас. Для этого просят код из СМС от портала Госуслуг.</i>";
        section3Content.setText(HtmlCompat.fromHtml(section3Html, HtmlCompat.FROM_HTML_MODE_LEGACY));
        section3Content.setMovementMethod(LinkMovementMethod.getInstance());

        // Установка содержимого для раздела 4
        TextView section4Content = findViewById(R.id.section4_content);
        String section4Html = "<p>«Здравствуйте, я из службы безопасности банка» — настоящая классика среди схем мошенников." +
                "Но есть и некоторые новинки: вместо угроз оформления кредита и блокировки счёта злоумышленники звонят и представляются работниками техподдержки, которые беспокоятся о том," +
                "обновлено ли у человека банковское приложение и установлена ли программа для поиска вирусов.</p>"
                + "<p><b>Если оказывается, что человек не обновлял приложение, то аферисты:</b></p>"
                + "<ol>"
                + "<li> Скидывают ссылку на скачивание программного обеспечения, которое открывает мошенникам доступ к персональным данным на вашем телефоне;</li>"
                + "<li> Просят подождать звонка технического специалиста, который поможет в установке. Для этого мошенники сообщат о необходимости идентифицировать человека по биометрии;</li>"
                + "<li> Объясняют, как включить демонстрацию экрана для идентификации. Это позволит злоумышленникам видеть счета, суммы на них, коды СМС от банка.</li>"
                + "</ol>";
        section4Content.setText(HtmlCompat.fromHtml(section4Html, HtmlCompat.FROM_HTML_MODE_LEGACY));
        section4Content.setMovementMethod(LinkMovementMethod.getInstance());
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
}
