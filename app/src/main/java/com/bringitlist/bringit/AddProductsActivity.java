package com.bringitlist.bringit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bringitlist.bringit.Database.DBNames;
import com.bringitlist.bringit.Other.CategoriesAdapter;
import com.bringitlist.bringit.Other.IdAndChecked;
import com.bringitlist.bringit.Other.IdQuantChecked;
import com.bringitlist.bringit.Other.NavigationViewListener;
import com.bringitlist.bringit.Other.ProductsAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AddProductsActivity extends AppCompatActivity {

	private ListView listView;
	private EditText searchBar;
	private ProductsAdapter adapter;
	public IdAndChecked[] items;
	private App app;
	private String like = null;
	private ActionBarDrawerToggle drawerToggle;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_products);

		app = (App) getApplicationContext();
		listView = findViewById(R.id.add_products_grid_view);
		searchBar = findViewById(R.id.add_products_searchbar);

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

		searchBar.addTextChangedListener(new TextWatcher() {
			private Timer timer = new Timer();
			private final long delay = 1000;

			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override public void afterTextChanged(Editable s) {
				timer.cancel();
				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						like = searchBar.getText().toString();
						if (like.equals("")) like = null;
						runOnUiThread(new Runnable() {
							@Override public void run() {
								resetAdapter();
							}
						});
					}
				}, delay);
			}
		});

		DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

		drawerLayout.addDrawerListener(drawerToggle);
		drawerToggle.syncState();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(new NavigationViewListener(this));

		TextView drawerUserName = navigationView.getHeaderView(0).findViewById(R.id.nav_header_textView);
		drawerUserName.setText(app.userName);
	}

	@Override protected void onStart() {
		super.onStart();
		resetAdapter();
	}


	public void resetAdapter() {
		Integer[] where = null;
		if (adapter != null)
			where = adapter.where;

		adapter = new ProductsAdapter(this, where, like);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_add_product, menu);
		return true;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
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
							adapter = new ProductsAdapter(AddProductsActivity.this, null, like);
						} else {
							Integer[] idsArray = ids.toArray(new Integer[0]);
							adapter = new ProductsAdapter(AddProductsActivity.this, idsArray, like);
						}
						listView.setAdapter(adapter);
					}
				}).create().show();

				return true;
		}
		return true;
	}

	@Override public void onBackPressed() {
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
