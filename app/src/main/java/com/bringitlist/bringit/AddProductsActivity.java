package com.bringitlist.bringit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.bringitlist.bringit.Database.DBNames;
import com.bringitlist.bringit.Other.CategoriesAdapter;
import com.bringitlist.bringit.Other.IdAndChecked;
import com.bringitlist.bringit.Other.IdQuantChecked;
import com.bringitlist.bringit.Other.ProductsAdapter;

import java.util.ArrayList;
import java.util.Arrays;

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
        adapter = new ProductsAdapter(this, null);
        listView = findViewById(R.id.add_products_grid_view);
        listView.setAdapter(adapter);


        SQLiteDatabase db = app.getReadableDB();
        Cursor cursor = db.rawQuery("select id from " + DBNames.CATEGORIES + " order by name;", null);

        items = new IdAndChecked[cursor.getCount()];

        for (int i = 0; cursor.moveToNext(); i++) {
            items[i] = new IdAndChecked();
            items[i].id = cursor.getInt(0);
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

                //LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                //View popupView = inflater.inflate(R.layout.popup_cat, null);

                //ListView listViewPopup = popupView.findViewById(R.id.popup_cat_listview);
                CategoriesAdapter catAdapter = new CategoriesAdapter(this, items);
                //listViewPopup.setAdapter(catAdapter);

                Log.i("Cats", "Antes: " + Arrays.toString(items));

                AlertDialog dialog = new AlertDialog.Builder(this).setAdapter(catAdapter, null).setOnDismissListener(new DialogInterface.OnDismissListener() {
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

                        Log.i("Cats", "Depois: " + Arrays.toString(items));
                    }
                }).create();
                /*dialog.setContentView(popupView);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        ArrayList<Integer> ids = new ArrayList<>();
                        for (IdAndChecked idAndChecked : items)
                            if (idAndChecked.checked) ids.add(idAndChecked.id);

                        if (ids.size() == 0) {
                            adapter = new ProductsAdapter(AddProductsActivity.this, null);
                        } else {
                            Integer[] idsArray = ids.toArray(new Integer[0]);
                            adapter = new ProductsAdapter(AddProductsActivity.this, idsArray);
                        }
                        listView.setAdapter(adapter);
                    }
                });*/
                dialog.show();

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
