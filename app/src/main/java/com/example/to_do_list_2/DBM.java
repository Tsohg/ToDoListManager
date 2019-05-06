package com.example.to_do_list_2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

///Database Manager
public class DBM extends SQLiteOpenHelper
{
    private static final String TAG = "DBM";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "listDB";
    private static final String TABLE_NAME = "toDoTable";

    //columns in records
    private static final String NAME_KEY = "name"; //primary key
    private static final String DUE_KEY = "dueDate";
    private static final String DESC_KEY = "description";

    public DBM(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Creating database.");
        //create the table. borrowed from another sql project.
        String tableQuery = "Create Table " + TABLE_NAME + "(" +
                NAME_KEY + " Text Primary Key, " + DUE_KEY + " Text, " +
                DESC_KEY + " Text " + ")";
        db.execSQL(tableQuery);
        Log.d(TAG, "onCreate: Database successfully created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: Upgrading database.");
        db.execSQL("Drop Table If Exists " + TABLE_NAME); //borrowed from another sql project.
        onCreate(db);
        Log.d(TAG, "onUpgrade: Upgrade finished.");
    }

    ///From the SQL in class project. Edited for my purposes.
    public ToDoRecord getRecord(String name)
    {
        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.query(TABLE_NAME, new String[]{NAME_KEY, DUE_KEY, DESC_KEY},
                NAME_KEY + " = ? ",
                new String[]{String.valueOf(name)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        ToDoRecord rec = new ToDoRecord(cursor.getString(1),
                cursor.getString(0),
                cursor.getString(2));

        return rec;
    }

    public void insertToDoItem(ToDoRecord tdr)
    {
        Log.d(TAG, "insertToDoItem: Inserting into database.");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME_KEY, tdr.getName());
        cv.put(DUE_KEY, tdr.getDueDate());
        Log.d(TAG, "insertToDoItem: " + cv.getAsString(DUE_KEY));
        cv.put(DESC_KEY, tdr.getDescription());
        db.insert(TABLE_NAME, null, cv);
        db.close();
        Log.d(TAG, "insertToDoItem: Insertion successful.");
    }

    public void updateToDoItem(ToDoRecord tdr)
    {
        Log.d(TAG, "updateToDoItem: Updating database record.");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME_KEY, tdr.getName());
        cv.put(DUE_KEY, tdr.getDueDate());
        cv.put(DESC_KEY, tdr.getDescription());
        db.update(TABLE_NAME, cv, NAME_KEY + " = ? ", new String[]{tdr.getName()});
        db.close();
        Log.d(TAG, "updateToDoItem: Update of record successful.");
    }

    public void delete(ToDoRecord tdr)
    {
        Log.d(TAG, "delete: Deleting record.");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, NAME_KEY + " = ? ", new String[]{tdr.getName()});
        db.close();
        Log.d(TAG, "delete: Deletion successful.");
    }

    public int getCount()
    {
        //from SQL project in class.
        String countQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(countQuery, null);
        return cursor.getCount();
    }

    public ArrayList<ToDoRecord> getAllRecords()
    {
        ArrayList<ToDoRecord> list = new ArrayList<ToDoRecord>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cur = db.rawQuery(query,null);

        if(cur.moveToFirst())
        {
            do {
                list.add(new ToDoRecord(cur.getString(1),
                        cur.getString(0),
                        cur.getString(2))); //name comes first then due date in database.
            } while(cur.moveToNext());
        }
        else
        {
            db.close();
            return null;
        }
        return list;
    }

    public ArrayList<ToDoRecord> getSortedList()
    {
        ArrayList<ToDoRecord> unsorted = getAllRecords();
        if(unsorted == null || unsorted.size() == 0)
            return null;
        else {
            quickSortList(unsorted, 0, unsorted.size() - 1);
            return unsorted; //now sorted.
        }
    }

    //Algorithm from: https://www.geeksforgeeks.org/quick-sort/
    //modified for my purposes.
    private void quickSortList(ArrayList<ToDoRecord> list, int lowndx, int highndx)
    {
        if(list == null || list.size() == 0)
            return;

        if (lowndx < highndx)
        {
            int pi = qsPartition(list, lowndx, highndx);
            quickSortList(list, lowndx, pi - 1);
            quickSortList(list, pi + 1, highndx);
        }
    }

    //Algorithm from: https://www.geeksforgeeks.org/quick-sort/
    //modified for my purposes.
    private int qsPartition(ArrayList<ToDoRecord> list, int lowndx, int highndx)
    {
        ToDoRecord pivot = list.get(highndx);
        int i = (lowndx - 1); // index of smaller element
        for (int j = lowndx; j < highndx; j++)
        {
            if (list.get(j).compareDueDates(pivot) <= 0) //less than or equal to pivot
            {
                i++;

                ToDoRecord temp = list.get(i);
                //set i to j
                list.set(i, list.get(j));
                //set j to temp
                list.set(j, temp);
            }
        }

        // store i+1 in temp
        ToDoRecord temp = list.get(i + 1);
        //set i+1 to list at highndx
        list.set(i + 1, list.get(highndx));
        //set highndx to temp
        list.set(highndx, temp);

        return i + 1;
    }

    public static void printList(ArrayList<ToDoRecord> list)
    {
        if(list == null)
            Log.d(TAG, "printList: NULL LIST.");
        else {
            for (ToDoRecord tdr : list) {
                if (tdr != null)
                    Log.d(TAG, "printList: " + tdr.getDueDate() + " :: " + tdr.getName());
                else
                    Log.d(TAG, "printList: NULL RECORD.");
            }
        }
    }
    //from: https://stackoverflow.com/questions/4550896/return-all-columns-of-a-sqlite-table-in-android
    //edited for my purposes.
    public void printTableCols()
    {
        SQLiteDatabase mDataBase = getReadableDatabase();
        Cursor dbCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, null);

        if(dbCursor.moveToFirst())
        {
            Log.d(TAG, "printTableCols: " + dbCursor.getColumnCount());
            int i = 0;
            do {

                Log.d(TAG, "printTableCols: " + dbCursor.getType(i) +
                        " ::: " + dbCursor.getColumnName(i));i++;
            } while(dbCursor.moveToNext());
        }
    }

    public void purgeDatabase()
    {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }
}
