package ch.supsi.dti.isin.meteoapp.model;

import android.content.Context;
import android.database.Cursor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ch.supsi.dti.isin.meteoapp.activities.DataBaseHandler;

public class LocationsHolder {

    private static LocationsHolder sLocationsHolder;
    private List<Location> mLocations;
    private DataBaseHandler dbHandler;

    public static LocationsHolder get(Context context) {
        if (sLocationsHolder == null)
            sLocationsHolder = new LocationsHolder(context);

        return sLocationsHolder;
    }

    private LocationsHolder(Context context) {
        mLocations = new ArrayList<>();
        dbHandler = new DataBaseHandler(context);

        //read from database and load saved locations
        try {
            dbHandler.createAndOpenDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Cursor c = dbHandler.getCities();

        if(c.moveToFirst()){
            while(!c.isAfterLast()){
                mLocations.add(new Location(c.getString(2), c.getDouble(3), c.getDouble(4)));
                c.moveToNext();
            }
        }

        c.close();
    }

    @Override
    protected void finalize(){
        dbHandler.close();

        try {
            super.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public List<Location> getLocations() {
        return mLocations;
    }

    public Location getLocation(UUID id) {
        for (Location location : mLocations) {
            if (location.getId().equals(id))
                return location;
        }

        return null;
    }

    public void save(Location l) {
        dbHandler.saveCity(l);
    }
}
