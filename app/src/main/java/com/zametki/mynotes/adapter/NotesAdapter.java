package com.zametki.mynotes.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.zametki.mynotes.NoteAdapterCallback;
import com.zametki.mynotes.NoteEditRequestHandler;
import com.zametki.mynotes.db.Note;
import com.zametki.mynotes.R;
import com.zametki.mynotes.db.NoteDao;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> implements NoteAdapterCallback {

    private final NoteEditRequestHandler editHandler;
    private final NoteDao dao;
    private List<Note> notes;

    @SuppressLint("NotifyDataSetChanged")
    public NotesAdapter(NoteDao noteDao, NoteEditRequestHandler handler) {
        editHandler = handler;
        dao = noteDao;
        filter("");
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_note, parent, false);
        return new NotesViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String filter) {
        notes = dao.getNotes('%' + filter + '%');
        notifyDataSetChanged();
    }

    public void addOrEditItem(Note note) {
        long oldId = note.id;
        note.id = dao.insert(note);
        if (oldId == 0) {
            notes.add(note);
            notifyItemInserted(notes.size() - 1);
        } else {
            for (int i = 0; i < notes.size(); i++) {
                if (notes.get(i).id == oldId) {
                    notes.set(i, note);
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    @Override
    public void edit(int position) {
        editHandler.editNote(notes.get(position));
    }

    @Override
    public void changePinState(int position) {
        Note note = notes.remove(position);
        note.pinned = !note.pinned;

        int newPosition;
        for (newPosition = 0; newPosition < notes.size(); newPosition++) {
            Note n = notes.get(newPosition);
            if (note.pinned) {
                if (!n.pinned || n.id > note.id) break;
            } else {
                if (!n.pinned && n.id > note.id) break;
            }
        }

        notes.add(newPosition, note);
        notifyItemMoved(position, newPosition);

        dao.insert(note);
    }

    @Override
    public void delete(int position) {
        Note note = notes.remove(position);
        notifyItemRemoved(position);
        dao.delete(note);
    }

    @Override
    public void share(Context context, int position) {
        Note note = notes.get(position);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String text = context.getResources().getString(
                R.string.note_share_format, note.title, note.text
        );
        intent.putExtra(Intent.EXTRA_TEXT, text);

        String title = context.getResources().getString(R.string.note_share);
        context.startActivity(Intent.createChooser(intent, title));
    }

    static class NotesViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {
        NoteAdapterCallback callback;
        CardView cardView;
        TextView titleTextView;
        TextView textTextView;
        TextView dateTextView;
        ImageView pinImageView;

        private static final List<Integer> cardColors = new ArrayList<>();

        static {
            cardColors.add(R.color.note_color_1);
            cardColors.add(R.color.note_color_2);
            cardColors.add(R.color.note_color_3);
            cardColors.add(R.color.note_color_4);
            cardColors.add(R.color.note_color_5);
        }

        public NotesViewHolder(@NonNull View itemView, NoteAdapterCallback callback) {
            super(itemView);
            this.callback = callback;
            cardView = itemView.findViewById(R.id.cardView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            textTextView = itemView.findViewById(R.id.textTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            pinImageView = itemView.findViewById(R.id.pinImageView);
        }

        public void bind(Note note) {
            titleTextView.setText(note.title);

            textTextView.setText(note.text);

            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            dateTextView.setText(dateFormat.format(new Date(note.date)));

            pinImageView.setVisibility(note.pinned ? View.VISIBLE : View.GONE);

            Random random = new Random(note.id);
            int colorRes = cardColors.get(random.nextInt(cardColors.size()));
            int color = ContextCompat.getColor(cardView.getContext(), colorRes);
            cardView.setCardBackgroundColor(color);

            itemView.setOnClickListener(v -> callback.edit(getAdapterPosition()));
            itemView.setOnLongClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(itemView.getContext(), itemView);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.inflate(R.menu.note_menu);
                MenuItem pin = popupMenu.getMenu().findItem(R.id.pinItem);
                pin.setTitle(note.pinned ? R.string.note_unpin : R.string.note_pin);
                popupMenu.show();
                return true;
            });
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            int pos = getAdapterPosition();
            if (id == R.id.pinItem) {
                boolean isHidden = pinImageView.getVisibility() == View.GONE;
                pinImageView.setVisibility(isHidden ? View.VISIBLE : View.GONE);
                callback.changePinState(pos);
            } else if (id == R.id.deleteItem) {
                callback.delete(pos);
            } else if (id == R.id.shareItem) {
                callback.share(itemView.getContext(), pos);
            }
            return true;
        }
    }
}
