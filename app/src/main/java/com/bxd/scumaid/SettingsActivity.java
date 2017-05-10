package com.bxd.scumaid;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private String mRecord2Delete = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void onAddNameClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_add_name, null))
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //this.getDialog().cancel();
                    }
                })
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CheckBox checkBox = (CheckBox)((AlertDialog)dialog).findViewById(R.id.checkboxAllNames);
                        if (checkBox.isChecked()) {
                            for (int i = 0; i < ScrumAidConstants.ATTENDEES_EN.length; ++i) {
                               addName2Db(ScrumAidConstants.ATTENDEES_EN[i], ScrumAidConstants.ATTENDEES_CN[i]);
                            }
                        } else {
                            EditText editTextEn = (EditText)((AlertDialog)dialog).findViewById(R.id.inputNameEn);
                            EditText editTextCn = (EditText)((AlertDialog)dialog).findViewById(R.id.inputNameCn);
                            Log.d(ScrumAidConstants.TAG, editTextEn.getText().toString() + editTextCn.getText().toString());
                            addName2Db(editTextEn.getText().toString(), editTextCn.getText().toString());
                        }
                    }
                });
        builder.create().show();
    }

    public void addName2Db(String nameEn, String nameCn) {
        SQLiteDatabase db = ScrumDbHelper.getInstance(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ScrumDbSchema.ScrumEntry.COLUMN_NAME_EN, nameEn);
        values.put(ScrumDbSchema.ScrumEntry.COLUMN_NAME_CN, nameCn);
        db.insert(ScrumDbSchema.ScrumEntry.TABLE_NAME, null, values);
    }

    public void onDeleteNameClicked(View view) {
        mRecord2Delete = null;

        SQLiteDatabase db = ScrumDbHelper.getInstance(this).getReadableDatabase();
        List<String> records = new ArrayList<>();
        String[] projection = {
                ScrumDbSchema.ScrumEntry._ID,
                ScrumDbSchema.ScrumEntry.COLUMN_NAME_EN,
                ScrumDbSchema.ScrumEntry.COLUMN_NAME_CN
        };
        Cursor cursor = db.query(ScrumDbSchema.ScrumEntry.TABLE_NAME, projection, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(ScrumDbSchema.ScrumEntry._ID));
            String nameEn = cursor.getString(cursor.getColumnIndexOrThrow(ScrumDbSchema.ScrumEntry.COLUMN_NAME_EN));
            String nameCn = cursor.getString(cursor.getColumnIndexOrThrow(ScrumDbSchema.ScrumEntry.COLUMN_NAME_CN));
            records.add(id + ":" + nameEn + ":" + nameCn);
            Log.d(ScrumAidConstants.TAG, "Query from DB, id: " + id + ", EN name: " + nameEn + ", CN name: " + nameCn);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_delete_name, null);
        builder.setView(dialogView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, records);
        Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinnerNames);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Log.d(ScrumAidConstants.TAG, "Item selected:" + item);
                mRecord2Delete = item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //this.getDialog().cancel();
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mRecord2Delete != null) {
                            SQLiteDatabase db = ScrumDbHelper.getInstance(SettingsActivity.this).getWritableDatabase();
                            String selection = ScrumDbSchema.ScrumEntry._ID + " LIKEe ?";
                            String[] selectionArgs = {mRecord2Delete.split(":")[0]};
                            db.delete(ScrumDbSchema.ScrumEntry.TABLE_NAME, selection, selectionArgs);
                        }
                    }
                });
        builder.create().show();
    }
}
