package com.bringitlist.bringit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bringitlist.bringit.Other.NavigationViewListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryActivity extends AppCompatActivity {

	private ListView listView;
	private BaseAdapter adapter;
	private App app;
	private ActionBarDrawerToggle drawerToggle;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		app = (App) getApplication();
		listView = findViewById(R.id.history_list_view);

		ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
		SQLiteDatabase db = app.getReadableDB();

		String[] args = new String[]{String.valueOf(app.loggedUser)};
		Cursor cursor = db.rawQuery("select prod_name,date,amount,(amount*prod_price) as price from history where history.user_id=?;", args);
		while (cursor.moveToNext()) {
			HashMap<String, String> hashMap = new HashMap<>();
			hashMap.put("name", cursor.getString(0));
			hashMap.put("date", cursor.getString(1));
			hashMap.put("amount", getString(R.string.amount) + " : " + cursor.getString(2));
			hashMap.put("price", getString(R.string.price) + " : " + cursor.getString(3) + "€");
			arrayList.add(hashMap);
		}
		cursor.close();
		String[] from = {"name", "date", "amount", "price"};
		int[] to = {R.id.list_history_text_view_name, R.id.list_history_text_view_date, R.id.list_history_text_view_amount, R.id.list_history_text_view_price};

		adapter = new SimpleAdapter(this, arrayList, R.layout.list_history, from, to);
		listView.setAdapter(adapter);


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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return true;
	}
}