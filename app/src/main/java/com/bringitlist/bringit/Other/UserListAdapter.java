package com.bringitlist.bringit.Other;

import android.app.Activity;
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

import com.bringitlist.bringit.App;
import com.bringitlist.bringit.Database.DBNames;
import com.bringitlist.bringit.Database.DatabaseOpen;
import com.bringitlist.bringit.R;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Long> ids;
    private SQLiteDatabase db;

    public UserListAdapter(Context context) {
        this.context = context;
        this.ids = ((App) context.getApplicationContext()).userItems;
        this.db = new DatabaseOpen(context).getReadableDatabase();
    }

    @Override
    public int getCount() {
        return ids.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return ids.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        String[] selectionArgs = new String[]{String.valueOf(getItemId(position))};
        Cursor cursor = db.query(DBNames.PRODUCTS, new String[]{"name", "image"}, "id=?", selectionArgs, null, null, null);
        if (!cursor.moveToFirst()) {
            return null;
        }

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

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ids.remove(position);
                notifyDataSetChanged();
                return true;
            }
        });

        return convertView;
    }
}
