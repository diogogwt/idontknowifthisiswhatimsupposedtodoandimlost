package com.bringitlist.bringit;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bringitlist.bringit.Other.NavigationViewListener;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

	public static String TAG = "LoginActivity";

	private App app;
	private EditText usernameView;
	private EditText passwordView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		app = (App) getApplication();
		app.DoOnFirstOpening();

		usernameView = findViewById(R.id.login_username);
		passwordView = findViewById(R.id.login_password);

		SharedPreferences preferences = getSharedPreferences("default", MODE_PRIVATE);

		Boolean autoLogin = preferences.getBoolean("autoLogin", false);
		String lastUsername = preferences.getString("lastUsername", "");
		if (autoLogin) {
			app.userName = lastUsername;
			app.loggedUser = preferences.getInt("loggedUser", -1);
			Intent intent = new Intent(this, CartActivity.class);
			startActivity(intent);
			finish();
		}
		usernameView.setText(lastUsername);

	}

	public void onClickLogin(View view) {

		SQLiteDatabase db = app.getReadableDB();
		String username = usernameView.getText().toString();
		String password = passwordView.getText().toString();

		if (username.equals("")) {
			Toast.makeText(this, getString(R.string.fill_username), Toast.LENGTH_LONG).show();
		} else if (password.equals("")) {
			Toast.makeText(this, getString(R.string.password_constraints), Toast.LENGTH_LONG).show();
		}

		Cursor cursor = db.rawQuery("select id,password_hash from users where username = ?", new String[]{username});

		if (!cursor.moveToFirst()) {
			Toast.makeText(this, getString(R.string.incorrect_username), Toast.LENGTH_LONG).show();
		} else {
			String hashedPassword = cursor.getString(1);
			if (hashedPassword.equals(App.hash(password))) {

				app.loggedUser = cursor.getInt(0);
				cursor.close();
				Intent intent = new Intent(this, CartActivity.class);
				startActivity(intent);

				SharedPreferences.Editor editor = getSharedPreferences("default", MODE_PRIVATE).edit();
				editor.putString("lastUsername", username);
				editor.putInt("loggedUser", app.loggedUser);
				editor.putBoolean("autoLogin", true);
				editor.apply();
				finish();
				return;
			}
			Toast.makeText(this, getString(R.string.incorrect_password), Toast.LENGTH_LONG).show();
		}
		cursor.close();
	}

	public void onClickRegister(View view) {
		startActivity(new Intent(this, RegisterActivity.class));
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
}
