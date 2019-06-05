package com.bringitlist.bringit.Other;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bringitlist.bringit.App;
import com.bringitlist.bringit.Database.DBNames;
import com.bringitlist.bringit.Database.DatabaseOpen;
import com.bringitlist.bringit.R;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter {

    public static String TAG = "UserListAdapter";
    public Context context;
    private SQLiteDatabase db;
    private ArrayList<IdQuantChecked> items;

    public UserListAdapter(Context context) {
        this.context = context;
        this.items = ((App) context.getApplicationContext()).userItems;
        this.db = new DatabaseOpen(context).getReadableDatabase();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Log.i(TAG, "getView: Start");
        String[] selectionArgs = new String[]{String.valueOf(getItemId(position))};
        Cursor cursor = db.query(DBNames.PRODUCTS, new String[]{"name", "image"}, "id=?", selectionArgs, null, null, null);
        if (!cursor.moveToFirst()) {
            return null;
        }

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.user_product_layout, parent, false);
        }
        Log.i(TAG, "getView: Inflated");

        TextView nameView = convertView.findViewById(R.id.product_name);
        ImageView imageView = convertView.findViewById(R.id.product_image);
        Button plusBtn = convertView.findViewById(R.id.product_plus_btn);
        Button minusBtn = convertView.findViewById(R.id.product_minus_btn);
        final EditText quantView = convertView.findViewById(R.id.product_quant);

        quantView.setText("test");
        Log.i(TAG, "getView: Got Views");
        Log.i(TAG, "getView: " + items.get(position).amount);
        quantView.setText("" + items.get(position).amount);
        Log.i(TAG, "getView: Got quant");
        nameView.setText(cursor.getString(0));
        try {
            Log.i(TAG, "getView: Start Image");
            String fileName = cursor.getString(1);
            Bitmap bitmap = BitmapFactory.decodeStream(context.getResources().getAssets().open(fileName));
            imageView.setImageBitmap(bitmap);
            Log.i(TAG, "getView: Bitmap Set");
        } catch (Exception e) {
            Log.e("BIG OPPSIE", "putting image on grid element: ", e);
        }
        cursor.close();

        /**---------------------------------Listeners------------------------------------*/
        final View view = convertView;

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Tens a certeza que desejas remover da lista?")
                        .setNegativeButton("Não", null)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                items.remove(position);
                                notifyDataSetChanged();
                            }
                        })
                        .create();
                dialog.show();
                return true;
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.get(position).reverse();
                if (items.get(position).checked)
                    view.setBackgroundColor(Color.GRAY);
                else
                    view.setBackgroundColor(Color.WHITE);
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.valueOf(quantView.getText().toString());
                quantView.setText(val + 1 + "");
            }
        });
        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.valueOf(quantView.getText().toString());
                quantView.setText(val - 1 + "");
            }
        });

        quantView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int val = Integer.valueOf(s.toString());
                    int newVal = val;
                    if (val < 1) {
                        newVal = 1;
                    } else if (val > 999) {
                        newVal = 999;
                    }
                    if (val != newVal) {
                        s.clear();
                        s.append(String.valueOf(val));
                    }
                    items.get(position).amount = val;
                } catch (Exception ignored) {
                }
            }
        });

        Log.i(TAG, "getView: End");
        return convertView;
    }
}
