package com.zametki.mynotes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zametki.mynotes.db.Note;

import java.util.Date;

public class AddOrEditNoteActivity extends AppCompatActivity {

    private static final String noteArg = "note";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_note);

        EditText titleEditText = findViewById(R.id.titleEditText);
        EditText textEditText = findViewById(R.id.textEditText);
        ImageView saveImageView = findViewById(R.id.saveImageView);

        Note note = getNote();

        titleEditText.setText(note.title);
        textEditText.setText(note.text);


        saveImageView.setOnClickListener(view -> {
            String title = titleEditText.getText().toString().trim();
            String text = textEditText.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, R.string.note_title_required, Toast.LENGTH_SHORT).show();
                return;
            }

            if (text.isEmpty()) {
                Toast.makeText(this, R.string.note_text_required, Toast.LENGTH_SHORT).show();
                return;
            }

            note.title = title;
            note.text = text;
            note.date = new Date().getTime();

            Intent intent = new Intent();
            intent.putExtra("note", note);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });

    }

    private Note getNote() {
        Note note = (Note) getIntent().getSerializableExtra(noteArg);
        if (note == null) note = new Note();
        return note;
    }

    public static Intent getIntent(Context context, @Nullable Note noteBeingEdited) {
        Intent intent = new Intent(context, AddOrEditNoteActivity.class);
        intent.putExtra(noteArg, noteBeingEdited);
        return intent;
    }
}