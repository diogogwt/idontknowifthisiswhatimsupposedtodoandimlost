package com.bringitlist.bringit;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bringitlist.bringit.Database.DBNames;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class NewEditProductActivity extends AppCompatActivity {

	public static String TAG = "NewEditProductActivity";

	private App app;
	private Integer prod_id;
	private Integer cat_id;
	private String imageName;
	private boolean newProd = false;
	private Spinner spinner;
	private EditText nameView;
	private EditText priceView;
	private ImageView imageView;

	static final int REQUEST_IMAGE_CAPTURE = 1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_edit_product);

		app = (App) getApplication();
		final SQLiteDatabase db = app.getReadableDB();

		Intent intent = getIntent();
		prod_id = intent.getIntExtra("prod_id", -1);

		spinner = findViewById(R.id.new_edit_product_spinner);
		nameView = findViewById(R.id.new_edit_product_name);
		priceView = findViewById(R.id.new_edit_product_price);
		imageView = findViewById(R.id.new_edit_product_image);

		if (prod_id != -1) {
			newProd = false;
			Cursor cursor = db.rawQuery("select cat_id,name,price,image from products where id=?", new String[]{prod_id.toString()});
			cursor.moveToFirst();
			cat_id = cursor.getInt(0);
			nameView.setText(cursor.getString(1));
			priceView.setText(cursor.getString(2));
			imageName = cursor.getString(3);
			App.loadImageToView(this, imageName, imageView);
			cursor.close();
		} else {
			newProd = true;
			Cursor cursor = db.rawQuery("select max(id)+1 as id from products;", null);
			cursor.moveToFirst();
			cat_id = 0;
			prod_id = cursor.getInt(0);
			cursor.close();
		}

		fillCatSpinner();
	}

	public void pickFromCamera(View view) {
		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
					&& checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
					&& checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

				requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 11);
				return;
			}
		}
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == 11) {
			if (grantResults.length > 1 &&
					grantResults[0] == PackageManager.PERMISSION_GRANTED &&
					grantResults[1] == PackageManager.PERMISSION_GRANTED &&
					grantResults[2] == PackageManager.PERMISSION_GRANTED) {
				pickFromCamera(null);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			try {
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
				imageName = prod_id + ".jpg";

				FileOutputStream outputStream = openFileOutput(imageName, MODE_PRIVATE);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
				outputStream.close();

				imageView.setImageBitmap(bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void onClickAddCategory(View view) {

		final EditText editText = new EditText(this);

		new AlertDialog.Builder(this)
				.setView(editText)
				.setNegativeButton("Cancel", null)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SQLiteDatabase db = app.getWritableDB();
						String[] args = new String[]{editText.getText().toString()};
						db.execSQL("insert into categories(name) values(?)", args);
						fillCatSpinner();
						dialog.dismiss();
					}
				}).create().show();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.new_prod_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.new_prod_menu_delete: {

				new AlertDialog.Builder(this)
						.setTitle(getString(R.string.are_you_sure_you_want_to_remove_this_item))
						.setNegativeButton(getString(R.string.cancel), null)
						.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

								if (imageName != null) {
									File file = new File(getFilesDir(), imageName);
									if (file.exists())
										file.delete();
								}
								if (!newProd) {
									app.getWritableDB().execSQL("delete from products where id=?", new Integer[]{prod_id});
								}
								finish();
							}
						})
						.create()
						.show();

				break;
			}
			case R.id.new_prod_menu_save: {

				String name = nameView.getText().toString();
				String price = priceView.getText().toString();

				if (name.equals("")) {
					Toast.makeText(this, getString(R.string.insert_prod_name), Toast.LENGTH_LONG).show();
					return true;
				}
				if (price.equals("")) {
					priceView.setText("0");
					price = "0";
				}

				SQLiteDatabase db = app.getWritableDB();

				if (newProd) {
					String[] args = new String[]{prod_id.toString(), cat_id.toString(), name, price, imageName};
					db.execSQL(DBNames.INSERT_PRODUCT, args);
					finish();
				} else {
					String[] args = new String[]{cat_id.toString(), name, price, imageName, prod_id.toString()};
					db.execSQL("update products set cat_id=?,name=?,price=?,image=? where id=?", args);
					finish();
				}
				break;
			}
		}
		return true;
	}

	public void fillCatSpinner() {
		Cursor cursor = app.getReadableDB().rawQuery("select id,name from categories order by id", null);

		final IdName[] cats = new IdName[cursor.getCount()];
		String[] array = new String[cursor.getCount()];
		int spinnerSelection = 0;
		for (int i = 0; cursor.moveToNext(); i++) {
			array[i] = cursor.getString(1);

			IdName temp = new IdName();
			cats[i] = temp;
			temp.id = cursor.getInt(0);
			temp.name = array[i];

			if (cat_id == temp.id) {
				spinnerSelection = i;
			}
		}
		cursor.close();

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, array);

		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(spinnerSelection);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				cat_id = cats[position].id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	class IdName {
		int id = -1;
		String name = null;
	}
}
