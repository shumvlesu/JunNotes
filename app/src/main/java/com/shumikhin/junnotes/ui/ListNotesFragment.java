package com.shumikhin.junnotes.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shumikhin.junnotes.MainActivity;
import com.shumikhin.junnotes.R;
import com.shumikhin.junnotes.data.NotesData;
import com.shumikhin.junnotes.data.NotesSource;
import com.shumikhin.junnotes.data.NotesSourceImpl;

public class ListNotesFragment extends Fragment {

    public static final String CURRENT_NOTES = "CurrentNotes";
    //Класс данных в котором храним наши записки
    private NotesData currentNote;
    //положение экрана
    private boolean isLandscape;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_notes, container, false);

        //ищем наш ресайкл вью у фрагмента
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_lines);

        // Получим источник данных для списка
        NotesSource data = new NotesSourceImpl(getResources()).init();

        //Инициализируем менеджер для recyclerView в отдельном методе, так удобней
        initRecyclerView(recyclerView, data);

        return view;
    }

    private void initRecyclerView(RecyclerView recyclerView, NotesSource data) {
        // Эта установка служит для повышения производительности системы
        recyclerView.setHasFixedSize(true);

        // Будем работать со встроенным менеджером
        //берем не LinearLayoutManager а GridLayoutManager что бы показать как можно при поворте менять количество столбцов
        int span = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1;
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), span);

        recyclerView.setLayoutManager(layoutManager);
        //layoutManager можно задать и в макете строкой как внизу, но это неудобно
        //например - app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"

        // Установим адаптер
        // В адапторе (SocialNetworkAdapter) мы заполняем данные из массива
        ListNotesAdapter adapter = new ListNotesAdapter(data);

        //Устанавливаем адаптер для RecyclerView
        recyclerView.setAdapter(adapter);

        //Область текста ответственная на обработку нажатий+++++++
        // Установим слушателя
        adapter.SetOnItemClickListener(new ListNotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                showDescription(data.getNotesData(position));

            }
        });
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    }


    private void showDescription(NotesData currentNote) {
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
            currentNote = new NotesSourceImpl(getResources()).init().getNotesData(0);
        }

    }

    // Сохраним текущую позицию (вызывается перед выходом из фрагмента)
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(CURRENT_NOTES, currentNote);
        super.onSaveInstanceState(outState);
    }

}