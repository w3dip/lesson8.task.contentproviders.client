package ru.sberbank.lesson8.task.contentproviders.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ru.sberbank.lesson8.task.contentproviders.client.adapters.NoteAdapter;
import ru.sberbank.lesson8.task.contentproviders.client.resolvers.NoteContentResolver;

import static ru.sberbank.lesson8.task.contentproviders.client.adapters.NoteAdapter.noteColor;
import static ru.sberbank.lesson8.task.contentproviders.client.adapters.NoteAdapter.textColor;
import static ru.sberbank.lesson8.task.contentproviders.client.adapters.NoteAdapter.textSize;
import static ru.sberbank.lesson8.task.contentproviders.client.adapters.NoteContract.NOTE_CONTENT;
import static ru.sberbank.lesson8.task.contentproviders.client.adapters.NoteContract.NOTE_ID;
import static ru.sberbank.lesson8.task.contentproviders.client.resolvers.NoteContentResolver.NOTE_URI;
import static ru.sberbank.lesson8.task.contentproviders.client.adapters.NoteContract.COLUMN_CONTENT;
import static ru.sberbank.lesson8.task.contentproviders.client.adapters.NoteContract.COLUMN_ID;
import static ru.sberbank.lesson8.task.contentproviders.client.utils.Helpers.getRealProgress;
import static ru.sberbank.lesson8.task.contentproviders.client.utils.Helpers.getSettingName;

public class MainActivity extends AppCompatActivity {
    private static final int LOADER_NOTES = 1;
    public static final int NEW_NOTE_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_NOTE_ACTIVITY_REQUEST_CODE = 2;

    private SharedPreferences settings;

    private NoteAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private NoteContentResolver noteContentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.createNoteBtn)
                .setOnClickListener((v) ->
                        startActivityForResult(new Intent(MainActivity.this, CreateNoteActivity.class),
                                NEW_NOTE_ACTIVITY_REQUEST_CODE)
                );
        findViewById(R.id.settingsBtn).setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        recyclerView = findViewById(R.id.notes);
        recyclerView.setHasFixedSize(true);

        adapter = new NoteAdapter(this);
        noteContentResolver = new NoteContentResolver(this);

        settings = getApplicationContext().getSharedPreferences(getSettingName(getResources(), R.string.settings_filename), MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        getSupportLoaderManager().restartLoader(LOADER_NOTES, null, loaderCallbacks);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        super.onResume();
    }

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks =
            new LoaderManager.LoaderCallbacks<Cursor>() {

                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    switch (id) {
                        case LOADER_NOTES:
                            return new CursorLoader(getApplicationContext(),
                                    NOTE_URI,
                                    new String[]{COLUMN_ID, COLUMN_CONTENT},
                                    null, null, null);
                        default:
                            throw new IllegalArgumentException();
                    }
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    switch (loader.getId()) {
                        case LOADER_NOTES:
                            adapter.setNotes(data);
                            break;
                    }
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    switch (loader.getId()) {
                        case LOADER_NOTES:
                            adapter.setNotes(null);
                            break;
                    }
                }

            };

    @Override
    protected void onStart() {
        textSize = Integer.valueOf(getRealProgress(settings.getInt(getSettingName(getResources(), R.string.text_size_setting), 0)));
        textColor = settings.getInt(getSettingName(getResources(), R.string.text_color_setting), getResources().getColor(R.color.text_color, null));
        noteColor = settings.getInt(getSettingName(getResources(), R.string.note_color_setting), getResources().getColor(R.color.note_color, null));
        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(null);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();
        super.onStart();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == NEW_NOTE_ACTIVITY_REQUEST_CODE) {
                noteContentResolver.create(data.getStringExtra(NOTE_CONTENT));
            }
            if (requestCode == EDIT_NOTE_ACTIVITY_REQUEST_CODE) {
                noteContentResolver.update(data.getLongExtra(NOTE_ID, 0), data.getStringExtra(NOTE_CONTENT));
            }
        }
    }
}

