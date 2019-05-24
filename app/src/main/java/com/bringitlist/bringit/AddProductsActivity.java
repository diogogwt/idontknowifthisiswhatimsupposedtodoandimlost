package com.bringitlist.bringit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.bringitlist.bringit.Other.CategoriesAdapter;
import com.bringitlist.bringit.Other.ProductsAdapter;

public class AddProductsActivity extends AppCompatActivity {

    private GridView gridView;
    private ProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

        adapter = new ProductsAdapter(this);

        gridView = findViewById(R.id.add_products_grid_view);
        gridView.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_prod_select_cat:

                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_cat, null);

                PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAtLocation(gridView, Gravity.CENTER, 0, 0);

                ListView listView = popupView.findViewById(R.id.popup_cat_listview);
                CategoriesAdapter adapter = new CategoriesAdapter(this);
                listView.setAdapter(adapter);

                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                });

                return true;
        }
        return true;
    }
}
