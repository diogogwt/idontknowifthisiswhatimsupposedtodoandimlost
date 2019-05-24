package com.bringitlist.bringit.Other;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bringitlist.bringit.Database.DBNames;
import com.bringitlist.bringit.Database.DatabaseOpen;
import com.bringitlist.bringit.R;

import java.util.Arrays;

public class CategoriesAdapter extends BaseAdapter {


    private Context context;
    private DatabaseOpen dbOpen;
    private SQLiteDatabase db;
    private int[] ids;

    public CategoriesAdapter(Context context) {
        this.context = context;
        this.dbOpen = new DatabaseOpen(context);
        this.db = dbOpen.getReadableDatabase();

        SQLiteDatabase db = dbOpen.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(id) from " + DBNames.CATEGORIES + ";", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        ids = new int[count];

        cursor = db.rawQuery("select id from " + DBNames.CATEGORIES + " order by name;", null);
        cursor.moveToFirst();
        for (int i = 0; cursor.moveToNext(); i++) {
            ids[i] = cursor.getInt(0);
        }
        cursor.close();
    }

    @Override
    public int getCount() {
        return ids.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return ids[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String[] selectionArgs = {String.valueOf(getItemId(position))};
        Cursor cursor = db.query(DBNames.PRODUCTS, new String[]{"name"}, "id=?", selectionArgs, null, null, null);
        cursor.moveToFirst();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_cat_checkbox, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.row_cat_textview);
        textView.setText(cursor.getString(0));


        cursor.close();

        return convertView;
    }
}
