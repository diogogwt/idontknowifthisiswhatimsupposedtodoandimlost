package com.bringitlist.bringit;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.bringitlist.bringit.Database.DatabaseOpen;

//esta classe serve para poder ter objetos nas várias atividades
//é criado um objeto desta classe quando a aplicação é aberta
//para o pegarmos em qualquer atividade ou serviço faz-se:
//App app = (App) getApplication();
//App- nome da classe
//app- nome da variavel
public class App extends Application {

    public App() {
        if (isFirstOpening()) {
            //Fill database
            DatabaseOpen dbOpen = new DatabaseOpen(getApplicationContext());
            SQLiteDatabase db = dbOpen.getWritableDatabase();

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
