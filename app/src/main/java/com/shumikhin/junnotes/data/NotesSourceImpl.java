package com.shumikhin.junnotes.data;

import android.content.res.Resources;

import com.shumikhin.junnotes.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotesSourceImpl implements NotesSource {

    private List<NotesData> dataSource; //этот массив объектов будет загружаться в наш ресайклвью
    private Resources resources; // ресурсы приложения

    public NotesSourceImpl(Resources resources) {
        this.dataSource = new ArrayList<>(9);
        this.resources = resources;
    }

    public NotesSource init(NotesSourceResponse notesSourceResponse) {
        // строки заголовков записок из ресурсов
        String[] titles = resources.getStringArray(R.array.title);
        // строки описаний из ресурсов
        String[] descriptions = resources.getStringArray(R.array.description);

        // заполнение источника данных
        for (int i = 0; i < descriptions.length; i++) {
            dataSource.add(new NotesData(titles[i], descriptions[i], new Date()));
        }

        if (notesSourceResponse != null){
            notesSourceResponse.initialized(this);
        }


        return this;
    }

    @Override
    public NotesData getNotesData(int position) {
        return dataSource.get(position);
    }

    //размер массива данных, нужен для адаптера
    @Override
    public int size() {
        return dataSource.size();
    }

    @Override
    public void deleteNoteData(int position) {
        dataSource.remove(position);
    }

    @Override
    public void updateNoteData(int position, NotesData notesData) {
        dataSource.set(position, notesData);
    }

    @Override
    public void addNoteData(NotesData notesData) {
        dataSource.add(notesData);
    }

    @Override
    public void clearNoteData() {
        dataSource.clear();
    }

}
