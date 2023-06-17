package com.zametki.mynotes.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM notes WHERE title LIKE :filter order by pinned desc")
    List<Note> getNotes(String filter);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Note note);

    @Delete
    void delete(Note note);
}
