package com.bringitlist.bringit;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bringitlist.bringit.Database.DBNames;
import com.bringitlist.bringit.Database.DatabaseOpen;
import com.bringitlist.bringit.Other.IdQuantChecked;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;

//esta classe serve para poder ter objetos nas várias atividades
//é criado um objeto desta classe quando a aplicação é aberta
//para o pegarmos em qualquer atividade ou serviço faz-se:
//App app = (App) getApplication();
//App- nome da classe
//app- nome da variavel
public class App extends Application {

	private static String TAG = "App";
	public ArrayList<IdQuantChecked> userItems = null;
	private SQLiteDatabase readableDb = null;
	private SQLiteDatabase writableDb = null;
	public Integer loggedUser = null;

	public App() {
	}

	public void fillUserItems() {
		if (loggedUser == null) {
			Toast.makeText(getApplicationContext(), "What the heck are you doing?", Toast.LENGTH_LONG).show();
		}

		SQLiteDatabase db = getReadableDB();

		Cursor cursor = db.rawQuery("select prod_id,amount from " + DBNames.CARTS + " where user_id = ?;", new String[]{loggedUser.toString()});

		userItems = new ArrayList<>(cursor.getCount());

		while (cursor.moveToNext()) {
			IdQuantChecked temp = new IdQuantChecked();
			temp.id = cursor.getInt(0);
			temp.amount = cursor.getInt(1);
			temp.checked = false;

			userItems.add(temp);
		}
		cursor.close();
	}

	public void saveUserListToDatabase() {
		SQLiteDatabase db = getWritableDB();

		db.execSQL("delete from carts where user_id = ?", new Integer[]{loggedUser});

		Integer[] selectionArgs = new Integer[3];
		selectionArgs[0] = loggedUser;

		for (IdQuantChecked item : userItems) {
			selectionArgs[1] = item.id;
			selectionArgs[2] = item.amount;

			db.execSQL(DBNames.INSERT_CART_ITEM, selectionArgs);
		}
	}


	public SQLiteDatabase getReadableDB() {
		if (readableDb == null)
			readableDb = new DatabaseOpen(getApplicationContext()).getReadableDatabase();
		return readableDb;
	}

	public SQLiteDatabase getWritableDB() {
		if (writableDb == null)
			writableDb = new DatabaseOpen(getApplicationContext()).getWritableDatabase();
		return writableDb;
	}

	public void printSelect(String query, String[] selectionArgs) {

		Cursor cursor = this.getReadableDB().rawQuery(query, selectionArgs);
		StringBuilder strBuilder = new StringBuilder(".\n");

		String[] columnNames = cursor.getColumnNames();

		for (String columnName : columnNames)
			strBuilder.append(columnName).append(",\t");

		strBuilder.append("\n");

		while (cursor.moveToNext()) {
			for (int i = 0; i < cursor.getColumnCount() - 1; i++)
				strBuilder.append(cursor.getString(i)).append(",\t");

			strBuilder.append(cursor.getString(cursor.getColumnCount() - 1));
			strBuilder.append("\n");
		}
		cursor.close();
		Log.i("Cursor Result", strBuilder.toString());
	}


	public boolean isFirstOpening() {

		SharedPreferences pref = getApplicationContext().getSharedPreferences("random", MODE_PRIVATE);
		boolean firstTime = pref.getBoolean("firstTime", true);
		Log.i(TAG, "DoOnFirstOpening: Fill Database? " + firstTime);
		if (firstTime) {
			SharedPreferences.Editor editor = pref.edit();
			editor.putBoolean("firstTime", false);
			editor.apply();
		}
		return firstTime;
		//return true;
	}

	public void DoOnFirstOpening() {
		if (isFirstOpening()) {
			try {
				SQLiteDatabase db = this.getWritableDB();

				String line;
				BufferedReader br;

				br = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.categories)));
				while ((line = br.readLine()) != null) {

					String[] splittedLine = line.split(";");
					Log.d(TAG, Arrays.toString(splittedLine));
					db.execSQL(DBNames.INSERT_CATEGORY, splittedLine);
				}

				br = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.products)));
				for (int i = 0; (line = br.readLine()) != null; i++) {

					String[] splittedLine = line.split(";");
					String[] allValues = new String[splittedLine.length + 1];
					allValues[0] = "" + i;
					System.arraycopy(splittedLine, 0, allValues, 1, splittedLine.length);

					String internalFilePath = allValues[allValues.length - 1];
					try {
						BufferedOutputStream out = new BufferedOutputStream(openFileOutput(internalFilePath, MODE_PRIVATE));
						BufferedInputStream in = new BufferedInputStream(getAssets().open("prod_img/" + internalFilePath));
						byte[] buf = new byte[65533];
						int len;
						while ((len = in.read(buf)) > 0)
							out.write(buf, 0, len);
					} catch (Exception e) {
						Log.e(TAG, "DoOnFirstOpening: ", e);
					}

					db.execSQL(DBNames.INSERT_PRODUCT, allValues);
				}
			} catch (IOException e) {
				Log.e("BIG OPPSIE", "Filling Database: ", e);
			}
		}
	}


	public static String hash(String input) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(input.getBytes());
			return new String(messageDigest.digest());
		} catch (Exception e) {
			Log.e(TAG, "hash error: ", e);
			return null;
		}
	}

	public static void loadImageToView(Context context, String filename, ImageView imageView) {
		try {
			if (filename != null) {
				Bitmap bitmap = BitmapFactory.decodeStream(context.openFileInput(filename));
				imageView.setImageBitmap(bitmap);

			} else {
				imageView.setImageBitmap(null);
			}

		} catch (Exception e) {
			Log.e("BIG OPPSIE", "putting image on grid element: ", e);
		}
	}
}
