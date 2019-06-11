package com.bringitlist.bringit.Other;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bringitlist.bringit.App;
import com.bringitlist.bringit.Database.DBNames;
import com.bringitlist.bringit.Database.DatabaseOpen;
import com.bringitlist.bringit.NewEditProductActivity;
import com.bringitlist.bringit.R;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter {

	public static String TAG = "UserListAdapter";
	public Context context;
	private SQLiteDatabase db;
	public ArrayList<IdQuantChecked> items;

	public UserListAdapter(Context context) {
		this.context = context;
		this.items = ((App) context.getApplicationContext()).userItems;
		this.db = new DatabaseOpen(context).getReadableDatabase();
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).id;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		String[] selectionArgs = new String[]{String.valueOf(getItemId(position))};
		Cursor cursor = db.query(DBNames.PRODUCTS, new String[]{"name", "image"}, "id=?", selectionArgs, null, null, null);
		if (!cursor.moveToFirst()) {
			return null;
		}

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.new_user_layout, parent, false);
		}

		TextView nameView = convertView.findViewById(R.id.user_product_name);
		ImageView imageView = convertView.findViewById(R.id.user_product_image);
		ImageButton plusBtn = convertView.findViewById(R.id.user_product_plus_btn);
		ImageButton minusBtn = convertView.findViewById(R.id.user_product_minus_btn);
		CheckBox checkBox = convertView.findViewById(R.id.user_product_checkbox);
		final TextView quantView = convertView.findViewById(R.id.user_product_amount);

		quantView.setText(String.valueOf(items.get(position).amount));
		nameView.setText(cursor.getString(0));

		String fileName = cursor.getString(1);
		App.loadImageToView(context, fileName, imageView);

		cursor.close();

		/**---------------------------------Listeners------------------------------------*/

		convertView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				AlertDialog dialog = new AlertDialog.Builder(context)
						.setTitle(context.getString(R.string.remove_from_cart))
						.setNegativeButton(context.getString(R.string.no), null)
						.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								items.remove(position);
								notifyDataSetChanged();
							}
						})
						.create();
				dialog.show();
				return true;
			}
		});
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, NewEditProductActivity.class);
				intent.putExtra("prod_id", items.get(position).id);
				context.startActivity(intent);
			}
		});
		checkBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				items.get(position).reverse();
				CheckBox temp = (CheckBox) v;
				temp.setChecked(temp.isChecked());
			}
		});

		plusBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				double val = Double.valueOf(quantView.getText().toString());
				quantView.setText(val + 1 + "");
			}
		});
		minusBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				double val = Double.valueOf(quantView.getText().toString());
				quantView.setText(val - 1 + "");
			}
		});

		quantView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				try {
					double val = Double.valueOf(s.toString());
					double newVal = val;
					if (val < 1) {
						newVal = 1;
					} else if (val > 999) {
						newVal = 999;
					}
					if (val != newVal) {
						s.clear();
						s.append(String.valueOf(newVal));
					}
					items.get(position).amount = val;
				} catch (Exception ignored) {
					s.clear();
					s.append("1");
					items.get(position).amount = 1;
				}
			}
		});
		return convertView;
	}
}
