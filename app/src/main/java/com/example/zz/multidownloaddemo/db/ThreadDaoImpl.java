package com.example.zz.multidownloaddemo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.zz.multidownloaddemo.bean.ThreadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：数据库接口实现类
 * 创建人：zz
 * 创建时间： 2017/7/19 16:35
 */


public class ThreadDaoImpl implements ThreadDao{

    private DBhelper dBhelper;

    public ThreadDaoImpl(Context context) {
     dBhelper = DBhelper.getdBhelper(context);
    }

    @Override
    public synchronized void insertThread(ThreadInfo threadInfo) {
        SQLiteDatabase db = dBhelper.getWritableDatabase();
        db.execSQL("insert into thread_info(thread_id,url,start,end,finished) values(?,?,?,?,?)",new Object[]{
                threadInfo.getId(),threadInfo.getUrl(),threadInfo.getStart(),threadInfo.getEnd(),threadInfo.getFinished()
        });
        db.close();
    }

    @Override
    public synchronized void deleteThread(String url, int thread_id) {
      SQLiteDatabase db = dBhelper.getWritableDatabase();
        db.execSQL("delete from thread_info where url = ? and thread_id = ? ",new Object[]{url,thread_id});
        db.close();
    }

    @Override
    public synchronized void updateThread(String url, int thread_id, int finished) {
        SQLiteDatabase db = dBhelper.getWritableDatabase();
        db.execSQL("update thread_info set finished = ? where url = ? and thread_id = ? ",new Object[]{finished,url,thread_id});
        db.close();
    }

    @Override
    public List<ThreadInfo> getThreads(String url) {
        List<ThreadInfo> lists = new ArrayList<>();
        SQLiteDatabase db = dBhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from thread_info where  url = ?",new String[]{url});
        while (cursor.moveToNext()){
            ThreadInfo threadInfo = new ThreadInfo();
            threadInfo.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
            threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            threadInfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            threadInfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
            threadInfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            lists.add(threadInfo);
        }
        cursor.close();
        db.close();
        return lists;
    }

    @Override
    public boolean threadIsExists(String url, int thread_id) {
        SQLiteDatabase db = dBhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from thread_info where  url = ? and thread_id = ?",new String[]{url,thread_id+""});
        boolean exists = cursor.moveToNext();
        db.close();
        cursor.close();
        return exists;
    }
}
