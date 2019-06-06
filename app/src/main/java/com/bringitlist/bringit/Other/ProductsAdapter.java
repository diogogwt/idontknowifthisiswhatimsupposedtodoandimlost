package com.bringitlist.bringit.Other;

import android.content.Context;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bringitlist.bringit.App;
import com.bringitlist.bringit.Database.DBNames;
import com.bringitlist.bringit.NewEditProductActivity;
import com.bringitlist.bringit.R;

public class ProductsAdapter extends BaseAdapter {

    private static String TAG = "ProductsAdapter";

    public Context context;
    private App app;
    private SQLiteDatabase db;
    public IdAndChecked[] ids;
    public Integer[] where;

    //para selecionar categorias receber um array com os ids ou nomes
    public ProductsAdapter(Context context, Integer[] where) {
        this.context = context;
        this.app = (App) context.getApplicationContext();
        this.db = app.getReadableDB();
        this.where = where;

        Cursor cursor = null;

        if (where == null) {
            cursor = db.rawQuery("select id from products order by cat_id,name;", null);

        } else {
            String whereString = TextUtils.join(",", where);
            cursor = db.rawQuery("select id from products where cat_id in (" + whereString + ") order by cat_id,name;", null);
        }

        ids = new IdAndChecked[cursor.getCount()];

        for (int i = 0; cursor.moveToNext(); i++) {
            ids[i] = new IdAndChecked();
            ids[i].id = cursor.getInt(0);
            ids[i].checked = false;
        }
        cursor.close();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
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
        return ids[position].id;
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
        CheckBox checkBox = convertView.findViewById(R.id.product_checkbox_right);

        nameView.setText(cursor.getString(0));

        String fileName = cursor.getString(1);
        App.loadImageToView(context, fileName, imageView);

        cursor.close();

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ids[position].reverse();
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewEditProductActivity.class);
                intent.putExtra("prod_id", ids[position].id);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
