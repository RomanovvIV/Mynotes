package com.zametki.mynotes.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes")
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String title;
    public String text;
    public Long date;
    public boolean pinned;
}