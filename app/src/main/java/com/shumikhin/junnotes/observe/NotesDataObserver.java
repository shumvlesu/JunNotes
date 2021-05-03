package com.shumikhin.junnotes.observe;

import com.shumikhin.junnotes.data.NotesData;

public interface NotesDataObserver {
    void updateNoteData(NotesData notesData);
}

