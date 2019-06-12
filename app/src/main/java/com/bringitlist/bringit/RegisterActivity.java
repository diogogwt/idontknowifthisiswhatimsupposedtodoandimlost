package com.bringitlist.bringit;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bringitlist.bringit.Database.DBNames;

public class RegisterActivity extends AppCompatActivity {

	private App app;
	private EditText usernameView;
	private EditText passwordView;
	private EditText confirmPasswordView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		app = (App) getApplication();

		usernameView = findViewById(R.id.register_username);
		passwordView = findViewById(R.id.register_password);
		confirmPasswordView = findViewById(R.id.register_confirm_password);
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_login, menu);
		return true;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_login_info) {
			Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.info_popup);
			dialog.show();
		}
		return true;
	}

	public void onClickRegisterR(View view) {

		String username = usernameView.getText().toString();
		String password = passwordView.getText().toString();
		String confirmPassword = confirmPasswordView.getText().toString();

		if (username.length() < 2) {
			Toast.makeText(this, getString(R.string.username_too_short), Toast.LENGTH_LONG).show();
			return;
		}
		if (password.length() < 6) {
			Toast.makeText(this, getString(R.string.password_too_short), Toast.LENGTH_LONG).show();
			return;
		}
		if (!password.equals(confirmPassword)) {
			Toast.makeText(this, getString(R.string.passwords_not_match), Toast.LENGTH_LONG).show();
			return;
		}

		SQLiteDatabase db = app.getReadableDB();

		Cursor cursor = db.rawQuery("select id from users where username = ?", new String[]{username});

		if (cursor.getCount() != 0) {
			Toast.makeText(this, getString(R.string.username_taken), Toast.LENGTH_LONG).show();
			cursor.close();
			return;
		}
		cursor.close();

		db = app.getWritableDB();
		db.execSQL(DBNames.INSERT_USER, new String[]{username, App.hash(password)});
		Toast.makeText(this, getString(R.string.registration_complete), Toast.LENGTH_LONG).show();


		cursor = db.rawQuery("select id from users where username = ?", new String[]{username});
		cursor.moveToFirst();
		app.loggedUser = cursor.getInt(0);
		cursor.close();

		SharedPreferences.Editor editor = getSharedPreferences("default", MODE_PRIVATE).edit();
		app.userName = username;
		editor.putString("lastUsername", username);
		editor.putInt("loggedUser", app.loggedUser);
		editor.putBoolean("autoLogin", true);
		editor.apply();

		Intent intent = new Intent(this, CartActivity.class);
		startActivity(intent);
		finish();
	}

}
