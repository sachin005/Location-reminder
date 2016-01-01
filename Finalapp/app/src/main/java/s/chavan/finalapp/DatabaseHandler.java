package s.chavan.finalapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sachin on 5/1/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static int VERSION = 1;
    private String DB_NAME = "finaldb";
    private String TABLE_NAME = "locations", POLY_TABLE = "poly";

    private int ID = 1, LAT_NUM = 2, LNG_NUM = 3, NOTE_NUM = 4, TIME_NUM = 5;

    public static final String ROW_ID = "id";
    public static final String LAT = "lat";
    public static final String LNG = "lng";



    public static final String NOTE = "note";
    public static final String DATE_TIME = "datetime";
    public final String POLY_LAT = "lat";
    public final String POLY_LNG = "lng";

    public DatabaseHandler(Context context) {
        super(context, "finaldb", null, VERSION);
//        this.db = getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table " + TABLE_NAME + " ( " +
                ROW_ID + " INTEGER primary key , " +
                LAT + " double , " +
                LNG + " double , " +
                NOTE + " text , " +
                DATE_TIME + " text " +
                " ) ";
        String createPolyTable = "create table " + POLY_TABLE + " ( " +
                POLY_LAT + " double, " +
                POLY_LNG + " double)";

        db.execSQL(createTable);
        db.execSQL(createPolyTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop older table if existing
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertData(LocationData locationData) {
        SQLiteDatabase db;
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ROW_ID, locationData.getId());
        values.put(LAT, locationData.getLat());
        values.put(LNG, locationData.getLng());
        values.put(NOTE, locationData.getNote());
        values.put(DATE_TIME, locationData.getDate());

        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public void insertNote(String note) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET NOTE = '" + note + "' WHERE " + ROW_ID +
                " = (SELECT MAX(" + ROW_ID + ") FROM " + TABLE_NAME+ ")");
        db.close();
    }

    public void removeData(String id) {
        SQLiteDatabase db, dbr;
        dbr = this.getReadableDatabase();
        Cursor cursor = dbr.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        int row = cursor.getInt(cursor.getColumnIndex(ROW_ID));
        db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " where " + ROW_ID + "='" + row + "'");
        Log.d("removeData", "deleted " +row);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public LocationData getData(Context context) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
        Date date;
        try {
            date = dateFormat.parse(cursor.getString(TIME_NUM));
        }
        catch(Exception e) {
            Calendar c = new GregorianCalendar();
            c.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            date = c.getTime();
        }
        if(cursor.moveToFirst()) {
            LocationData locationData = new LocationData(cursor.getLong(cursor.getColumnIndex(ROW_ID)),
                    cursor.getDouble(cursor.getColumnIndex(LAT)), cursor.getDouble(cursor.getColumnIndex(LNG)),
                    cursor.getString(cursor.getColumnIndex(NOTE)), cursor.getString(cursor.getColumnIndex(DATE_TIME)));

//            Toast.makeText(context, cursor.getColumnName(2) + cursor.getColumnName(3), Toast.LENGTH_SHORT).show();
            return locationData;
        }
        else {
            Log.d("locationData:", "null");
            return null;
        }

    }

    public boolean hasData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        boolean hasData = cursor.moveToFirst();
        Log.d("hasData", hasData + "");
        cursor.close();
        return hasData;
    }

    public boolean isEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT count(*) FROM " + TABLE_NAME;
        String checkTable = "SELECT name FROM sqlite_master WHERE type='table' AND name='locations'";
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(checkTable, null);
//        cursor.close();
        return cursor.moveToFirst();
//        return cursor.getInt(cursor.getColumnIndex(ROW_ID));

    }

    public int deleteData(String rowid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "";
        return db.delete(TABLE_NAME, "1", null);
    }

    public boolean insertPolyPoints(Location location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(POLY_LAT, location.getLatitude());
        values.put(POLY_LNG, location.getLongitude());
        long insert = db.insert(POLY_TABLE, null, values);
        db.close();
        if(insert != -1) {
            return true;
        }
        return false;
    }

    public void deletePolyPoints() {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete the complete table
        db.delete(POLY_TABLE, "1", null);
    }

    public List<LatLng> getPolyPoints() {
        List<LatLng> latLngs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String polyQuery = "SELECT * FROM " + POLY_TABLE;

        Cursor cursor = db.rawQuery(polyQuery, null);

        if(cursor.moveToFirst()) {
            LatLng latLng;
            double lat = cursor.getDouble(cursor.getColumnIndex(POLY_LAT));
            double lng = cursor.getDouble(cursor.getColumnIndex(POLY_LNG));
            latLng = new LatLng(lat, lng);
            do {
                latLngs.add(latLng);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return latLngs;
    }

    public String getNote() {
        String note = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if(cursor.moveToFirst()) {
            note = cursor.getString(cursor.getColumnIndex(NOTE));
        }
        return note;
    }
}
