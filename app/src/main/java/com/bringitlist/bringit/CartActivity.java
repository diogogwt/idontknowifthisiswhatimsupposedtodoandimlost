package com.bringitlist.bringit;

import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.bringitlist.bringit.Database.DBNames;
import com.bringitlist.bringit.Other.IdQuantChecked;
import com.bringitlist.bringit.Other.NavigationViewListener;
import com.bringitlist.bringit.Other.UserListAdapter;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

	private App app;
	private ListView listView;
	private UserListAdapter listAdapter;
	private ActionBarDrawerToggle drawerToggle;

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

						db.execSQL(DBNames.INSERT_HISTORY, new Object[]{app.loggedUser, item.amount, item.id});
					}
				}
				listAdapter.notifyDataSetChanged();
			}
		});

		listAdapter = new UserListAdapter(this);

		listView = findViewById(R.id.main_grid_view);
		listView.setAdapter(listAdapter);

		DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

		drawerLayout.addDrawerListener(drawerToggle);
		drawerToggle.syncState();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(new NavigationViewListener(this));
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
		//app.printSelect("select * from history", null);
		//app.printSelect("select * from categories", null);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();
		app.saveUserListToDatabase();
	}
}
