package com.example.traffic01.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public MyDatabaseHelper(
            @Nullable Context context,
            @Nullable String name,
            int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table recharge (id integer primary key autoincrement,date String,name String,money integer)");
        db.execSQL("create table car_balance (id integer primary key autoincrement,carname String,balance integer)");
        db.execSQL("create table traffic_lights (id integer,redlight integer,yellolight integer,greenlight integer)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
