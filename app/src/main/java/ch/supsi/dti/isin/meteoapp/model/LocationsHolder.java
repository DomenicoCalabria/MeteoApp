package ch.supsi.dti.isin.meteoapp.model;

import android.content.Context;
import android.database.Cursor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocationsHolder {

    private static LocationsHolder sLocationsHolder;
    private List<Location> mLocations;
    private DataBaseHandler dbHandler;
    private Cursor c;

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

        c = dbHandler.getCities();

        if(c.moveToFirst()){
            while(!c.isAfterLast()){
                mLocations.add(new Location(c.getString(1), c.getDouble(2), c.getDouble(3)));
                c.moveToNext();
            }
        }
    }

    @Override
    protected void finalize(){
        c.close();
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

    public boolean exist(String locName){
        for(Location l : mLocations)
            if(l.getName().equals(locName))
                return true;

        return false;
    }

    public void remove(String locName){
        dbHandler.removeCity(locName);
    }
}
