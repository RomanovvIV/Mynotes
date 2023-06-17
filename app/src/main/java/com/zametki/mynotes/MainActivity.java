package com.zametki.mynotes;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.zametki.mynotes.adapter.NotesAdapter;
import com.zametki.mynotes.db.AppDatabase;
import com.zametki.mynotes.db.Note;

public class MainActivity extends AppCompatActivity implements NoteEditRequestHandler {

    private ActivityResultLauncher<Intent> addOrEditNoteActivityLauncher;
    private NotesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fab = findViewById(R.id.fab);
        SearchView searchView = findViewById(R.id.searchView);

        adapter = new NotesAdapter(AppDatabase.getInstance(this).noteDao(), this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        addOrEditNoteActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) return;
                        Note note = (Note) data.getSerializableExtra("note");
                        if (note == null) return;
                        adapter.addOrEditItem(note);
                    }
                }
        );
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        fab.setOnClickListener(view -> {
            Intent intent = AddOrEditNoteActivity.getIntent(this, null);
            addOrEditNoteActivityLauncher.launch(intent);
        });

    }

    @Override
    public void editNote(Note note) {
        Intent intent = AddOrEditNoteActivity.getIntent(this, note);
        addOrEditNoteActivityLauncher.launch(intent);
    }
}

