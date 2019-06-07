package com.bringitlist.bringit;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.bringitlist.bringit.Database.DBNames;
import com.bringitlist.bringit.Other.IdQuantChecked;
import com.bringitlist.bringit.Other.UserListAdapter;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private App app;
    private ListView listView;
    private UserListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        app = (App) getApplication();
        app.fillUserItems();

        FloatingActionButton fab = findViewById(R.id.add_prod_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddProductsActivity.class);
                startActivity(intent);
            }
        });
        fab = findViewById(R.id.cart_prod_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = app.getWritableDB();
                ArrayList<IdQuantChecked> temp = (ArrayList<IdQuantChecked>) app.userItems.clone();

                for (IdQuantChecked item : temp) {
                    if (item.checked) {
                        app.userItems.remove(item);

                        db.execSQL(DBNames.INSERT_HISTORY, new Integer[]{app.loggedUser, item.amount, item.id});
                    }
                }
                listAdapter.notifyDataSetChanged();
            }
        });

        listAdapter = new UserListAdapter(this);

        listView = findViewById(R.id.main_grid_view);
        listView.setAdapter(listAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        for (IdQuantChecked item : listAdapter.items) {
            item.checked = false;
        }
        listAdapter = new UserListAdapter(this);
        listView.setAdapter(listAdapter);
        //app.printSelect("select * from users", null);
        //app.printSelect("select * from carts", null);
        //app.printSelect("select * from products", null);
        app.printSelect("select * from history", null);
        //app.printSelect("select * from categories", null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.history: {
                Intent intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.logout: {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        app.saveUserListToDatabase();
    }
}
