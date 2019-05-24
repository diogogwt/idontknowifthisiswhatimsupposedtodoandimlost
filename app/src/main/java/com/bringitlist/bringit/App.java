package com.bringitlist.bringit;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bringitlist.bringit.Database.DatabaseNames;
import com.bringitlist.bringit.Database.DatabaseOpen;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

//esta classe serve para poder ter objetos nas várias atividades
//é criado um objeto desta classe quando a aplicação é aberta
//para o pegarmos em qualquer atividade ou serviço faz-se:
//App app = (App) getApplication();
//App- nome da classe
//app- nome da variavel
public class App extends Application {

    public App() {
        if (isFirstOpening()) {
            try {
                //Fill database
                DatabaseOpen dbOpen = new DatabaseOpen(getApplicationContext());
                SQLiteDatabase db = dbOpen.getWritableDatabase();

                Context context = getApplicationContext();
                String line;

                BufferedReader br = new BufferedReader(new InputStreamReader(context.openFileInput("categories.txt")));
                while ((line = br.readLine()) != null) {

                    String[] splittedLine = line.split(";");
                    db.execSQL(DatabaseNames.INSERT_CATEGORY, splittedLine);
                }

                br = new BufferedReader(new InputStreamReader(context.openFileInput("products.txt")));
                for (int i = 0; (line = br.readLine()) != null; i++) {

                    String[] splittedLine = line.split(";");
                    String[] allValues = new String[splittedLine.length + 1];
                    allValues[0] = "" + i;
                    for (int j = 0; j < splittedLine.length; j++) {
                        allValues[j + 1] = splittedLine[j];
                    }
                    db.execSQL(DatabaseNames.INSERT_PRODUCT, allValues);
                }
            } catch (IOException e) {
                Log.e("BIG OPPSIE", "Filling Database: ", e);
            }
        }
    }


    public boolean isFirstOpening() {
        SharedPreferences pref = getSharedPreferences("random", MODE_PRIVATE);
        boolean firstTime = pref.getBoolean("firstTime", true);
        if (firstTime) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firstTime", false);
            editor.apply();
        }
        return firstTime;
    }
}
