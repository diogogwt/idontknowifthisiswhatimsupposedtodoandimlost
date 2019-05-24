package com.bringitlist.bringit.Other;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bringitlist.bringit.Database.DatabaseNames;
import com.bringitlist.bringit.Database.DatabaseOpen;
import com.bringitlist.bringit.R;

import java.sql.DatabaseMetaData;

public class ProductsAdapter extends BaseAdapter {

    private Context context;
    private DatabaseOpen dbOpen;
    private SQLiteDatabase db;

    //para selecionar categorias receber um array com os ids ou nomes
    public ProductsAdapter(Context context) {
        this.context = context;
        this.dbOpen = new DatabaseOpen(context);
        this.db = dbOpen.getReadableDatabase();
    }

    @Override
    public int getCount() {
        SQLiteDatabase db = dbOpen.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(id) from produtcs;", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.product_layout, parent, false);
        }

        String[] selectionArgs = {String.valueOf(getItemId(position))};
        Cursor cursor = db.query(DatabaseNames.TABLE_PRODUCTS, null, "id=?", selectionArgs, null, null, null);

        TextView nameView = convertView.findViewById(R.id.product_name);
        ImageView imageView = convertView.findViewById(R.id.product_image);


        nameView.setText(cursor.getString(cursor.getColumnIndex(DatabaseNames.COLUMN_NAME)));

        String resourceName = cursor.getString(cursor.getColumnIndex(DatabaseNames.COLUMN_NAME));
        int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        imageView.setImageResource(resourceId);

        cursor.close();

        return convertView;
    }
}
