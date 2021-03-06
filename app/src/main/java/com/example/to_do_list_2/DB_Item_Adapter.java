package com.example.to_do_list_2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DB_Item_Adapter extends RecyclerView.Adapter<DB_Item_Adapter.ToDoViewHolder>
{
    private ArrayList<ToDoRecord> records;
    private static final String TAG = "DB_Item_Adapter";
    private DBM dbm;
    private SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");

    public DB_Item_Adapter(ArrayList<ToDoRecord> allRecords, DBM dbm)
    {
        records = allRecords;
        this.dbm = dbm;
    }

    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View viewItem = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycle_item, viewGroup, false);
        return new ToDoViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final ToDoViewHolder toDoViewHolder, int i) {
        if(records.size() > 0) {
            toDoViewHolder.nameView.setText(records.get(i).getName());
            toDoViewHolder.dateView.setText(records.get(i).getDueDate());
            setDaysLeft(toDoViewHolder.daysLeftView, toDoViewHolder.dateView);

            toDoViewHolder.options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) { //NTS: be wary of this final.
                    //sourced from: https://stackoverflow.com/questions/37601346/create-options-menu-for-recyclerview-item
                    //edited for my purposes.
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.inflate(R.menu.popup_menu);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.popup_desc:
                                    handlePopupDesc(toDoViewHolder, v);
                                    return true;
                                case R.id.popup_delete:
                                    handlePopupDelete(toDoViewHolder);
                                    return true;
                                default:
                                    Log.d(TAG, "onMenuItemClick: Default reached");
                                    return true;
                            }
                        }
                    });
                    popupMenu.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(records == null)
            return 0;
        else
            return records.size();
    }

    public void handlePopupDesc(ToDoViewHolder tdvh, View view)
    {
        ToDoRecord rec = dbm.getRecord(tdvh.nameView.getText().toString());
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.mainActivityContextHandle);
        alert.setMessage(rec.getDescription());
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    //delete from database and view.
    private void handlePopupDelete(ToDoViewHolder tdvh)
    {
        RecyclerView rv = MainActivity.mainActivityContextHandle.findViewById(R.id.recyclerView); //might not get what i am looking for

        dbm.delete(new ToDoRecord(tdvh.dateView.getText().toString(),
                tdvh.nameView.getText().toString(), ""));

        records = dbm.getSortedList(); //update records.
        rebuildRecyclerView(rv);

        Log.d(TAG, "handlePopupDelete: View removed.");
    }

    private void rebuildRecyclerView(RecyclerView rv)
    {
        rv.removeAllViews();
        rv.setAdapter(new DB_Item_Adapter(records, dbm));
    }

    private void setDaysLeft(TextView daysLeftView, TextView dueDateView)
    {
        try
        {
            Date d = new Date();
            Date date1 = formatter.parse(formatter.format(d));
            Date date2 = formatter.parse(dueDateView.getText().toString());
            int daysLeft = (int)( (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24) ); //milliseconds to days conversion.

            daysLeftView.setText("Days Left: "+ daysLeft);

            if(daysLeft < 0)
            {
                daysLeftView.setTextColor(Color.RED);
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

    }

    public class ToDoViewHolder extends RecyclerView.ViewHolder {

        public TextView dateView;
        public TextView nameView;
        public TextView options;
        public TextView daysLeftView;

        public ToDoViewHolder(@NonNull View itemView)
        {
            super(itemView);
            dateView = itemView.findViewById(R.id.dueDateView);
            nameView = itemView.findViewById(R.id.nameView);
            options = itemView.findViewById(R.id.optionDotsView);
            daysLeftView = itemView.findViewById(R.id.daysLeftView);
        }
    }
}
