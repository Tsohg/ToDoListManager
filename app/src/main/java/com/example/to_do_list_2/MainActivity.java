package com.example.to_do_list_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityContextHandle = this;
        mainActivityView = this.getCurrentFocus();

        Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.inflateMenu(R.menu.main_menu); //doesn't work for menu inflation
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

//        ArrayList<ToDoRecord> list = dbm.getAllRecords();
//        DBM.printList(list);
//        Log.d(TAG, "printList Checker:____________________________________");
//        ArrayList<ToDoRecord> sorted = dbm.getSortedList();
//        DBM.printList(sorted);
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
//            case R.id.sortMenuItem:
//                return handleSortItem();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //When sort button is pressed.
//    private boolean handleSortItem() {
//        return false;
//    }

    //When add item button is pressed:
    //Step 1: start new intent
    //Step 2: get user input for due date, name, description
    //Step 3: insert that info into the database
    //Step 4: method call to rebuild recycler view from database.
    private boolean handleAddItem()
    {
        Intent i = new Intent(getApplicationContext(), AddListItemActivity.class);
        startActivity(i);
        return true;
    }

    private void fillRecyclerView()
    {
        //from recycler view example from class. edited for my purposes.
        RecyclerView rv = findViewById(R.id.recyclerView);
        DB_Item_Adapter adapter = new DB_Item_Adapter(records, dbm);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext());
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
        rv.setItemAnimator(new DefaultItemAnimator());
    }

    private void purgeRecyclerView()
    {
        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.removeAllViews();
        fillRecyclerView();
    }
}
