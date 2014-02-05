// @author Andrew Gulla (100395486)
package ca.uoit.AndrewGulla.notepad;

//import java.text.SimpleDateFormat;
//import java.util.Calendar;
import java.text.DateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotesAdapter {
//Keys that will be used in database entries
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";
    
    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, 
			DateFormat.SHORT);
    public static final String KEY_DATE = "date";
    //Create the database
    private static final String TAG = "NotesAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_CREATE =
        "create table notes (_id integer primary key autoincrement, "
        + "title text not null, body text not null, date text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

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

    public NotesAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    
    /*Opens the database and creates a new one if not created*/
    public NotesAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    /*Creates a new note using the values given from the values entered.
     * '-1' is returned if failure to enter. RowID is returned if success
     */
    public long createNote(String title, String body, String date) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body); 
        date = df.format(new Date());
        initialValues.put(KEY_DATE, date);
             
        
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        //initialValues.put(KEY_DATE, timeStamp);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /*Deletes a specific note with a specific rowID*/
    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /*Cursor is returned over all notes*/
    public Cursor fetchAllNotes() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_BODY, KEY_DATE}, null, null, null, null, null);
    }

    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_TITLE, KEY_BODY, KEY_DATE}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
       
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
        return mCursor;
    }
   
    /*Allows user to edit an already existent note*/
    public boolean updateNote(long rowId, String title, String body, String date) {
    	ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);
        date = df.format(new Date());
        args.put(KEY_DATE, date);
        
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        //args.put(KEY_DATE, timeStamp);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
