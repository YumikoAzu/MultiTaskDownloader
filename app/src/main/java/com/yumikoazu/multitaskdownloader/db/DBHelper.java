package com.yumikoazu.multitaskdownloader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by joker on 2015/7/18.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DB_NAME = "dlder.db";

    private static final String CREATE_TASKINFO = "create table taskinfo(" +
            "id integer primary key autoincrement," +
            "url text," +
            "filename text," +
            "length integer," +
            "finished integer," +
            "status integer)";


    private static final String CREATE_DOWNLOAD = "create table download(" +
            "id integer primary key autoincrement," +
            "thread_id integer," +
            "url text," +
            "start integer," +
            "end integer," +
            "finished integer)";

    private static final String DROP_TASKINFO = "drop table if exists taskinfo";
    private static final String DROP_DOWNLOAD = "drop table if exists download";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TASKINFO);
        db.execSQL(CREATE_DOWNLOAD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TASKINFO);
        db.execSQL(DROP_DOWNLOAD);
        db.execSQL(CREATE_TASKINFO);
        db.execSQL(CREATE_DOWNLOAD);
    }
}

