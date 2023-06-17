package com.zametki.mynotes;

import com.zametki.mynotes.db.Note;

public interface NoteEditRequestHandler {
    void editNote(Note note);
}
