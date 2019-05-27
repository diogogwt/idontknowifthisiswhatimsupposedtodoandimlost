package com.bringitlist.bringit;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.bringitlist.bringit.Database.DBNames;
import com.bringitlist.bringit.Database.DatabaseOpen;
import com.bringitlist.bringit.Other.IdAndChecked;
import com.bringitlist.bringit.Other.UserListAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private App app;
    private GridView gridView;
    private UserListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        app = (App) getApplication();
        app.DoOnFirstOpening();
        app.RetrieveAll();


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
                SQLiteDatabase db = new DatabaseOpen(MainActivity.this).getWritableDatabase();
                ArrayList<IdAndChecked> temp = (ArrayList<IdAndChecked>) app.userItems.clone();

                for (IdAndChecked item : temp) {
                    if (item.checked) {
                        app.userItems.remove(item);

                        db.execSQL(DBNames.INSERT_HISTORY, new Integer[]{item.id});
                    }
                }
                listAdapter.notifyDataSetChanged();
            }
        });

        listAdapter = new UserListAdapter(this);

        gridView = findViewById(R.id.main_grid_view);
        gridView.setAdapter(listAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        listAdapter.notifyDataSetChanged();

        app.printSelect("select * from history", null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        app.SaveAll();
    }
}
