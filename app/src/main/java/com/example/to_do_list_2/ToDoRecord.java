package com.example.to_do_list_2;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ToDoRecord
{
    private String dueDate;
    private String name;
    private String description;
    private SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
    private static final String TAG = "ToDoRecord";

    public ToDoRecord(String dueDate, String name, String description)
    {
        this.dueDate = dueDate;
        this.name = name;
        this.description = description;
        try {
            Log.d(TAG, "ToDoRecord: " + formatter.parse(dueDate));
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "ToDoRecord: EXCEPTION IN PARSING.");
        }
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Due Date: " + dueDate.toString() + "\n" +
                "Name: " + name + "\n" +
                "Description: " + description;
    }

    @Override
    public boolean equals(Object obj)
    {
        ToDoRecord compareMe = null;
        try
        {
            compareMe = (ToDoRecord) obj;
        }
        catch(Exception e)
        {
            Log.d(TAG, "equals: " + "Can not cast as a record.");
        }
        if(compareMe == null)
        {
            Log.d(TAG, "equals: " + "Null comparison.");
            return false;
        }
        if(compareMe.getName().compareTo(this.getName()) == 0)
            return true;
        else
            return false;
    }

    public int compareDueDates(ToDoRecord recToCompare)
    {
        int result = Integer.MAX_VALUE;

        try
        {
            if (formatter.parse(this.dueDate).compareTo(formatter.parse(recToCompare.getDueDate())) == 0)
                result = 0;
            else if (formatter.parse(this.dueDate).compareTo(formatter.parse(recToCompare.getDueDate())) > 0)
                result = 1;
            else
                result = -1;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d(TAG, "compareDueDates: ERROR IN COMPARISON PARSING.");
        }

        if(result == Integer.MAX_VALUE)
        {
            Log.d(TAG, "compareDueDates: MAX VALUE DETECTED. " + Integer.MAX_VALUE);
            return result;
        }
        else
            return result;
    }
}
