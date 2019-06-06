package com.bringitlist.bringit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.bringitlist.bringit.Database.DBNames;

public class NewEditProductActivity extends AppCompatActivity {

    private App app;
    private int prod_id;
    private boolean newProd = false;
    private Spinner spinner;
    private EditText name;
    private EditText price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_edit_product);

        app = (App) getApplication();
        SQLiteDatabase db = app.getReadableDB();

        Intent intent = getIntent();
        prod_id = intent.getIntExtra("prod_id", -1);

        spinner = findViewById(R.id.new_edit_product_spinner);
        name = findViewById(R.id.new_edit_product_name);
        price = findViewById(R.id.new_edit_product_price);

        if (prod_id != -1) {
            newProd = false;
            Cursor cursor = db.rawQuery("select cat_id,name,price,image from products where id=?", new String[]{String.valueOf(prod_id)});
            cursor.moveToFirst();

            name.setText(cursor.getString(1));
            price.setText(cursor.getString(2));

            cursor.close();
        } else {
            newProd = true;
            Cursor cursor = db.rawQuery("select max(id)+1 as id from products;", null);
            cursor.moveToFirst();
            prod_id = cursor.getInt(0);
            cursor.close();
        }


        Cursor cursor = db.rawQuery("select name from categories order by id", null);

        String[] array = new String[cursor.getCount()];

        for (int i = 0; cursor.moveToNext(); i++) {
            array[i] = cursor.getString(0);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array);

        spinner.setAdapter(arrayAdapter);
    }

    public void onClickAddCategory(View view) {

        final EditText editText = new EditText(this);

        new AlertDialog.Builder(this)
                .setView(editText)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = app.getWritableDB();

                        String[] args = new String[]{editText.getText().toString()};

                        db.execSQL("insert into categories(name) values(?)", args);

                        dialog.dismiss();
                    }
                }).create().show();
    }

    @Override
    protected void onStop() {
        super.onStop();

        SQLiteDatabase db = app.getWritableDB();

        if (newProd) {
            String[] args = new String[]{String.valueOf(prod_id), null, name.getText().toString(), price.getText().toString(), null};

            db.execSQL(DBNames.INSERT_PRODUCT, args);
        } else {
            String[] args = new String[]{null, name.getText().toString(), price.getText().toString(), null, String.valueOf(prod_id)};

            db.execSQL("update products set cat_id=?,name=?,price=?,image=? where id=?", args);
        }
    }
}
