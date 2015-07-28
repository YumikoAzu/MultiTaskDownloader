package com.yumikoazu.multitaskdownloader.download;

import com.yumikoazu.multitaskdownloader.entity.TaskInfo;

/**
 * Created by joker on 2015/7/25.
 */
public interface DownloadObserver {
    public abstract void onDownloadStateChanged(TaskInfo taskInfo);

    public abstract void onDownloadProgressed(TaskInfo taskInfo);
}
