package ru.sberbank.lesson8.task.contentproviders.client.resolvers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import static ru.sberbank.lesson8.task.contentproviders.client.adapters.NoteContract.COLUMN_CONTENT;
import static ru.sberbank.lesson8.task.contentproviders.client.adapters.NoteContract.TABLE_NAME;

public class NoteContentResolver {
    public static final Uri NOTE_URI = Uri.parse("content://ru.sberbank.lesson8.task.contentproviders.provider/" + TABLE_NAME);

    private ContentResolver contentResolver;

    public NoteContentResolver(Context context) {
        this.contentResolver = context.getContentResolver();
    }

    public void create(String text) {
        contentResolver.insert(NOTE_URI, getValues(text));
    }

    public void update(long id, String text) {
        contentResolver.update(ContentUris.withAppendedId(NOTE_URI, id), getValues(text), null, null);
    }

    public void delete(long id) {
        contentResolver.delete(ContentUris.withAppendedId(NOTE_URI, id),null, null);
    }

    private ContentValues getValues(String text) {
        ContentValues note = new ContentValues();
        note.put(COLUMN_CONTENT, text);
        return note;
    }
}
