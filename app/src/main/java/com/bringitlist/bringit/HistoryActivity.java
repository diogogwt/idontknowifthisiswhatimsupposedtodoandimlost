package com.bringitlist.bringit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

        String[] args = new String[]{String.valueOf(app.loggedUser)};
        Cursor cursor = db.rawQuery("select prod_name,date,amount,(amount*prod_price) as price from history where history.user_id=?;", args);
        while (cursor.moveToNext()) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", cursor.getString(0));
            hashMap.put("date", cursor.getString(1));
            hashMap.put("amount", cursor.getString(2));
            hashMap.put("price", cursor.getString(3) + "€");
            arrayList.add(hashMap);
        }
        cursor.close();
        String[] from = {"name", "date", "amount", "price"};
        int[] to = {R.id.list_history_text_view_name, R.id.list_history_text_view_date, R.id.list_history_text_view_amount, R.id.list_history_text_view_price};

        adapter = new SimpleAdapter(this, arrayList, R.layout.list_history, from, to);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_history_total_semanal: {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                calendar.set(Calendar.DAY_OF_WEEK, 1);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(calendar.getTime());
                Log.i("test", "onOptionsItemSelected: " + date);
                String query = "select sum(prod_price*amount) as total from history where date>=Datetime('" + date + " 00:00:00');";

                String toastText = getString(R.string.this_week_total) + " : ";
                Cursor cursor = app.getReadableDB().rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    toastText += cursor.getString(0);
                }
                cursor.close();
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.menu_history_total_anual: {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -1);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                String year = sdf.format(calendar.getTime());

                String query = "select sum(prod_price*amount) as total from history where date>=Datetime('" + year + "-01-01 00:00:00');";

                String toastText = getString(R.string.this_years_total) + " : ";
                Cursor cursor = app.getReadableDB().rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    toastText += cursor.getString(0);
                }
                cursor.close();
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
                break;
            }
            default:
                break;
        }
        return true;
    }
}