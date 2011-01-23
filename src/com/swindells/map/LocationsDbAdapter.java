package com.swindells.map;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocationsDbAdapter
{
	public static final String KEY_NAME = "name";
	public static final String KEY_DESC = "description";
	public static final String KEY_LATITUDE = "lat";
	public static final String KEY_LONGITUDE = "long";
    public static final String KEY_ROWID = "_id";
    
    public static final String TAG = "LocationDbAdapater";

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table locations (_id integer primary key autoincrement, "
        + "title text not null, body text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "locations";
    private static final int DATABASE_VERSION = 2;
    
    private Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    public static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }
    
    public LocationsDbAdapter(Context ctx)
    {
    	mCtx = ctx;
    }
    

    public LocationsDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
}
