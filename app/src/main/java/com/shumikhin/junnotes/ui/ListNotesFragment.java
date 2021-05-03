package com.shumikhin.junnotes.ui;

import android.content.Context;
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
import com.shumikhin.junnotes.Navigation;
import com.shumikhin.junnotes.R;
import com.shumikhin.junnotes.data.NotesData;
import com.shumikhin.junnotes.data.NotesSource;
import com.shumikhin.junnotes.data.NotesSourceFirebaseImpl;
import com.shumikhin.junnotes.data.NotesSourceResponse;
import com.shumikhin.junnotes.observe.Observer;
import com.shumikhin.junnotes.observe.Publisher;

public class ListNotesFragment extends Fragment {

    private static final int MY_DEFAULT_DURATION = 500;
    public static final String CURRENT_NOTES = "CurrentNotes";
    //Класс данных в котором храним наши записки
    private NotesData currentNote;
    private NotesSource data;
    private ListNotesAdapter adapter;
    private RecyclerView recyclerView;
    private Navigation navigation;
    private Publisher publisher;
    // признак, что при повторном открытии фрагмента
    // (возврате из фрагмента, добавляющего запись)
    // надо прыгнуть на первую запись
    private boolean moveToFirstPosition;


    public static ListNotesFragment newInstance() {
        return new ListNotesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_notes, container, false);

        //ищем наш ресайкл вью у фрагмента
        //RecyclerView recyclerView = view.findViewById(R.id.recycler_view_lines);

        // Получим источник данных для списка
        //data = new NotesSourceImpl(getResources()).init();

        initView(view);

        data = new NotesSourceFirebaseImpl().init(new NotesSourceResponse() {
            @Override
            public void initialized(NotesSource notesData) {
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setDataSource(data);

        //Говорим что у нас есть меню для фрагмента
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity) context;
        navigation = activity.getNavigation();
        publisher = activity.getPublisher();
    }

    @Override
    public void onDetach() {
        navigation = null;
        publisher = null;
        super.onDetach();
    }

    private void initView(View view) {
        //ищем наш ресайкл вью у фрагмента
        recyclerView = view.findViewById(R.id.recycler_view_lines);
        // Получим источник данных для списка
        //data = new NotesSourceImpl(getResources()).init();
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
        adapter = new ListNotesAdapter(this);

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

        if (moveToFirstPosition && data.size() > 0) {
            recyclerView.scrollToPosition(0);
            moveToFirstPosition = false;
        }

        //Область текста ответственная на обработку нажатий+++++++
        // Установим слушателя
        adapter.SetOnItemClickListener(new ListNotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showDescription(data.getNotesData(position));
                publisher.subscribe(new Observer() {
                    @Override
                    public void updateNoteData(NotesData cardData) {
                        data.updateNoteData(position, cardData);
                        adapter.notifyItemChanged(position);
                    }
                });
            }
        });
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    }


    private void showDescription(NotesData currentNote) {
        navigation.addFragment(DescriptionFragment.newInstance(currentNote), true);
    }

    //TODO Дописать
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Если это не первое создание, то восстановим текущую позицию
        if (savedInstanceState != null) {
            // Восстановление текущего объекта.
            currentNote = savedInstanceState.getParcelable(CURRENT_NOTES);
        }
//        else {
//            // Если восстановить не удалось, то сделаем объект с первым индексом
//            //currentNote = new NotesSourceFirebaseImpl(getResources()).init().getNotesData(0);
//            currentNote = new NotesSourceFirebaseImpl().init().getNotesData(0);
//        }

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
        return onItemSelected(item.getItemId()) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return onItemSelected(item.getItemId()) || super.onContextItemSelected(item);
    }

    private boolean onItemSelected(int menuItemId) {
        switch (menuItemId) {
            case R.id.action_add:
                navigation.addFragment(DescriptionFragment.newInstance(), true);
                publisher.subscribe(new Observer() {
                    @Override
                    public void updateNoteData(NotesData noteData) {
                        data.addNoteData(noteData);
                        adapter.notifyItemInserted(data.size() - 1);
                        // это сигнал, чтобы вызванный метод onCreateView
                        // перепрыгнул на начало списка
                        moveToFirstPosition = true;
                    }
                });
                return true;
            case R.id.action_update:
                final int updatePosition = adapter.getMenuPosition();
                navigation.addFragment(DescriptionFragment.newInstance(data.getNotesData(updatePosition)), true);
                publisher.subscribe(new Observer() {
                    @Override
                    public void updateNoteData(NotesData noteData) {
                        data.updateNoteData(updatePosition, noteData);
                        adapter.notifyItemChanged(updatePosition);
                    }
                });
                return true;
            case R.id.action_delete:
                int deletePosition = adapter.getMenuPosition();
                data.deleteNoteData(deletePosition);
                adapter.notifyItemRemoved(deletePosition);
                return true;
            case R.id.action_clear:
                data.clearNoteData();
                adapter.notifyDataSetChanged();
                return true;
        }
        return false;
    }


}