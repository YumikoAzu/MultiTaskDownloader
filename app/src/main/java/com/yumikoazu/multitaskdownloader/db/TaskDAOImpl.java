package com.yumikoazu.multitaskdownloader.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yumikoazu.multitaskdownloader.constants.Constants;
import com.yumikoazu.multitaskdownloader.entity.TaskInfo;


/**
 * Created by joker on 2015/7/18.
 */
public class TaskDAOImpl implements TaskDAO {

    private DBHelper mHelper = null;

    public TaskDAOImpl(Context context) {
        mHelper = new DBHelper(context);
    }


    @Override
    public void insertTask(TaskInfo taskInfo) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL(
                "insert into taskinfo (id,url,filename,length,finished,status) values(?,?,?,?,?,?)"
                , new Object[]{taskInfo.getId(),
                        taskInfo.getUrl(),
                        taskInfo.getFileName(),
                        taskInfo.getLength(),
                        taskInfo.getFinished(),
                        Constants.State_NONE}
        );
        db.close();
    }

    @Override
    public void deleteTask(String url) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL(
                "delete from taskinfo where url = ?"
                , new Object[]{url}
        );
        db.close();
    }

    @Override
    public void updateTask(String url, int id, int finished, int status) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL(
                "update taskinfo set finished = ?,status = ? where url = ? and id = ?"
                , new Object[]{finished, status, url, id}
        );
        db.close();
    }

    @Override
    public void updateTaskStatus(String url, int id,int status) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL(
                "update taskinfo set status = ? where url = ? and id = ?"
                , new Object[]{status, url, id}
        );
        db.close();
    }

    @Override
    public TaskInfo queryTask(String url) {
        TaskInfo taskInfo = new TaskInfo();

        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from taskinfo where url = ?", new String[]{url});

        if (cursor.moveToFirst()) {
            taskInfo.setId(cursor.getInt(cursor.getColumnIndex("id")));
            taskInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            taskInfo.setFileName(cursor.getString(cursor.getColumnIndex("filename")));
            taskInfo.setLength(cursor.getInt(cursor.getColumnIndex("length")));
            taskInfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            taskInfo.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
        }

        cursor.close();
        db.close();
        return taskInfo;
    }
}
