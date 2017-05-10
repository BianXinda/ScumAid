package com.bxd.scumaid;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        //ActionBar actionBar = getActionBar();
        //actionBar.hide();
        //this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set Xunfei application ID
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + ScrumAidConstants.APPID);
        //initDB();
    }

    public void startScrumActivity(View view) {
        Intent intent = new Intent(this, ScrumActivity.class);
        startActivity(intent);
    }

    public void startSettingsActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

//    public void initDB() {
//        SQLiteDatabase db = mDbHelp.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(ScrumDbSchema.ScrumEntry.COLUMN_NAME_EN, "nan");
//        values.put(ScrumDbSchema.ScrumEntry.COLUMN_NAME_CN, "南");
//        long id = db.insert(ScrumDbSchema.ScrumEntry.TABLE_NAME, null, values);
//
//        SQLiteDatabase db2 = mDbHelp.getReadableDatabase();
//        String[] projection = {
//                ScrumDbSchema.ScrumEntry._ID,
//                ScrumDbSchema.ScrumEntry.COLUMN_NAME_EN,
//                ScrumDbSchema.ScrumEntry.COLUMN_NAME_CN
//        };
//        Cursor cursor = db2.query(ScrumDbSchema.ScrumEntry.TABLE_NAME, projection, null, null, null, null, null);
////        List items = new ArrayList<>();
//        while (cursor.moveToNext()) {
//            String name1 = cursor.getString(cursor.getColumnIndexOrThrow(ScrumDbSchema.ScrumEntry.COLUMN_NAME_EN));
//            String name2 = cursor.getString(cursor.getColumnIndexOrThrow(ScrumDbSchema.ScrumEntry.COLUMN_NAME_CN));
//            Log.d(TAG, name1 + name2);
//        }
//    }
}
