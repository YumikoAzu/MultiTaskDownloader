package com.yumikoazu.multitaskdownloader.db;


import com.yumikoazu.multitaskdownloader.entity.TaskInfo;

/**
 * Created by joker on 2015/7/18.
 */
public interface TaskDAO {

    public void insertTask(TaskInfo taskInfo);

    public void deleteTask(String url);

    public void updateTask(String url, int id, int finished, int status);

    public void updateTaskStatus(String url, int id, int status);

    public TaskInfo queryTask(String url);
}
