package com.bringitlist.bringit.Other;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bringitlist.bringit.App;
import com.bringitlist.bringit.Database.DBNames;
import com.bringitlist.bringit.Database.DatabaseOpen;
import com.bringitlist.bringit.R;

import java.util.Arrays;

public class ProductsAdapter extends BaseAdapter {

    private static String TAG = "ProductsAdapter";

    private Context context;
    private App app;
    private SQLiteDatabase db;
    private int[] ids;

    //para selecionar categorias receber um array com os ids ou nomes
    public ProductsAdapter(Context context, Integer[] where) {
        this.context = context;
        this.app = (App) context.getApplicationContext();
        this.db = ((App) context.getApplicationContext()).getReadableDB();

        if (where == null) {

            Cursor cursor = db.rawQuery("select id from " + DBNames.PRODUCTS + " order by name;", null);
            ids = new int[cursor.getCount()];

            cursor.moveToFirst();
            for (int i = 0; cursor.moveToNext(); i++) {
                ids[i] = cursor.getInt(0);
            }
            cursor.close();
        } else {
            String whereString = TextUtils.join(",", where);

            Cursor cursor = db.rawQuery("select id from " + DBNames.PRODUCTS + " where cat_id in (" + whereString + ") order by name;", null);
            ids = new int[cursor.getCount()];
            cursor.moveToFirst();
            for (int i = 0; cursor.moveToNext(); i++) {
                ids[i] = cursor.getInt(0);
            }
            cursor.close();
        }

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
    public View getView(final int position, View convertView, ViewGroup parent) {

        String[] selectionArgs = {String.valueOf(getItemId(position))};
        Cursor cursor = db.query(DBNames.PRODUCTS, new String[]{"name", "image"}, "id=?", selectionArgs, null, null, null);
        //Cursor cursor = db.rawQuery("select name,image from products where id=?",selectionArgs);
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

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                IdQuantChecked das = new IdQuantChecked();
                das.id = ids[position];
                app.userItems.add(das);
                ((Activity) context).finish();
            }
        });


        return convertView;
    }
}
