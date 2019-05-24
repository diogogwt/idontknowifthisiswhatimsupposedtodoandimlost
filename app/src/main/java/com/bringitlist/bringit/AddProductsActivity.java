package com.bringitlist.bringit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

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
}
