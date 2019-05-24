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

public class ProductsAdapter extends BaseAdapter {

    private static String TAG = "ProductsAdapter";

    private Context context;
    private DatabaseOpen dbOpen;
    private SQLiteDatabase db;
    private int[] ids;

    //para selecionar categorias receber um array com os ids ou nomes
    public ProductsAdapter(Context context) {
        this.context = context;
        this.dbOpen = new DatabaseOpen(context);
        this.db = dbOpen.getReadableDatabase();

        SQLiteDatabase db = dbOpen.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(id) from " + DBNames.PRODUCTS + ";", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        ids = new int[count];

        cursor = db.rawQuery("select id from " + DBNames.PRODUCTS + " order by name;", null);
        cursor.moveToFirst();
        for (int i = 0; cursor.moveToNext(); i++) {
            ids[i] = cursor.getInt(0);
        }
        cursor.close();
        Log.i(TAG, Arrays.toString(ids));
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
        Cursor cursor = db.query(DBNames.PRODUCTS, new String[]{"name", "image"}, "id=?", selectionArgs, null, null, "name");
        cursor.moveToFirst();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.product_layout, parent, false);
        }

        TextView nameView = convertView.findViewById(R.id.product_name);
        ImageView imageView = convertView.findViewById(R.id.product_image);

        nameView.setText(cursor.getString(0));
        try {
            String fileName = cursor.getString(1);
            Bitmap bitmap = BitmapFactory.decodeStream(context.getResources().getAssets().open(fileName));
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.e("BIG OPPSIE", "putting image on grid element: ", e);
        }
        cursor.close();

        return convertView;
    }
}
