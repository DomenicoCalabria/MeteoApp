package ch.supsi.dti.isin.meteoapp.model;

import java.util.UUID;

public class Location {
    private UUID Id;
    private String mName;
    private double latitude;
    private double longitude;
    private boolean latLonSet = false;

    public Location(String name) {
        Id = UUID.randomUUID();
        this.mName = name;
    }

    public Location(String name, double lat, double lon) {
        Id = UUID.randomUUID();
        this.mName = name;
        this.latitude = lat;
        this.longitude = lon;
        latLonSet = true;
    }

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Location() {
        Id = UUID.randomUUID();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        this.latLonSet = true;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        this.latLonSet = true;
    }

    public boolean isLatLonSet() {
        return latLonSet;
    }
}