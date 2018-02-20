package com.sidewindercookie.glance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

public class NoteMaker extends AppCompatActivity {

    int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_maker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.noteMakerPlusButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("name", "thing");
                resultIntent.putExtra("details", "more things");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
    }

    public void openLocationPicker(View view) {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Error e) {
            Toast.makeText(this, "sorry it broke", Toast.LENGTH_SHORT).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }
}
