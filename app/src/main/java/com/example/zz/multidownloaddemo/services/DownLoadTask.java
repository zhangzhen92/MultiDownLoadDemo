package com.example.zz.multidownloaddemo.services;

import android.content.Context;
import android.content.Intent;

import com.example.zz.multidownloaddemo.bean.FileInfo;
import com.example.zz.multidownloaddemo.bean.ThreadInfo;
import com.example.zz.multidownloaddemo.db.ThreadDao;
import com.example.zz.multidownloaddemo.db.ThreadDaoImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 类描述：文件下载类
 * 创建人：zz
 * 创建时间： 2017/7/19 17:00
 */


public class DownLoadTask {

    private Context mContext;
    private FileInfo mFileInfo;
    private ThreadDao threadDao;
    private long mFinished = 0;     //已下载的大小          如果文件过大，定义为long型，发送进度，否则会出现越界
    public boolean isPause = false;   //是否暂停
    private int mThreadCount = 1;     //设置几个线程进行下载
    private List<DownLoadThread> mThreadList;
    public static ExecutorService sExecutorService = Executors.newCachedThreadPool();

    public DownLoadTask(Context context, FileInfo fileInfo,int threadCount) {
        this.mContext = context;
        this.mFileInfo = fileInfo;
        this.mThreadCount = threadCount;
        threadDao = new ThreadDaoImpl(context);
    }

    public void downLoad(){
        List<ThreadInfo> threads = threadDao.getThreads(mFileInfo.getUrl());
        if(threads.size() == 0){
             int length = mFileInfo.getLength() / mThreadCount;
            for (int i = 0; i < mThreadCount; i++) {
                ThreadInfo threadInfo = new ThreadInfo(i,mFileInfo.getUrl(),i * length,(i+1) * length -1,0);
                if(i == mThreadCount -1){
                    threadInfo.setEnd(mFileInfo.getLength());
                }
                threads.add(threadInfo);
                //向数据库插入线程信息
                    threadDao.insertThread(threadInfo);
            }
        }
        mThreadList = new ArrayList<>();
        for(ThreadInfo info : threads){
            DownLoadThread thread = new DownLoadThread(info);
            DownLoadTask.sExecutorService.execute(thread);
            mThreadList.add(thread);

        }
    }


    /**
     * 检查所有的线程是否下载完毕
     */
    private synchronized void checkAllThreadFinish(){
     boolean allFinished = true;
        for(DownLoadThread thread : mThreadList){
            if(!thread.threadFinish){
                allFinished =false;
                break;
            }
        }
        if(allFinished){
           Intent intent = new Intent(DownLoadService.FINISH_ACTION);
            intent.putExtra("fileInfo",mFileInfo);
            mContext.sendBroadcast(intent);
        }

    }

    class DownLoadThread extends Thread {
        private ThreadInfo mThreadInfo;
        private boolean threadFinish = false;

        public DownLoadThread(ThreadInfo threadInfo) {
            this.mThreadInfo = threadInfo;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream inputStream = null;
            try {
                //设置文件写入位置
                //开始下载
                //设置下载位置
                URL url = new URL(mFileInfo.getUrl());
                 conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
                conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getEnd()); //可以设置下载开始的位置结束的位置

                File file = new File(DownLoadService.DOWN_LOAD,mFileInfo.getFileName());
                 raf = new RandomAccessFile(file,"rwd");        //随机访问文件流
                raf.seek(start);             //在读写文件的时候，跳过指定字节数，从所在位置的下一位进行读写

              Intent intent = new Intent(DownLoadService.UPDATE_ACTION);
                //开始下载
                mFinished += mThreadInfo.getFinished();
                if(conn.getResponseCode() == 206){
                    //读取数据
                     inputStream = conn.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int len  = -1;
                    long time = System.currentTimeMillis();
                    while((len = inputStream.read(buffer)) != -1){
                      raf.write(buffer,0,len);
                        mFinished += len;
                        mThreadInfo.setFinished(mThreadInfo.getFinished() + len);
                        if(System.currentTimeMillis() - time > 1000){
                            int progress = (int)(mFinished *100 / mFileInfo.getLength());
                            intent.putExtra("finished",progress);
                            intent.putExtra("id",mFileInfo.getId());
                            mContext.sendBroadcast(intent);
                            time = System.currentTimeMillis();
                        }
                        if(isPause){
                            threadDao.updateThread(mFileInfo.getUrl(),mThreadInfo.getId(),mThreadInfo.getFinished());
                            return;

                        }
                    }
                    //下载完成要把进度为100的进度发送至主界面
                    intent.putExtra("finished",100);
                    intent.putExtra("id",mFileInfo.getId());
                    mContext.sendBroadcast(intent);
                    threadFinish = true;
                    checkAllThreadFinish();
                    threadDao.deleteThread(mFileInfo.getUrl(),mThreadInfo.getId());

                }

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    conn.disconnect();
                    raf.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
