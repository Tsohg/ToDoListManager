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

import java.util.ArrayList;

public class DB_Item_Adapter extends RecyclerView.Adapter<DB_Item_Adapter.ToDoViewHolder>
{
    private ArrayList<ToDoRecord> records;
    private static final String TAG = "DB_Item_Adapter";
    private DBM dbm;

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
//                                case R.id.popup_update:
//                                    handlePopupUpdate(toDoViewHolder);
//                                    return true;
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
//        Toast.makeText(MainActivity.mainActivityContextHandle,
//                "Currently Unimplemented.", Toast.LENGTH_SHORT).show();
        //Log.d(TAG, "handlePopupDesc: Inside Method");
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
        //Log.d(TAG, "handlePopupDesc: After instructions");
    }
    //delete from database and view.
    private void handlePopupDelete(ToDoViewHolder tdvh)
    {
        RecyclerView rv = MainActivity.mainActivityContextHandle.findViewById(R.id.recyclerView); //might not get what i am looking for
//        if(rv == null)
//            Log.d(TAG, "handlePopupDelete: Recycler View Null");
//        rv.removeViewAt(0);

        dbm.delete(new ToDoRecord(tdvh.dateView.getText().toString(),
                tdvh.nameView.getText().toString(), ""));

        records = dbm.getSortedList(); //update records.
        rebuildRecyclerView(rv);

        Log.d(TAG, "handlePopupDelete: View removed.");
    }

//    private void handlePopupUpdate(ToDoViewHolder tdvh)
//    {
//        RecyclerView rv = MainActivity.mainActivityContextHandle.findViewById(R.id.recyclerView); //might not get what i am looking for
//        ToDoRecord record = new ToDoRecord(tdvh.dateView.getText().toString(),
//                tdvh.nameView.getText().toString(),
//                )
//        //update database record
//
//        //get new sorted records
//        records = dbm.getSortedList();
//
//        //rebuild rv
//        rebuildRecyclerView(rv);
//    }

    private void rebuildRecyclerView(RecyclerView rv)
    {
        rv.removeAllViews();
        rv.setAdapter(new DB_Item_Adapter(records, dbm));
    }

    public class ToDoViewHolder extends RecyclerView.ViewHolder {

        public TextView dateView;
        public TextView nameView;
        public TextView options;

        public ToDoViewHolder(@NonNull View itemView)
        {
            super(itemView);
            dateView = itemView.findViewById(R.id.dueDateView);
            nameView = itemView.findViewById(R.id.nameView);
            options = itemView.findViewById(R.id.optionDotsView);
            dateView.setTextColor(Color.BLACK);
            nameView.setTextColor(Color.BLACK);
            options.setTextColor(Color.BLACK);
        }
    }
}
