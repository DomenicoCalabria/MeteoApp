package ch.supsi.dti.isin.meteoapp.activities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "test.db"; // nome del database
    private static final int VERSION = 1; // versione
    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String creationQuery = "create table "+ DatabaseSchema.Locations.NAME+"(" +
                "_id integer primary key autoincrement, " +
                DatabaseSchema.Locations.Cities.CITY_NAME +
                " , "+
                DatabaseSchema.Locations.Cities.LATITUDE +
                " , "+
                DatabaseSchema.Locations.Cities.LONGITUDE +
                ")";
        db.execSQL(creationQuery);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        /*String updateQuery = "create table "+DatabaseSchema.Locations.LATITUDE+"(" +
                "_id integer primary key autoincrement, " +
                DatabaseSchema.Locations.Cities.CITY_NAME +
                " , "+
                DatabaseSchema.Locations.Cities.LATITUDE +
                ")";
        db.execSQL(updateQuery);*/
    }
    //TODO: inserire funzionalità del database: salvare le città
}
