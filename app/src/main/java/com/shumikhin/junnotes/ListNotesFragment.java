package com.shumikhin.junnotes;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Date;

public class ListNotesFragment extends Fragment {

    public static final String CURRENT_NOTES = "CurrentNotes";
    //Класс данных в котором храним наши записки
    private Notes currentNote;
    //положение экрана
    private boolean isLandscape;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Определение, можно ли будет расположить рядом герб в другом фрагменте
        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        initView(view);
    }

    private void initView(View view) {
        LinearLayout layoutView = (LinearLayout) view;
        String[] cities = getResources().getStringArray(R.array.title);
        // В этом цикле создаём элемент TextView,
        // заполняем его значениями
        // и добавляем на экран.
        // Кроме того, создаём обработку касания на элемент
        for (int i = 0; i < cities.length; i++) {
            String city = cities[i];
            TextView tv = new TextView(getContext());
            tv.setText(city);
            tv.setTextSize(30);
            layoutView.addView(tv);
            final int index = i;
            tv.setOnClickListener(v -> {
                //Постоянно создаю новую дату, временное решение
                currentNote = new Notes(index, getResources().getStringArray(R.array.title)[index], getResources().getStringArray(R.array.description)[index], new Date() );
                showDescription(currentNote);

            });
        }
    }

    private void showDescription(Notes currentNote) {
        if (isLandscape) {
            //showLandCoatOfArms(index);
            showLandDescription(currentNote);
        } else {
            //showPortCoatOfArms(index);
            showPortDescription(currentNote);
        }
    }

    private void showPortDescription(Notes currentNote) {
        // Откроем вторую activity
        Intent intent = new Intent();
        intent.setClass(getActivity(), DescriptionActivity.class);
        // и передадим туда параметры
        intent.putExtra(DescriptionFragment.NOTE_INDEX, currentNote);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Если это не первое создание, то восстановим текущую позицию
        if (savedInstanceState != null) {
            // Восстановление текущего объекта.
            currentNote = savedInstanceState.getParcelable(CURRENT_NOTES);
        } else {
            // Если восстановить не удалось, то сделаем объект с первым индексом
            currentNote = new Notes(0, getResources().getStringArray(R.array.title)[0],getResources().getStringArray(R.array.description)[0], new Date() );
        }


        // Если можно показать описание рядом, то рисуем рядом
        if (isLandscape) {
            //showLandCoatOfArms(currentPosition);
            showLandDescription (currentNote);
        }
    }

    // Показать описание заметки в ландшафтной ориентации
    private void showLandDescription(Notes currentNote) {

        DescriptionFragment detail = DescriptionFragment.newInstance(currentNote);

        // Выполняем транзакцию по замене фрагмента
        requireActivity().getSupportFragmentManager() //
                .beginTransaction() //фрагмент меняется в транзакции
                .replace(R.id.description, detail) // замена фрагмента
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE) //тип анимации замены транзакции
                .commit();
    }

    // Сохраним текущую позицию (вызывается перед выходом из фрагмента)
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(CURRENT_NOTES, currentNote);
        super.onSaveInstanceState(outState);
    }

}