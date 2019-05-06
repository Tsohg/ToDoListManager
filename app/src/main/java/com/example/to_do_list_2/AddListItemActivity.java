package com.example.to_do_list_2;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddListItemActivity extends AppCompatActivity {

    private DBM dbm;
    private RecyclerView rv;
    private ArrayList<ToDoRecord> records;
    private static final String TAG = "AddListItemActivity";
    private static String dueDateText;
    private static Context contextHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_item);
        contextHandle = this.getApplicationContext();

        dbm = MainActivity.dbm;
        rv = MainActivity.mainActivityContextHandle.findViewById(R.id.recyclerView);
        records = MainActivity.records;
    }

    public void addToDoItem(View view)
    {
        //get input from 3 edit texts
        //EditText dueDateText = findViewById(R.id.dueDateButton);//dueDateEditText
        EditText nameText = findViewById(R.id.nameEditText);
        EditText descText = findViewById(R.id.descEditText);

        if(dueDateIsFormatted(dueDateText)) {
            if(!nameText.getText().toString().equals("")) {
                //new To Do Record
                ToDoRecord record = new ToDoRecord(dueDateText,
                        nameText.getText().toString(),
                        descText.getText().toString());

                //add to database
                dbm.insertToDoItem(record);

                //get new sorted records
                records = dbm.getSortedList();

                //rebuild view
                rebuildRecyclerView();

                Toast.makeText(this, "Successfully added to the list.", Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                Toast.makeText(this, "You are required to give the to do item a name.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Due date is not in correct format. " +
                            "Example for January 3rd: " +
                            "01-03", Toast.LENGTH_SHORT).show();
        }
    }

    //pretty much deprecated. helps to ensure format is correct.
    private boolean dueDateIsFormatted(String date)
    {
        Pattern p = Pattern.compile("\\d\\d[-]\\d\\d");
        Matcher m = p.matcher(date);

        if(date.length() > 5)
            return false;

        if(date.length() < 5)
            return false;

        if(!m.matches()) //if it doesn't match the pattern, return false
            return false;
        else
        {
            String[] tokens = date.split("-");

            try
            {
                int month = Integer.parseInt(tokens[0]);
                int day = Integer.parseInt(tokens[1]);

                if(month > 12 || month < 1)
                    return false;
                else if(day > 31 || day < 1)
                    return false;
                else
                    return true;

            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.d(TAG, "dueDateIsFormatted: Integer Parsing Error.");
            }
        }

        return false;
    }

    private void rebuildRecyclerView()
    {
        rv.removeAllViews();
        rv.setAdapter(new DB_Item_Adapter(records, dbm));
    }


    public void showDatePicker(View view)
    {
        DialogFragment newFragment = new MyDatePicker();
        newFragment.show(this.getSupportFragmentManager(),
                "datePicker");
    }

    //from: https://developer.android.com/guide/topics/ui/controls/pickers.html
    //edited for my purposes.
    public static class MyDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener
    {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        //format the datepicker into something more acceptable.
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
        {
            String result = "";

            if(month < 10)
            {
                result += "0";
                result += month + 1; //+1 because it starts counting at 0 for Jan.
            }
            else
                result += month + 1;

            result += "-";

            if(dayOfMonth < 10)
            {
                result += "0";
                result += dayOfMonth;
            }
            else
                result += dayOfMonth;

            AddListItemActivity.dueDateText = result;
            Toast.makeText(AddListItemActivity.contextHandle, "Date successfully set to: " + result, Toast.LENGTH_SHORT).show();
        }
    }

}
