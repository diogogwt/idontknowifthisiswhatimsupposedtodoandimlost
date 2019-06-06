package com.bringitlist.bringit.Other;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bringitlist.bringit.App;
import com.bringitlist.bringit.R;

public class CategoriesAdapter extends BaseAdapter {

    public static String TAG = "CategoriesAdapter";

    private Context context;
    private SQLiteDatabase db;
    IdAndChecked[] items;


    public CategoriesAdapter(Context context, IdAndChecked[] items) {
        this.context = context;
        this.db = ((App) context.getApplicationContext()).getReadableDB();
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return items[position].id;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        String[] selectionArgs = {String.valueOf(getItemId(position))};
        //Cursor cursor = db.query(DBNames.CATEGORIES, new String[]{"name"}, "id=?", selectionArgs, null, null, null);
        Cursor cursor = db.rawQuery("select name from categories where id=?", selectionArgs);
        cursor.moveToFirst();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_cat_checkbox, parent, false);
        }

        final CheckBox checkBox = convertView.findViewById(R.id.row_cat_checkbox);
        checkBox.setChecked(items[position].checked);
        TextView textView = convertView.findViewById(R.id.row_cat_textview);
        textView.setText(cursor.getString(0));

        cursor.close();
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items[position].reverse();
                checkBox.setChecked(!checkBox.isChecked());
            }
        });

        return convertView;
    }
}

