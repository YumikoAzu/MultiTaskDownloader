package com.yumikoazu.multitaskdownloader.db;

import android.content.Context;
import android.util.Log;

import com.yumikoazu.multitaskdownloader.entity.TaskInfo;
import com.yumikoazu.multitaskdownloader.entity.ThreadInfo;

import java.util.List;

/**
 * Created by joker on 2015/7/18.
 */
public class DBManager {

    private static DBManager sManager = null;
    private TaskDAOImpl mTaskDAO;
    private ThreadDAOImpl mThreadDAO;


    private DBManager(Context context) {
        mTaskDAO = new TaskDAOImpl(context);
        mThreadDAO = new ThreadDAOImpl(context);
    }

    public static DBManager getInstance(Context context) {
        if (null == sManager) {
            synchronized (DBManager.class) {
                if (null == sManager) {
                    sManager = new DBManager(context);
                }
            }
        }

        return sManager;
    }

    public synchronized void insertTaskInfo(TaskInfo info) {
        Log.d("taskins",info.toString());
        mTaskDAO.insertTask(info);
    }

    /**
     * 根据下载地址删除一条下载任务数据信息
     *
     * @param url 下载地址
     */
    public synchronized void deleteTaskInfo(String url) {
        mTaskDAO.deleteTask(url);
    }

    /**
     * 更新一条下载任务数据信息
     *
     * @param info 下载任务对象
     */
    public synchronized void updateTaskInfo(TaskInfo info) {
        mTaskDAO.updateTask(info.getUrl(), info.getId(), info.getFinished(), info.getStatus());
    }

    public synchronized void updateTaskStatus(TaskInfo info) {
        mTaskDAO.updateTaskStatus(info.getUrl(),info.getId(),info.getStatus());
    }

    /**
     * 根据下载地址查询一条下载任务数据信息
     *
     * @param url 下载地址
     * @return 下载任务对象
     */
    public synchronized TaskInfo queryTaskInfoByUrl(String url) {
        return mTaskDAO.queryTask(url);
    }

    public synchronized void insertThread(ThreadInfo threadInfo) {
        Log.d("threadins",threadInfo.toString());
        mThreadDAO.insertThread(threadInfo);
    }

    public synchronized void deleteThread(String url) {
        mThreadDAO.deleteThread(url);
    }

    public synchronized void updateThread(String url, int thread_id, int finished) {
        mThreadDAO.updateThread(url, thread_id, finished);
    }

    public synchronized List<ThreadInfo> getThreads(String url) {
        return mThreadDAO.getThreads(url);
    }

    public boolean isExists(String url, int thread_id) {
        return mThreadDAO.isExists(url, thread_id);
    }

}
