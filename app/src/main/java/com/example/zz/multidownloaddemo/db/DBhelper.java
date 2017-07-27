package com.example.zz.multidownloaddemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 类描述：数据库管理类
 * 创建人：zz
 * 创建时间： 2017/7/19 16:07
 */


public class DBhelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "download.db";
    private static final int DB_VERSION = 1;
    private static DBhelper dBhelper = null;
    private static final String CREATE_TABLE ="create table thread_info(id integer primary key autoincrement, " +
            "thread_id integer,url text,start integer,end integer,finished integer)";
    private static final String DROP_TABLE = "drop table if exists thread_info";
    private DBhelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DBhelper getdBhelper(Context context){
        if(dBhelper == null){
            dBhelper = new DBhelper(context);
        }
        return dBhelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
