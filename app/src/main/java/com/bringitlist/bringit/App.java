package com.bringitlist.bringit;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.bringitlist.bringit.Database.DBNames;
import com.bringitlist.bringit.Database.DatabaseOpen;
import com.bringitlist.bringit.Other.IdAndChecked;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    public ArrayList<IdAndChecked> userItems = null;

    public App() {
    }

    public void DoOnFirstOpening() {
        if (isFirstOpening()) {
            try {
                DatabaseOpen dbOpen = new DatabaseOpen(getApplicationContext());
                SQLiteDatabase db = dbOpen.getWritableDatabase();

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
                    allValues[allValues.length - 1] = "prod_img/" + allValues[allValues.length - 1];

                    Log.i(TAG, Arrays.toString(allValues));

                    db.execSQL(DBNames.INSERT_PRODUCT, allValues);
                }
            } catch (IOException e) {
                Log.e("BIG OPPSIE", "Filling Database: ", e);
            }
        }
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


    public void SaveAll() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(openFileOutput("user_list", MODE_PRIVATE));
            oos.writeObject(userItems);
            oos.close();
        } catch (IOException e) {
            Log.e("Error", "Save user list: ", e);
        }
    }

    public void RetrieveAll() {
        try {
            ObjectInputStream oos = new ObjectInputStream(openFileInput("user_list"));
            userItems = (ArrayList<IdAndChecked>) oos.readObject();
            oos.close();
        } catch (Exception ex) {
            userItems = new ArrayList<>();
        }
    }

    public SQLiteDatabase getReadableDB() {
        return new DatabaseOpen(getApplicationContext()).getReadableDatabase();
    }


    public void printSelect(String query, String[] selectionArgs) {

        Cursor cursor = getReadableDB().rawQuery(query, selectionArgs);
        cursor.moveToFirst();
        StringBuilder strBuilder = new StringBuilder(".\n");

        String[] collumnNames = cursor.getColumnNames();

        for (int i = 0; i < collumnNames.length; i++)
            strBuilder.append(collumnNames[i]).append(",\t");

        strBuilder.append("\n");
        do {
            for (int i = 0; i < cursor.getColumnCount() - 1; i++)
                strBuilder.append(cursor.getString(i)).append(",\t");

            strBuilder.append(cursor.getString(cursor.getColumnCount() - 1));
            strBuilder.append("\n");
        } while (cursor.moveToNext());
        cursor.close();
        Log.i("Cursor Result", strBuilder.toString());
    }
}
