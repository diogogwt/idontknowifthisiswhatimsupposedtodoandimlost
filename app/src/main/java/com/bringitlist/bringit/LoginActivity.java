package com.bringitlist.bringit;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

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

        Cursor cursor = db.rawQuery("select id,password from users where username = ?", new String[]{username});

        if (!cursor.moveToFirst()) {
            Toast.makeText(this, getString(R.string.incorrect_username), Toast.LENGTH_LONG).show();
        } else {
            String hashedPassword = cursor.getString(1);
            if (hashedPassword.equals(hash(password))) {

                app.loggedUser = cursor.getInt(0);
                cursor.close();
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
                return;
            }
        }
        cursor.close();
    }

    public void onClickRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }


    public static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown"
                    + " for incorrect algorithm: " + e);

            return null;
        }
    }

}
