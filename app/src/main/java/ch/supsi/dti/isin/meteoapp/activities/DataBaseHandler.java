package ch.supsi.dti.isin.meteoapp.activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBaseHandler extends SQLiteOpenHelper {

    // THANK YOU https://blog.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/

    //The Android's default system path of your application database.
    private static String DB_PATH = "ch.supsi.dti.isin.meteoapp/databases/";

    private static String DB_NAME = "posti.db";

    private static final int VERSION = 1; // versione

    private SQLiteDatabase db;

    private final Context myContext;

    public DataBaseHandler(Context context, Context myContext) {
        super(context, DB_NAME, null, VERSION);
        this.myContext = myContext;
    }

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

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
                DataBaseSchema.Locations.Cities.SERVICE_ID + ", " +
                DataBaseSchema.Locations.Cities.CITY_NAME + ", "+
                DataBaseSchema.Locations.Cities.LATITUDE + ", "+
                DataBaseSchema.Locations.Cities.LONGITUDE +
                ")";
        this.db.execSQL(creationQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

    public void saveCity(String nome, double latitudine, double longitudine){
        String query = "insert into "+DataBaseSchema.Locations.NAME+" values (" +
                "null" + ","+
                nome + ","+
                latitudine + ","+
                longitudine + ","+
                ")";
        db.execSQL(query);
    }

    public Cursor getCities(){
        String[] colonne = {DataBaseSchema.Locations.Cities.SERVICE_ID, DataBaseSchema.Locations.Cities.CITY_NAME,DataBaseSchema.Locations.Cities.LATITUDE, DataBaseSchema.Locations.Cities.LONGITUDE};
        return db.query(DataBaseSchema.Locations.NAME,colonne,"*",null,null,null,null);
    }

    public void updateCity(String city, double newLat, double newLong){
        ContentValues cv = new ContentValues();
        cv.put(DataBaseSchema.Locations.Cities.CITY_NAME, "city");
        cv.put(DataBaseSchema.Locations.Cities.LATITUDE, newLat);
        cv.put(DataBaseSchema.Locations.Cities.LONGITUDE, newLong);
        db.update(DataBaseSchema.Locations.NAME,cv,DataBaseSchema.Locations.Cities.CITY_NAME+" = "+city,null);
    }

}
