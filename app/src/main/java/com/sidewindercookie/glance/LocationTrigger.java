package com.sidewindercookie.glance;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationTrigger implements Parcelable {

    private String name,  details, url;
    private InformalLocation informalLocation;
    int proximity = 100;

    public LocationTrigger(Note note) {
        name = note.getName();
        details = note.getDetails();
        informalLocation = note.getInformalLocation();
    }

    public LocationTrigger(String name, String details, String url) {
        this.name = name;
        this.details = details;
        this.informalLocation = new InformalLocation(name);
        this.url = url;
    }

    public LocationTrigger(Parcel parcel) {
        name = parcel.readString();
        details = parcel.readString();
        informalLocation = parcel.readParcelable(InformalLocation.class.getClassLoader());
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public InformalLocation getInformalLocation() {
        return informalLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(details);
        parcel.writeParcelable(informalLocation, i);
    }

    public static final Parcelable.Creator<LocationTrigger> CREATOR = new Creator<LocationTrigger>() {
        @Override
        public LocationTrigger createFromParcel(Parcel parcel) {
            return new LocationTrigger(parcel);
        }

        @Override
        public LocationTrigger[] newArray(int i) {
            return new LocationTrigger[i];
        }
    };
}
