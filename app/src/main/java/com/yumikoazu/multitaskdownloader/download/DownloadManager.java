package com.yumikoazu.multitaskdownloader.download;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yumikoazu.multitaskdownloader.constants.Constants;
import com.yumikoazu.multitaskdownloader.entity.TaskInfo;
import com.yumikoazu.multitaskdownloader.service.DownloadService;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by joker on 2015/7/18.
 */
public class DownloadManager implements DownloadObserver {
    private static final int THREAD_POOL_SIZE = 32;

    private static DownloadManager sDownloadManager;

    private Context mContext;

    public static ExecutorService sExecutor;

    private ArrayList<TaskInfo> workArray;
    private ArrayList<TaskInfo> waitArray;

    private DownloadManager(Context context) {
        this.mContext = context;
        workArray = new ArrayList<>();
        waitArray = new ArrayList<>();
        sExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public static DownloadManager getInstance(Context context) {
        if (null == sDownloadManager) {
            synchronized (DownloadManager.class) {
                if (null == sDownloadManager) {
                    sDownloadManager = new DownloadManager(context);
                }
            }
        }

        return sDownloadManager;
    }


    public void startTask(TaskInfo taskInfo) {

        allotList(taskInfo);

        Intent intent = getDownloadIntent(taskInfo, Constants.State_START);
        mContext.startService(intent);
    }

    public void pauseTask(TaskInfo taskInfo) {
        removeTaskFromDownloadingList(taskInfo);

        Log.d("tt", taskInfo.toString());
        Intent intent = getDownloadIntent(taskInfo, Constants.State_PAUSE);
        mContext.startService(intent);
    }

    public void resumeTask(TaskInfo taskInfo) {
        allotList(taskInfo);

        Intent intent = getDownloadIntent(taskInfo, Constants.State_RESUME);
        mContext.startService(intent);
    }

    public void cancelTask(TaskInfo taskInfo) {

        if (waitArray.contains(taskInfo)) {
            waitArray.remove(taskInfo);
            Intent intent = getDownloadIntent(taskInfo, Constants.State_CANCELING);
            mContext.startService(intent);
        } else {
            Intent intent = getDownloadIntent(taskInfo, Constants.State_CANCEL);
            mContext.startService(intent);
        }

    }

    public void startAllTask() {

    }

    public void cancelAllTask() {

    }

    public Intent getDownloadIntent(TaskInfo taskInfo, int status) {
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.putExtra("entity", taskInfo);
        intent.putExtra("status", status);
        return intent;
    }


    public void allotList(TaskInfo taskInfo) {
        if (workArray.size() < Constants.TASK_COUNT) {
            workArray.add(taskInfo);
        } else {
            waitArray.add(taskInfo);
        }
    }


    public void removeTaskFromDownloadingList(TaskInfo taskInfo) {
        if (workArray.contains(taskInfo)) {
            workArray.remove(taskInfo);
        }
    }


    public void refreshDownloaderList() {
        if (!waitArray.isEmpty()) {
            TaskInfo taskInfo = waitArray.remove(waitArray.size() - 1);

            workArray.add(taskInfo);

            startTask(taskInfo);
        }
    }

    @Override
    public void onDownloadStateChanged(TaskInfo taskInfo) {
        if (taskInfo.getStatus() == Constants.State_FINISHED) {
            refreshDownloaderList();
        }
    }

    @Override
    public void onDownloadProgressed(TaskInfo taskInfo) {

    }

    public void addObserver() {
        DownloadObservable.getInstance().registerObserver(this);
    }

    public void removeObserver() {
        DownloadObservable.getInstance().unRegisterObserver(this);
    }
}


