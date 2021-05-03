package com.shumikhin.junnotes.data;

public interface NotesSource {
    NotesData getNotesData(int position);
    
    //размер массива
    int size();

    //для  добаления, удаления, редактирования, очистки, обновления списка.
    void deleteNoteData(int position);

    void updateNoteData(int position, NotesData notesData);

    void addNoteData(NotesData notesData);

    void clearNoteData();
}
