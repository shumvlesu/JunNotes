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
        ;
        this.resources = resources;
    }

    public NotesSourceImpl init() {
        // строки заголовков записок из ресурсов
        String[] titles = resources.getStringArray(R.array.title);
        // строки описаний из ресурсов
        String[] descriptions = resources.getStringArray(R.array.description);

        // заполнение источника данных
        for (int i = 0; i < descriptions.length; i++) {
            dataSource.add(new NotesData(titles[i], descriptions[i], new Date()));
        }
        return this;
    }

    @Override
    public NotesData getNotesData(int position) {
        return dataSource.get(position);
    }

    //
    @Override
    public int size() {
        return dataSource.size();
    }
}
