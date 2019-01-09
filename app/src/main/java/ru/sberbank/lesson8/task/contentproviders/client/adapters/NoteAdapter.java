package ru.sberbank.lesson8.task.contentproviders.client.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.sberbank.lesson8.task.contentproviders.client.EditNoteActivity;
import ru.sberbank.lesson8.task.contentproviders.client.R;
import ru.sberbank.lesson8.task.contentproviders.client.resolvers.NoteContentResolver;

import static android.graphics.PorterDuff.Mode.SRC_ATOP;
import static ru.sberbank.lesson8.task.contentproviders.client.MainActivity.EDIT_NOTE_ACTIVITY_REQUEST_CODE;
import static ru.sberbank.lesson8.task.contentproviders.client.adapters.NoteContract.COLUMN_CONTENT;
import static ru.sberbank.lesson8.task.contentproviders.client.adapters.NoteContract.COLUMN_ID;
import static ru.sberbank.lesson8.task.contentproviders.client.adapters.NoteContract.NOTE_CONTENT;
import static ru.sberbank.lesson8.task.contentproviders.client.adapters.NoteContract.NOTE_ID;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private static final int MAX_NOTE_LENGTH = 140;

    private final LayoutInflater inflater;
    private Cursor notes;
    public static int textSize;
    public static int noteColor;
    public static int textColor;

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        public Long id;
        public String allText;
        public TextView note;
        public ImageView deleteBtn;
        public NoteViewHolder(View v) {
            super(v);
            deleteBtn = v.findViewById(R.id.delete_note);
            note = v.findViewById(R.id.notes_item_content);
            note.setTextSize(textSize);
            note.getBackground().setColorFilter(noteColor, SRC_ATOP);
            note.setTextColor(textColor);
            note.setOnClickListener((item) -> {
                Context context = item.getRootView().getContext();
                Intent intent = new Intent(context, EditNoteActivity.class);
                intent.putExtra(NOTE_ID, id);
                intent.putExtra(NOTE_CONTENT, allText);
                ((Activity)context).startActivityForResult(intent, EDIT_NOTE_ACTIVITY_REQUEST_CODE);
            });
            deleteBtn.setOnClickListener((item) -> {
                Context context = item.getRootView().getContext();
                new NoteContentResolver(context).delete(id);
            });
        }
    }

    public NoteAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public NoteAdapter.NoteViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        return new NoteViewHolder(inflater.inflate(R.layout.note, parent, false));
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        if (notes.moveToPosition(position)) {
            holder.id = notes.getLong(notes.getColumnIndexOrThrow(COLUMN_ID));
            String allText = notes.getString(notes.getColumnIndexOrThrow(COLUMN_CONTENT));
            holder.allText = allText;
            holder.note.setText(allText.length() > MAX_NOTE_LENGTH ? allText.substring(0, MAX_NOTE_LENGTH) + "..." : allText);
        }
    }

    public void setNotes(Cursor notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notes == null ? 0 : notes.getCount();
    }
}
