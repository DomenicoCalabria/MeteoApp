package ch.supsi.dti.isin.meteoapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;

public class DataBaseHandler extends SQLiteOpenHelper {

    // THANK YOU https://blog.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/

    private static String DATABASE_NAME = "posti.db";
    private static final int VERSION = 2; // versione
    private SQLiteDatabase db;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    /**
     * Open db and create if it does not exist
     * */
    public void createAndOpenDataBase() throws IOException{
        db = this.getWritableDatabase();
    }

    @Override
    public synchronized void close() {
        if(db != null)
            db.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String creationQuery = "create table "+ DataBaseSchema.Locations.NAME+"(" +
                "_id integer primary key autoincrement, " +
                DataBaseSchema.Locations.Cities.CITY_NAME + ", "+
                DataBaseSchema.Locations.Cities.LATITUDE + ", "+
                DataBaseSchema.Locations.Cities.LONGITUDE +
                ")";
        db.execSQL(creationQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseSchema.Locations.NAME);
        onCreate(db);
    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

    public void saveCity(Location l){
        String query = "insert into "+DataBaseSchema.Locations.NAME+" values (" +
                "null" + ","+
                "'"+l.getName().replaceAll("'", " ")+ "',"+
                l.getLatitude() + ","+
                l.getLongitude()+
                ");";
        db.execSQL(query);
    }

    public Cursor getCities(){
        return db.query(DataBaseSchema.Locations.NAME,null,null,null,null,null,null);
    }

    public void updateCity(String city, double newLat, double newLong){
        ContentValues cv = new ContentValues();
        cv.put(DataBaseSchema.Locations.Cities.CITY_NAME, "city");
        cv.put(DataBaseSchema.Locations.Cities.LATITUDE, newLat);
        cv.put(DataBaseSchema.Locations.Cities.LONGITUDE, newLong);
        db.update(DataBaseSchema.Locations.NAME,cv,DataBaseSchema.Locations.Cities.CITY_NAME+" = "+city,null);
    }

    public void removeCity(String cName){
        String query = "DELETE FROM "+DataBaseSchema.Locations.NAME+" WHERE nome_città = '"+cName+"'";
        db.execSQL(query);
    }
}