package com.bringitlist.bringit.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpen extends SQLiteOpenHelper {

    private static final int dbVersion = 100;
    private static final String dbName = "Produtos";

    public DatabaseOpen(Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseNames.CREATE_TABLE_CATEGORIES);
        db.execSQL(DatabaseNames.CREATE_TABLE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}



/***






Read from database
 SQLiteDatabase db = dbHelper.getReadableDatabase();
     Cursor cursor = db.query(
     FeedEntry.TABLE_NAME,   // The table to query
     projection,             // String[] The array of columns to return (pass null to get all)
     selection,              // String The columns for the WHERE clause
     selectionArgs,          // String[] The values for the WHERE clause
     null,                   // don't group the rows
     null,                   // don't filter by row groups
     sortOrder               // The sort order
 );


 */