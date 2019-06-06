package com.bringitlist.bringit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.bringitlist.bringit.Database.DBNames;
import com.bringitlist.bringit.Other.CategoriesAdapter;
import com.bringitlist.bringit.Other.IdAndChecked;
import com.bringitlist.bringit.Other.IdQuantChecked;
import com.bringitlist.bringit.Other.ProductsAdapter;

import java.util.ArrayList;

public class AddProductsActivity extends AppCompatActivity {

    private ListView listView;
    private ProductsAdapter adapter;
    public IdAndChecked[] items;
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

        app = (App) getApplicationContext();
        listView = findViewById(R.id.add_products_grid_view);


        SQLiteDatabase db = app.getReadableDB();
        Cursor cursor = db.rawQuery("select id from " + DBNames.CATEGORIES + " order by name;", null);

        items = new IdAndChecked[cursor.getCount()];

        for (int i = 0; cursor.moveToNext(); i++) {
            items[i] = new IdAndChecked();
            items[i].id = cursor.getInt(0);
        }
        cursor.close();

        FloatingActionButton fab = findViewById(R.id.add_prod_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewEditProductActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO : fix the products not updating after changing product category

        Integer[] where = null;
        if (adapter != null)
            where = adapter.where;

        Log.i("test man", "onStart: " + where);
        adapter = new ProductsAdapter(this, where);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_prod_select_cat:
                CategoriesAdapter catAdapter = new CategoriesAdapter(this, items);

                new AlertDialog.Builder(this).setAdapter(catAdapter, null).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ArrayList<Integer> ids = new ArrayList<>();
                        for (IdAndChecked item : items) {
                            if (item.checked) ids.add(item.id);
                        }

                        if (ids.size() == 0) {
                            adapter = new ProductsAdapter(AddProductsActivity.this, null);
                        } else {
                            Integer[] idsArray = ids.toArray(new Integer[0]);
                            adapter = new ProductsAdapter(AddProductsActivity.this, idsArray);
                        }
                        listView.setAdapter(adapter);
                    }
                }).create().show();

                return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        for (IdAndChecked item : adapter.ids) {
            if (item.checked) {
                boolean toAdd = true;
                for (IdAndChecked i : app.userItems) {
                    if (i.id == item.id)
                        toAdd = false;
                }
                if (toAdd) {
                    IdQuantChecked temp = new IdQuantChecked();
                    temp.id = item.id;
                    app.userItems.add(temp);
                }
            }
        }
    }
}
