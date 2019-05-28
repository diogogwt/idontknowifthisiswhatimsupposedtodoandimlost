package com.bringitlist.bringit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private ListView listView;
    private BaseAdapter adapter;
    private App app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        app = (App) getApplication();
        listView = findViewById(R.id.history_list_view);

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        SQLiteDatabase db = app.getReadableDB();
        Cursor cursor = db.rawQuery("select products.name,date from history,products where products.id=history.prod_id;", null);
        while (cursor.moveToNext()) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", cursor.getString(0));
            hashMap.put("date", cursor.getString(1));
            arrayList.add(hashMap);
        }
        cursor.close();
        String[] from = {"name", "date"};
        int[] to = {R.id.list_history_text_view_name, R.id.list_history_text_view_date};

        adapter = new SimpleAdapter(this, arrayList, R.layout.list_history, from, to);
        listView.setAdapter(adapter);

        app.printSelect("select products.name,date from history,products where products.id=history.prod_id;", null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_history_total_anual:
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -1);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                String year = sdf.format(calendar.getTime());

                String query = "select sum(price) as total from history,products where products.id=history.prod_id and date>=Datetime('" + year + "-01-01 00:00:00');";

                String toastText = "Total Anual: ";
                Cursor cursor = app.getReadableDB().rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    toastText += cursor.getString(0) + "€";
                }
                cursor.close();
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return true;
    }
}