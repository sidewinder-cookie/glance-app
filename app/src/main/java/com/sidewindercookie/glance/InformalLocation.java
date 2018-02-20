package com.sidewindercookie.glance;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by hydrabolt on 2/20/18.
 */

public class InformalLocation implements Parcelable {
    private String name;
    private LatLng latLng = new LatLng(-1, -1);

    public String getName() {
        return name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public Location getLocation() {
        Location loc = new Location("");
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        return loc;
    }

    public InformalLocation(Place place) {
        this.name = place.getName().toString();
        this.latLng = place.getLatLng();
    }

    public InformalLocation(String name) {
        this.name = name;
    }

    protected InformalLocation(Parcel in) {
        name = in.readString();
        double[] latLngDouble = new double[2];
        in.readDoubleArray(latLngDouble);
        latLng = new LatLng(latLngDouble[0], latLngDouble[1]);
    }

    public static final Creator<InformalLocation> CREATOR = new Creator<InformalLocation>() {
        @Override
        public InformalLocation createFromParcel(Parcel in) {
            return new InformalLocation(in);
        }

        @Override
        public InformalLocation[] newArray(int size) {
            return new InformalLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeDoubleArray(new double[] { latLng.latitude, latLng.longitude });
    }
}
