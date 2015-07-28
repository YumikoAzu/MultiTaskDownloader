package com.yumikoazu.multitaskdownloader.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.yumikoazu.multitaskdownloader.constants.Constants;
import com.yumikoazu.multitaskdownloader.db.DBManager;
import com.yumikoazu.multitaskdownloader.download.DownloadManager;
import com.yumikoazu.multitaskdownloader.download.DownloadObservable;
import com.yumikoazu.multitaskdownloader.download.DownloadTask;
import com.yumikoazu.multitaskdownloader.entity.TaskInfo;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by joker on 2015/7/18.
 */
public class DownloadService extends Service {

    private DBManager mDBManager;

    //下载任务集合
    private Map<String, DownloadTask> mTasks = new LinkedHashMap<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDBManager = DBManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return 0;
        }

        int status = intent.getIntExtra("status", 0);
        TaskInfo taskInfo = (TaskInfo) intent.getSerializableExtra("entity");

        switch (status) {
            case Constants.State_START:
                start(taskInfo);
                break;

            case Constants.State_PAUSE:
                pause(taskInfo);
                break;

            case Constants.State_CANCEL:
                cancel(taskInfo);
                break;

            case Constants.State_CANCELING:
                canceling(taskInfo);
                break;

            case Constants.State_RESUME:
                resume(taskInfo);
                break;
        }


        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 等待中任务取消
     *
     * @param taskInfo
     */
    private void canceling(TaskInfo taskInfo) {
        taskInfo.setStatus(Constants.State_CANCELING);
        mDBManager.updateTaskStatus(taskInfo);
        mDBManager.deleteTaskInfo(taskInfo.getUrl());
        DownloadObservable.getInstance().notifyDownloadStateChanged(taskInfo);
    }

    private void resume(TaskInfo taskInfo) {
        TaskInfo info = mDBManager.queryTaskInfoByUrl(taskInfo.getUrl());
        info.setStatus(Constants.State_RESUME);
        mDBManager.updateTaskStatus(info);
        DownloadTask downloadTask = new DownloadTask(mDBManager, info);
        mTasks.put(info.getUrl(), downloadTask);
        DownloadManager.sExecutor.execute(downloadTask);
        DownloadObservable.getInstance().notifyDownloadStateChanged(info);

    }

    private void start(TaskInfo taskInfo) {

        InitTask initTask = new InitTask(taskInfo);
        DownloadManager.sExecutor.execute(initTask);
        DownloadObservable.getInstance().notifyDownloadStateChanged(taskInfo);
    }

    private void pause(TaskInfo taskInfo) {
        taskInfo.setStatus(Constants.State_PAUSE);
        mDBManager.updateTaskStatus(taskInfo);
        DownloadTask task = mTasks.get(taskInfo.getUrl());

        if (null != task) {
            task.setPause(true);
        }

        mTasks.remove(taskInfo.getUrl());

        DownloadObservable.getInstance().notifyDownloadStateChanged(taskInfo);
    }

    /**
     * 下载中任务取消
     *
     * @param taskInfo
     */
    private void cancel(TaskInfo taskInfo) {
        taskInfo.setStatus(Constants.State_CANCEL);
        mDBManager.updateTaskStatus(taskInfo);
        mDBManager.deleteTaskInfo(taskInfo.getUrl());
        mDBManager.deleteThread(taskInfo.getUrl());

        DownloadObservable.getInstance().notifyDownloadStateChanged(taskInfo);
    }

    private class InitTask implements Runnable {

        private TaskInfo mTaskInfo;

        public InitTask(TaskInfo taskInfo) {
            this.mTaskInfo = taskInfo;
        }

        @Override
        public void run() {
            HttpURLConnection connection = null;
            RandomAccessFile raf = null;
            //获取网络文件
            try {
                URL url = new URL(mTaskInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(Constants.NET_TIMEOUT_CONNECT);
                connection.setReadTimeout(Constants.NET_TIMEOUT_READ);
                connection.setRequestMethod("GET");
                int length = -1;
                //获取文件长度
                if (connection.getResponseCode() == HttpStatus.SC_OK) {
                    length = connection.getContentLength();
                }

                if (length <= 0) {
                    return;
                }

                File dir = new File(Constants.DOWN_PATH);
                if (!dir.exists()) {
                    dir.mkdir();
                }

                //在本地创建文件,设置本地文件长度
                File file = new File(dir, mTaskInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                //设置文件长度
                raf.setLength(length);
                mTaskInfo.setLength(length);

                mDBManager.insertTaskInfo(mTaskInfo);
                DownloadTask downloadTask = new DownloadTask(mDBManager, mTaskInfo);
                mTasks.put(mTaskInfo.getUrl(), downloadTask);
                DownloadManager.sExecutor.execute(downloadTask);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (raf != null) {
                        raf.close();
                    }

                    if (connection != null) {
                        connection.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
