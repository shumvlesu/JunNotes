package com.shumikhin.junnotes.observe;

import com.shumikhin.junnotes.data.NotesData;

public interface Observer {
    void updateNoteData(NotesData notesData);
}

