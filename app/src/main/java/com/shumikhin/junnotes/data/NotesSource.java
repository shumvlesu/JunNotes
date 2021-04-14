package com.shumikhin.junnotes.data;

public interface NotesSource {
    NotesData getNotesData(int position);

    int size();
}
