package com.bringitlist.bringit.Other;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;
import android.widget.Toast;

import com.bringitlist.bringit.App;
import com.bringitlist.bringit.HistoryActivity;
import com.bringitlist.bringit.LoginActivity;
import com.bringitlist.bringit.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NavigationViewListener implements NavigationView.OnNavigationItemSelectedListener {

	private Context context;
	private App app;

	public NavigationViewListener(Context context) {
		this.context = context;
		this.app = (App) context.getApplicationContext();
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.nav_item_info: {
				Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.info_popup);
				dialog.show();
				break;
			}
			case R.id.nav_item_history: {
				context.startActivity(new Intent(context, HistoryActivity.class));
				break;
			}
			case R.id.nav_item_week_total: {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.WEEK_OF_YEAR, -1);
				calendar.set(Calendar.DAY_OF_WEEK, 1);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String date = sdf.format(calendar.getTime());

				String query = "select sum(prod_price*amount) as total from history where date>=Datetime('" + date + " 00:00:00');";

				String toastText = context.getString(R.string.this_week_total) + " : ";
				Cursor cursor = app.getReadableDB().rawQuery(query, null);
				if (cursor.moveToFirst()) {
					toastText += cursor.getString(0);
				}
				cursor.close();
				Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
				break;
			}
			case R.id.nav_item_year_total: {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.YEAR, -1);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
				String year = sdf.format(calendar.getTime());

				String query = "select sum(prod_price*amount) as total from history where date>=Datetime('" + year + "-01-01 00:00:00');";

				String toastText = context.getString(R.string.this_years_total) + " : ";
				Cursor cursor = app.getReadableDB().rawQuery(query, null);
				if (cursor.moveToFirst()) {
					toastText += cursor.getString(0);
				}
				cursor.close();
				Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
				break;
			}
			case R.id.nav_item_logout: {
				SharedPreferences.Editor editor = context.getSharedPreferences("default", Context.MODE_PRIVATE).edit();
				editor.putBoolean("autoLogin", false);
				editor.apply();
				Intent intent = new Intent(context, LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				context.startActivity(intent);
				((Activity) context).finish();
				break;
			}
		}
		return true;

	}
}
