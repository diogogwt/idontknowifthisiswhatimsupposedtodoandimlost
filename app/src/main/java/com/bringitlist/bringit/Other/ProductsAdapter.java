package com.bringitlist.bringit.Other;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bringitlist.bringit.App;
import com.bringitlist.bringit.Database.DBNames;
import com.bringitlist.bringit.NewEditProductActivity;
import com.bringitlist.bringit.R;

public class ProductsAdapter extends BaseAdapter {

	private static String TAG = "ProductsAdapter";

	private final String like;
	public Context context;
	private App app;
	private SQLiteDatabase db;
	public IdAndChecked[] ids;
	public Integer[] where;

	public ProductsAdapter(Context context, Integer[] where, String like) {
		this.context = context;
		this.app = (App) context.getApplicationContext();
		this.db = app.getReadableDB();
		this.where = where;
		this.like = like;

		updateFromDatabase();
	}


	private void updateFromDatabase() {
		Cursor cursor;
		String likeString = "";
		if (like != null) {
			likeString = "%" + like + "%";
		}

		if (where == null) {
			if (like == null)
				cursor = db.rawQuery("select id from products order by cat_id,name;", null);
			else
				cursor = db.rawQuery("select id from products where name like ? order by cat_id,name;", new String[]{likeString});
		} else {
			String whereString = TextUtils.join(",", where);
			if (like == null)
				cursor = db.rawQuery("select id from products where cat_id in (" + whereString + ") order by cat_id,name;", null);
			else
				cursor = db.rawQuery("select id from products where cat_id in (" + whereString + ") and name like ? order by cat_id,name;", new String[]{likeString});
		}

		ids = new IdAndChecked[cursor.getCount()];

		for (int i = 0; cursor.moveToNext(); i++) {
			ids[i] = new IdAndChecked();
			ids[i].id = cursor.getInt(0);
			ids[i].checked = false;
		}
		cursor.close();
	}

	@Override public void notifyDataSetChanged() {
		updateFromDatabase();
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return ids.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return ids[position].id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String id = String.valueOf(ids[position].id);
		final IdAndChecked elem = ids[position];

		Cursor cursor = db.query(DBNames.PRODUCTS, new String[]{"name", "image"}, "id=?", new String[]{id}, null, null, null);
		cursor.moveToFirst();

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.product_layout, parent, false);
		}

		TextView nameView = convertView.findViewById(R.id.product_name);
		final ImageView imageView = convertView.findViewById(R.id.product_image);
		CheckBox checkBox = convertView.findViewById(R.id.product_checkbox_right);
		checkBox.setChecked(elem.checked);

		nameView.setText(cursor.getString(0));

		final String fileName = cursor.getString(1);

		cursor.close();

		checkBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				elem.reverse();
			}
		});
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, NewEditProductActivity.class);
				intent.putExtra("prod_id", elem.id);
				context.startActivity(intent);
			}
		});
		App.loadImageToView(context, fileName, imageView);
		return convertView;
	}
}
