package com.shumikhin.junnotes;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1. Подумайте о функционале вашего приложения заметок. Какие экраны там могут быть,
        // помимо основного со списком заметок? Как можно использовать меню и всплывающее меню в вашем приложении?
        // Не обязательно сразу пытаться реализовать весь этот функционал, достаточно создать макеты и структуру,
        // а реализацию пока заменить на заглушки или всплывающие уведомления (Toast). Используйте подход Single Activity для отображения экранов.
        //2. Создайте боковое навигационное меню для своего приложения и добавьте туда хотя бы один экран, например «Настройки» или «О приложении».
        //3. * Создайте полноценный заголовок для NavigationDrawer’а. К примеру, аватарка пользователя, его имя и какая-то дополнительная информация.

        //Toolbar toolbar = initToolbar();
        initToolbar();

        //initDrawer(toolbar);
        //Еогр, ланшафтная ориентация сильно осложняет жизнь :( я от нее избавился.

        //Я  смотрю что многие разработчики просто запрещают ланшафтный режим, втб, сбер и т.д.
        initListNotes();

    }

//    private Toolbar initToolbar() {
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        return toolbar;
//    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        return toolbar;
    }


    private void initListNotes() {
        addFragment(getSupportFragmentManager() , new ListNotesFragment());
    }

    public static void addFragment(FragmentManager fragmentManager, Fragment fragment) {
        // Открыть транзакцию
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Удалить видимый фрагмент
        Fragment fragmentToRemove = getVisibleFragment(fragmentManager);
        if (fragmentToRemove != null) {
            fragmentTransaction.remove(fragmentToRemove);
        }
        // Зменить фрагмент
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        // Добавить транзакцию в бэкстек
        fragmentTransaction.addToBackStack(null);
        // Закрыть транзакцию
        fragmentTransaction.commit();
    }

    //getVisibleFragment получение видимого фрагмента.
    // Пробегается по стеку фрагментов и тот который сверху стека тот фрагмент и видимый
    public static Fragment getVisibleFragment(FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        int countFragments = fragments.size();
        //от последнего к первому в обратном порядке
        for (int i = countFragments - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment.isVisible())
                return fragment;
        }
        return null;
    }

}
