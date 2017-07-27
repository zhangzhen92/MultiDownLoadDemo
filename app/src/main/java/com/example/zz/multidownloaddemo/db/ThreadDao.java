package com.example.zz.multidownloaddemo.db;


import com.example.zz.multidownloaddemo.bean.ThreadInfo;

import java.util.List;

/**
 * 类描述：数据库操作接口
 * 创建人：zz
 * 创建时间： 2017/7/19 16:19
 */


public interface ThreadDao {

    /**
     * 插入线程
     * @param threadInfo
     */
    public void insertThread(ThreadInfo threadInfo);

    /**
     * 删除指定线程
     * @param url
     * @param thread_id
     */
    public void deleteThread(String url, int thread_id);

    /**
     * 更新线程
     * @param url
     * @param thread_id
     * @param finished
     */
    public void updateThread(String url, int thread_id, int finished);


    /**
     * 获取当前文件下所有的线程信息
     * @param url
     * @return
     */
    public List<ThreadInfo> getThreads(String url);


    /**
     * 判断当前线程是否存在
     * @param url
     * @param thread_id
     * @return
     */
    public boolean threadIsExists(String url, int thread_id);
}
