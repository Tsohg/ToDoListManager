package com.example.to_do_list_2;


import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;


/*
Important Note:
If code snippets have been found online with no source, chances are I simply missed citing source.
I will do my best to give credit where credit is due in this case.
For example, comments above methods I find online and edit for the purposes of my app will
explicitly state that I found them at a URL and edited it for my app.
I do not claim anything I missed the citing on as my own. If I missed a cite somewhere, let me know
and I will give credit where credit is due.
 */

public class MainActivity extends AppCompatActivity {

    public static DBM dbm;
    private static final String TAG = "MainActivity";
    public static ArrayList<ToDoRecord> records;
    public static AppCompatActivity mainActivityContextHandle;
    public static View mainActivityView;
    private SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityContextHandle = this;
        mainActivityView = this.getCurrentFocus();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //hard code some items
        dbm = new DBM(this.getApplicationContext());
//        dbm.purgeDatabase(); //be careful with this ^.^
//        dbm.insertToDoItem(new ToDoRecord("08-01", "a", "desc"));
//        dbm.insertToDoItem(new ToDoRecord("08-04", "c", "desc"));
//        dbm.insertToDoItem(new ToDoRecord("08-01", "aa", "desc"));
//        dbm.insertToDoItem(new ToDoRecord("08-03", "b", "desc"));
//        dbm.insertToDoItem(new ToDoRecord("08-20", "e", "desc"));
//        dbm.insertToDoItem(new ToDoRecord("08-19", "d", "desc"));
//        dbm.insertToDoItem(new ToDoRecord("08-30", "f", "desc"));
        //records = dbm.getAllRecords();
        records = dbm.getSortedList();
        //dbm.printTableCols();

        Log.d(TAG, "onCreate: List printing...");
        DBM.printList(records);

        fillRecyclerView();
    }

    //For menu inflation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.addMenuItem:
                return handleAddItem();
            case R.id.updateDaysRemainingMenuItem:
                return handleUpdateDaysLeft();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean handleAddItem()
    {
        Intent i = new Intent(getApplicationContext(), AddListItemActivity.class);
        startActivity(i);
        handleUpdateDaysLeft(); //update views.
        return true;
    }

    private boolean handleUpdateDaysLeft()
    {
        RecyclerView rv = findViewById(R.id.recyclerView);
        TextView daysLeftView;
        String dueDate;
        Date d = new Date();
        Date date1;
        Date date2; //due date
        int days;

        if(rv == null || rv.getChildCount() == 0)
        {
            Log.d(TAG, "handleUpdateDaysLeft: Null or zero sized recycler view.");
            return true;
        }

        for(int i = 0; i < rv.getChildCount(); i++)
        {
            daysLeftView = rv.getChildAt(i).findViewById(R.id.daysLeftView);
            dueDate = ((TextView) rv.getChildAt(i).findViewById(R.id.dueDateView)).getText().toString();

            try
            {
                date1 = formatter.parse(formatter.format(d));
                date2 = formatter.parse(dueDate);
                days = (int)( (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24) ); //TODO: Fix this.
                Log.d(TAG, "handleUpdateDaysLeft: date1: "+ date1.getTime() / (1000 * 60 * 60 * 24));
                Log.d(TAG, "handleUpdateDaysLeft: date2: "+ date2.getTime() / (1000 * 60 * 60 * 24));
                daysLeftView.setText("Days Left: " + days);

                if(days < 0)
                {
//                    ((TextView) rv.getChildAt(i).findViewById(R.id.dueDateView)).setTextColor(Color.RED);
//                    ((TextView) rv.getChildAt(i).findViewById(R.id.nameView)).setTextColor(Color.RED);
                    ((TextView) rv.getChildAt(i).findViewById(R.id.daysLeftView)).setTextColor(Color.RED);
//                    ((TextView) rv.getChildAt(i).findViewById(R.id.optionDotsView)).setTextColor(Color.RED);
                }
            }
            catch (ParseException e)
            {
                Log.d(TAG, "handleUpdateDaysLeft: Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return true;
    }

    private void fillRecyclerView()
    {
        RecyclerView rv = findViewById(R.id.recyclerView);
        DB_Item_Adapter adapter = new DB_Item_Adapter(records, dbm);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext());
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
        rv.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration div = new DividerItemDecoration(rv.getContext(), ((LinearLayoutManager) layoutManager).getOrientation());
        div.setDrawable(getDrawable(R.drawable.line));
        rv.addItemDecoration(div);
    }

    private void purgeRecyclerView()
    {
        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.removeAllViews();
        fillRecyclerView();
    }

}
