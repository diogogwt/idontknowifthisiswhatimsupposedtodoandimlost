package com.bringitlist.bringit;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;

import com.bringitlist.bringit.Database.DBNames;
import com.bringitlist.bringit.Database.DatabaseOpen;
import com.bringitlist.bringit.Other.CategoriesAdapter;
import com.bringitlist.bringit.Other.IdAndChecked;
import com.bringitlist.bringit.Other.ProductsAdapter;

import java.util.ArrayList;

public class AddProductsActivity extends AppCompatActivity {

    private GridView gridView;
    private ProductsAdapter adapter;
    public IdAndChecked[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

        adapter = new ProductsAdapter(this, null);

        gridView = findViewById(R.id.add_products_grid_view);
        gridView.setAdapter(adapter);


        SQLiteDatabase db = new DatabaseOpen(this).getReadableDatabase();
        Cursor cursor = db.rawQuery("select id from " + DBNames.CATEGORIES + " order by name;", null);

        items = new IdAndChecked[cursor.getCount()];
        for (int i = 0; i < items.length; i++)
            items[i] = new IdAndChecked();

        cursor.moveToFirst();
        items[0].id = cursor.getInt(0);
        for (int i = 1; cursor.moveToNext(); i++) {
            Log.i("CatAdapter", "CategoriesAdapter: " + cursor.getInt(0));
            items[i].id = cursor.getInt(0);
            items[i].checked = false;
        }

        cursor.close();
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

                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_cat, null);

                ListView listView = popupView.findViewById(R.id.popup_cat_listview);
                final CategoriesAdapter catAdapter = new CategoriesAdapter(this, items);
                listView.setAdapter(catAdapter);

                Dialog dialog = new Dialog(this);
                dialog.setContentView(popupView);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        ArrayList<Integer> ids = new ArrayList<>();
                        for (IdAndChecked idAndChecked : items)
                            if (idAndChecked.checked) ids.add(idAndChecked.id);

                        if (ids.size() == 0) {
                            adapter = new ProductsAdapter(AddProductsActivity.this, null);
                            gridView.setAdapter(adapter);
                        } else {
                            Integer[] idsArray = ids.toArray(new Integer[0]);
                            adapter = new ProductsAdapter(AddProductsActivity.this, idsArray);
                            gridView.setAdapter(adapter);
                        }
                    }
                });
                dialog.show();

                return true;
        }
        return true;
    }
}
