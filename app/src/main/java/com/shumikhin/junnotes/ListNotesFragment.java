package com.shumikhin.junnotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Date;

public class ListNotesFragment extends Fragment {

    public static final String CURRENT_NOTES = "CurrentNotes";
    //Класс данных в котором храним наши записки
    private Notes currentNote;
    //положение экрана
    private boolean isLandscape;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_notes, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Определение, можно ли будет расположить рядом подробное описание в другом фрагменте
        //isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        initView(view);
    }

    private void initView(View view) {

        //LinearLayout layoutView = (LinearLayout) view;
        LinearLayout layoutView = view.findViewById(R.id.myList);

        String[] cities = getResources().getStringArray(R.array.title);
        // В этом цикле создаём элемент TextView,
        // заполняем его значениями
        // и добавляем на экран.
        // Кроме того, создаём обработку касания на элемент
        for (int i = 0; i < cities.length; i++) {
            String city = cities[i];

            LinearLayout layoutGroup = new LinearLayout(getContext());
            layoutGroup.setOrientation(LinearLayout.VERTICAL);

            //Заголовок
            TextView titleView = new TextView(getContext());
            titleView.setText(city);
            titleView.setTextSize(30);
            layoutGroup.addView(titleView);

            //описание
            TextView descriptionView = new TextView(getContext());
            descriptionView.setText(getResources().getStringArray(R.array.description)[i]);
            descriptionView.setTextSize(20);
            layoutGroup.addView(descriptionView);

            //Постоянно создаю новую дату, временное решение
            TextView dateView = new TextView(getContext());
            Date dateNotes = new Date();
            dateView.setText(dateNotes.toString());
            dateView.setTextSize(10);
            layoutGroup.addView(dateView);

            layoutView.addView(layoutGroup);

            final int index = i;
            //tv.setOnClickListener(v -> {
            layoutGroup.setOnClickListener(v -> {
                currentNote = new Notes(index, getResources().getStringArray(R.array.title)[index], getResources().getStringArray(R.array.description)[index], dateNotes);
                showDescription(currentNote);
            });
        }
    }

    private void showDescription(Notes currentNote) {
        DescriptionFragment detail = DescriptionFragment.newInstance(currentNote);
        MainActivity.addFragment(getActivity().getSupportFragmentManager(), detail);
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
            currentNote = new Notes(0, getResources().getStringArray(R.array.title)[0], getResources().getStringArray(R.array.description)[0], new Date());
        }

    }

    // Сохраним текущую позицию (вызывается перед выходом из фрагмента)
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(CURRENT_NOTES, currentNote);
        super.onSaveInstanceState(outState);
    }

}