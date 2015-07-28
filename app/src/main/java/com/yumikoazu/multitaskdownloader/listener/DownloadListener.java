package com.yumikoazu.multitaskdownloader.listener;


import com.yumikoazu.multitaskdownloader.entity.TaskInfo;

/**
 * Created by joker on 2015/7/20.
 */
public interface DownloadListener {

    public void onFinish(TaskInfo taskInfo);
}
