package com.swindells.map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SelectedLocationList
{
	public static final String KEY_NAME = "name";
	public static final String KEY_DESC = "description";
	public static final String KEY_LATITUDE = "lat";
	public static final String KEY_LONGITUDE = "long";
    public static final String KEY_ROWID = "_id";
    
    public static final String TAG = "SelectedDbAdapater";

    private static final String DATABASE_NAME = "selectiondata";
    private static final String DATABASE_TABLE = "selections";
    private static final int DATABASE_VERSION = 1;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table " + DATABASE_TABLE + "(" + KEY_ROWID + " integer primary key autoincrement, "
        + KEY_NAME + " text not null, "
        + KEY_DESC + " text, "
        + KEY_LATITUDE + " integer not null, "
        + KEY_LONGITUDE + " integer not null);";
    
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
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
    
    public SelectedLocationList(Context ctx)
    {
    	mCtx = ctx;
    	open();
    }
    

    private SelectedLocationList open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close()
    {
    	mDb.close();
    }
    
    public long addSelection(Location l)
    {
    	ContentValues values = new ContentValues();
    	values.put(KEY_LATITUDE, l.getLatitude());
    	values.put(KEY_LONGITUDE, l.getLongitude());
    	values.put(KEY_NAME, l.getTitle());
    	values.put(KEY_DESC, l.getDescription());
    	
    	return mDb.insert(DATABASE_TABLE, null, values);
    	
    }
    
    public boolean removeSelection(long id)
    {
    	return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + id, null) > 0;
    }
    
    public Cursor fetchAll()
    {
    	String[] columns = { KEY_ROWID, KEY_LATITUDE, KEY_LONGITUDE, KEY_NAME, KEY_DESC };
    	return mDb.query(DATABASE_TABLE, columns, null, null, null, null, null);
    }
    
    public boolean removeAll()
    {
    	return mDb.delete(DATABASE_TABLE, "1", null) > 0;
    }
    
    public int count()
    {
    	Cursor c = fetchAll();
    	return c.getCount();
    }
    
    public Cursor fetch(int id)
    {
    	String[] columns = { KEY_ROWID, KEY_LATITUDE, KEY_LONGITUDE, KEY_NAME, KEY_DESC };
    	return mDb.query(DATABASE_TABLE, columns, KEY_ROWID + " = " + Integer.toString(id), null, null, null, null);
    }
    
    public boolean remove(int id)
    {
    	String clause = KEY_ROWID + "=?";
    	String[] params = {id + ""};
    	int result = mDb.delete(DATABASE_TABLE, clause, params);
    	return result == 1;
    }
}
