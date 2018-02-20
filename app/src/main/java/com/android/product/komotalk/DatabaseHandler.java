package com.android.product.komotalk;

/**
 * Created by Product on 14/07/2015.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "historyManager";
    public static final String DATABASE_TABLE = "history";

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_DATE = "date";



    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + DATABASE_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_PHONE_NUMBER + " TEXT," + KEY_DURATION + " TEXT,"
                + KEY_DATE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXIT " + DATABASE_TABLE);
        onCreate(db);
    }



    // Adding new contact
    void addHistory(History history) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, history.getName()); // Contact Name
        values.put(KEY_PHONE_NUMBER, history.getPhoneNumber()); // Contact Phone
        values.put(KEY_DURATION, history.get_duration());
        values.put(KEY_DATE, history.get_date());
        // Inserting Row
        db.insert(DATABASE_TABLE, null, values);
        db.close(); // Closing database connection
    }

    // Getting single history
    History getHistory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DATABASE_TABLE, new String[] { KEY_ID,
                        KEY_NAME, KEY_PHONE_NUMBER }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        History history = new History(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3),
                cursor.getString(4));
        // return
        return history;
    }

    // Getting All
    public List<History> getAllHistory() {
        List<History> historyList = new ArrayList<History>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE + " ORDER BY " + KEY_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                History history = new History();
                history.setID(Integer.parseInt(cursor.getString(0)));
                history.setName(cursor.getString(1));
                history.setPhoneNumber(cursor.getString(2));
                history.set_duration(cursor.getString(3));
                history.set_date(cursor.getString(4));
                // Adding contact to list
                historyList.add(history);
            } while (cursor.moveToNext());
        }

        // return contact list
        return historyList;
    }

    // Updating single contact
    public int updateHistory(History history) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, history.getName());
        values.put(KEY_PHONE_NUMBER, history.getPhoneNumber());
        values.put(KEY_DURATION, history.get_duration());
        values.put(KEY_DATE, history.get_date());
        // updating row
        return db.update(DATABASE_TABLE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(history.getID()) });
    }

    // Deleting single contact
    public void deleteHistory(History history) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, KEY_ID + " = ?",
                new String[] { String.valueOf(history.getID()) });
        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + DATABASE_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }


}
