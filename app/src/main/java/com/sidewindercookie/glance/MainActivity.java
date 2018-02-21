package com.sidewindercookie.glance;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    int WAIT_FOR_RESPONSE = 5786;
    int RETURN_FROM_THE_THINGY = 76;

    List<Note> notes = new ArrayList<Note>();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter dataAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNoteMaker();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.note_recycler_view);
        recyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        dataAdapter = new NotesViewAdapter(notes);
        recyclerView.setAdapter(dataAdapter);

        startLocationService();
    }

    private String locationToLatLng(Location location) {
        return location.getLatitude() + "," + location.getLongitude();
    }

    public void generateGoogleMapsURL() {
        String base = "http://maps.google.com/maps?daddr=";
        for (int i = 0; i < notes.size(); i++) {
            Location location = notes.get(i).getInformalLocation().getLocation();
            base += locationToLatLng(location) + "+to:";
        }
        base = base.substring(0, base.length() - 4);
        Log.d("fjgngkdgsdg", base);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(base));
        startActivity(i);
    }

    public void startLocationService() {
        Intent intent = new Intent(this, LocationWatcher.class);
        LocationTrigger[] arr = new LocationTrigger[notes.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new LocationTrigger(notes.get(i));
        }
        intent.putExtra("note-triggers", arr);
        startService(intent);
    }

    public void openNoteMaker() {
        startActivityForResult(new Intent(this, NoteMaker.class), WAIT_FOR_RESPONSE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WAIT_FOR_RESPONSE && resultCode == RESULT_OK) {
            String name = data.getStringExtra("name");
            String details = data.getStringExtra("details");
            InformalLocation location = (InformalLocation) data.getParcelableExtra("informalLocation");
            notes.add(new Note(name, details, location));
            dataAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Note created", Toast.LENGTH_SHORT).show();
            startLocationService();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            generateGoogleMapsURL();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
