package com.yumikoazu.multitaskdownloader.db;


import com.yumikoazu.multitaskdownloader.entity.ThreadInfo;

import java.util.List;

/**
 * Created by joker on 2015/7/23.
 */
public interface ThreadDAO {
    public void insertThread(ThreadInfo threadInfo);

    public void deleteThread(String url);

    public void updateThread(String url, int thread_id, int finished);

    public List<ThreadInfo> getThreads(String url);

    public boolean isExists(String url, int thread_id);
}
