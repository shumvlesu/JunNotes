package com.shumikhin.junnotes.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.shumikhin.junnotes.MainActivity;
import com.shumikhin.junnotes.R;
import com.shumikhin.junnotes.data.NotesData;
import com.shumikhin.junnotes.observe.Publisher;

import java.util.Calendar;
import java.util.Date;

public class DescriptionFragment extends Fragment {


    public static final String NOTE_INDEX = "noteIndex";
    private NotesData noteData; // Данные по карточке
    private Publisher publisher; // Паблишер, с его помощью обмениваемся данными

    private TextInputEditText title;
    private TextInputEditText description;
    private DatePicker datePicker;

    // Для редактирования данных
    public static DescriptionFragment newInstance(NotesData note) {
        DescriptionFragment fragment = new DescriptionFragment();
        Bundle args = new Bundle();
        args.putParcelable(NOTE_INDEX, note);
        fragment.setArguments(args);
        return fragment;
    }

    // Для добавления новых данных
    public static DescriptionFragment newInstance() {
        DescriptionFragment fragment = new DescriptionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            noteData = getArguments().getParcelable(NOTE_INDEX);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity) context;
        publisher = activity.getPublisher();
    }

    @Override
    public void onDetach() {
        publisher = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        //description_menu
        setHasOptionsMenu(true);
        //TextView cityNameView = view.findViewById(R.id.textView);
        //cityNameView.setText(noteData.getDescriptionNote());

        initView(view);
        // если cardData пустая, то это добавление
        if (noteData != null) {
            populateView();
        }

        return view;
    }

    // Здесь соберём данные из views
    @Override
    public void onStop() {
        super.onStop();
        noteData = collectNoteData();
    }

    // Здесь передадим данные в паблишер
    @Override
    public void onDestroy() {
        super.onDestroy();
        publisher.notifySingle(noteData);
    }

    //Пихаем все что на фрагменте в класс данных
    private NotesData collectNoteData() {
        String title = this.title.getText().toString();
        String description = this.description.getText().toString();
        Date date = getDateFromDatePicker();

        if (noteData != null) {
            NotesData answer;
            answer = new NotesData(title, description, date);
            answer.setId(noteData.getId());
            return answer;
        }

        return new NotesData(title, description, date);
    }

    // Получение даты из DatePicker
    private Date getDateFromDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, this.datePicker.getYear());
        cal.set(Calendar.MONTH, this.datePicker.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, this.datePicker.getDayOfMonth());
        return cal.getTime();
    }

    //Находим все вьюхи
    private void initView(View view) {
        title = view.findViewById(R.id.inputTitle);
        description = view.findViewById(R.id.inputDescription);
        datePicker = view.findViewById(R.id.inputDate);
    }

    //Тянем все из класса данных на фрагмент
    private void populateView() {
        title.setText(noteData.getTitleNote());
        description.setText(noteData.getDescriptionNote());
        initDatePicker(noteData.getDateNote());
    }

    // Установка даты в DatePicker
    private void initDatePicker(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        this.datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                null);
    }

    //Раздел работы с меню+++++++++++++++++++++++++++++++++++
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.description_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_shared) {
            Toast.makeText(getContext(), "Нажал поделиться", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++

}