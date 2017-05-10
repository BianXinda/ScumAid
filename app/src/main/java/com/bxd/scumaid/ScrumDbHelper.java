package com.bxd.scumaid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ScrumDbHelper extends SQLiteOpenHelper {
    private static ScrumDbHelper sInstance;
    public static synchronized ScrumDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ScrumDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + ScrumDbSchema.ScrumEntry.TABLE_NAME +
            " (" +
            ScrumDbSchema.ScrumEntry._ID + " INTEGER PRIMARY KEY," +
            ScrumDbSchema.ScrumEntry.COLUMN_NAME_EN + " TEXT," +
            ScrumDbSchema.ScrumEntry.COLUMN_NAME_CN + " TEXT" +
            ")";
    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + ScrumDbSchema.ScrumEntry.TABLE_NAME;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ScrumAid.db";


    public ScrumDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }
}
