package com.shumikhin.junnotes.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shumikhin.junnotes.MainActivity;
import com.shumikhin.junnotes.R;
import com.shumikhin.junnotes.data.NotesData;
import com.shumikhin.junnotes.data.NotesSource;
import com.shumikhin.junnotes.data.NotesSourceImpl;

import java.util.Date;

public class ListNotesFragment extends Fragment {

    private static final int MY_DEFAULT_DURATION = 500;
    public static final String CURRENT_NOTES = "CurrentNotes";
    //Класс данных в котором храним наши записки
    private NotesData currentNote;
    private NotesSource data;
    private ListNotesAdapter adapter;
    private RecyclerView recyclerView;
    private boolean moveToLastPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_notes, container, false);

        //ищем наш ресайкл вью у фрагмента
        //RecyclerView recyclerView = view.findViewById(R.id.recycler_view_lines);

        // Получим источник данных для списка
        data = new NotesSourceImpl(getResources()).init();

        initView(view);

        //Говорим что у нас есть меню для фрагмента
        setHasOptionsMenu(true);

        return view;
    }

    private void initView(View view) {
        //ищем наш ресайкл вью у фрагмента
        recyclerView = view.findViewById(R.id.recycler_view_lines);
        // Получим источник данных для списка
        data = new NotesSourceImpl(getResources()).init();
        initRecyclerView();
    }

    private void initRecyclerView() {
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
        adapter = new ListNotesAdapter(data, this);

        //Устанавливаем адаптер для RecyclerView
        recyclerView.setAdapter(adapter);

        //Анимация recyclerView
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Установим анимацию. А чтобы было хорошо заметно, сделаем анимацию долгой
        DefaultItemAnimator animator = new DefaultItemAnimator();
        //скорость анимации при добавлении
        animator.setAddDuration(MY_DEFAULT_DURATION);
        //скорость анимации при удалении
        animator.setRemoveDuration(MY_DEFAULT_DURATION);
        recyclerView.setItemAnimator(animator);
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++

        if (moveToLastPosition) {
            recyclerView.smoothScrollToPosition(data.size() - 1);
            moveToLastPosition = false;
        }

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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                data.addNoteData(new NotesData("Заголовок " + data.size(), "Описание " + data.size(), new Date()));
                adapter.notifyItemInserted(data.size() - 1);
                recyclerView.smoothScrollToPosition(data.size() - 1);
                return true;
            case R.id.action_clear:
                data.clearNoteData();
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = adapter.getMenuPosition();
        switch (item.getItemId()) {
            case R.id.action_update:
                data.updateNoteData(position,
                        new NotesData("Новая запись " + position, data.getNotesData(position).getDescriptionNote(), new Date()));
                adapter.notifyItemChanged(position);
                return true;
            case R.id.action_delete:
                data.deleteNoteData(position);
                adapter.notifyItemRemoved(position);
                return true;
        }
        return super.onContextItemSelected(item);
    }


}