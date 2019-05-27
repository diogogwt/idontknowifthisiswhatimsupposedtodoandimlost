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
        db.execSQL(DBNames.CREATE_TABLE_CATEGORIES);
        db.execSQL(DBNames.CREATE_TABLE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}