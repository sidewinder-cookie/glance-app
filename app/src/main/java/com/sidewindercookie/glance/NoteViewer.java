package com.sidewindercookie.glance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class NoteViewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_viewer);

        String name = getIntent().getStringExtra("name");
        String details = getIntent().getStringExtra("details");

        Log.d("GLANCE", "im alive bitch");
        Log.d("GLANCE", details);

        ((TextView) findViewById(R.id.cardViewerTitle)).setText(name);
        ((TextView) findViewById(R.id.cardViewerDetails)).setText(details);

        Log.d("GLANCE", getIntent().getExtras().toString());
    }
}
