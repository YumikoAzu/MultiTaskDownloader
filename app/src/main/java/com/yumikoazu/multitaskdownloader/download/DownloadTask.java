package com.yumikoazu.multitaskdownloader.download; 
 
 
 
 
import android.util.Log;
 
 
import com.yumikoazu.multitaskdownloader.constants.Constants; 
import com.yumikoazu.multitaskdownloader.db.DBManager; 
import com.yumikoazu.multitaskdownloader.entity.TaskInfo; 
import com.yumikoazu.multitaskdownloader.entity.ThreadInfo; 
 
 
 
 
import org.apache.http.HttpStatus;
 
 
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
 
 
/** 
 * Created by joker on 2015/7/18. 
 */ 
public class DownloadTask implements Runnable {
 
 
    public boolean isPause = false;
 
 
    public boolean isIdle = false;
 
 
    private int mFinished = 0;
 
 
    private int mThreadCount = 3;//每个任务下载的线程数量
 
 
    private List<DownloadThread> threads;//线程集合
 
 
    private DBManager mDBManager;
 
 
    private TaskInfo mTaskInfo;
 
 
 
 
    public DownloadTask(DBManager dbManager, TaskInfo taskInfo) {
        this.mTaskInfo = taskInfo;
        mDBManager = dbManager;
        threads = new ArrayList<>();
    } 
 
 
    public void setPause(boolean isPause) {
        this.isPause = isPause;
    } 
 
 
    @Override 
    public void run() { 
        List<ThreadInfo> threadInfos = mDBManager.getThreads(mTaskInfo.getUrl());
 
 
        if (threadInfos.size() == 0) {
            //获得每个线程下载长度 
            int length = mTaskInfo.getLength() / mThreadCount;
 
 
            for (int i = 0; i < mThreadCount; i++) {
                ThreadInfo threadInfo = new ThreadInfo(
                        i, mTaskInfo.getUrl(),
                        length * i, (i + 1) * length - 1, 0
                ); 
 
 
                if (i == mThreadCount - 1) {
                    threadInfo.setEnd(mTaskInfo.getLength());
                } 
 
 
                threadInfos.add(threadInfo);
 
 
                mDBManager.insertThread(threadInfo);
            } 
 
 
        } 
 
 
        for (ThreadInfo threadInfo : threadInfos) {
            DownloadThread downloadThread = new DownloadThread(threadInfo);
            DownloadManager.sExecutor.execute(downloadThread);
            threads.add(downloadThread);
        } 
 
 
        mTaskInfo.setStatus(Constants.State_DOWNLOADING);
        mDBManager.updateTaskStatus(mTaskInfo);
    } 
 
 
    /** 
     * 判断所有线程是否都执行完毕(单个任务是否完成) 
     */ 
    public synchronized void checkAllThreadsFinished() { 
        boolean allFinished = true;
 
 
        for (DownloadThread thread : threads) {
            if (!thread.isFinished) {
                allFinished = false;
                break; 
            } 
        } 
 
 
        if (allFinished) {
 
 
            mTaskInfo.setFinished(mFinished);
            mTaskInfo.setStatus(Constants.State_FINISHED);
            mDBManager.updateTaskInfo(mTaskInfo);
            //下载完毕,删除线程信息 
            mDBManager.deleteThread(mTaskInfo.getUrl());
 
 
            isIdle = true;
            DownloadObservable.getInstance().notifyDownloadStateChanged(mTaskInfo);
            DownloadObservable.getInstance().notifyDownloadProgressed(mTaskInfo);
 
 
        } 
 
 
    } 
 
 
     
 
 
 
 
    private class DownloadThread implements Runnable {
 
 
        private ThreadInfo mThreadInfo;
        public boolean isFinished = false;//标识线程是否执行完毕
 
 
        private int progress;
 
 
        private DownloadThreadListener mDownloadThreadListener;
 
 
        public DownloadThread(ThreadInfo threadInfo) {
            this.mThreadInfo = threadInfo;
        } 
 
 
 
 
        @Override 
        public void run() { 
 
 
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream is = null;
 
 
            try { 
                URL url = new URL(mTaskInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                //设置下载位置 
                int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
                conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getEnd());
                //设置文件写入位置 
                File file = new File(Constants.DOWN_PATH, mTaskInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                //seek()在读写的时候跳过设置好的字节数,从下一个字节数开始读写 
                //例如:seek(100),则跳过100个字节 从第101个字节开始读写 
                raf.seek(start);
                //开始下载 
                int len = -1;
                byte[] buff = new byte[1024 * 4];
 
 
 
 
                //累加完成进度 
                mFinished += mThreadInfo.getFinished();
 
 
                if (conn.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT) {
                    is = conn.getInputStream();
                    long time = System.currentTimeMillis();
                    //读取数据 
                    while ((len = is.read(buff)) != -1) {
                        //写入文件 
                        raf.write(buff, 0, len);
 
 
                        synchronized (this) {
                            //累加整个文件完成进度 
                            mFinished += len;
                            //累加每个线程完成进度 
                            Log.d("finish", mFinished + "");
                            mTaskInfo.setFinished(mFinished);
                            mThreadInfo.setFinished(mThreadInfo.getFinished() + len);
                            Log.d("finishthread", mThreadInfo.getFinished() + len + "");
 
 
                            //在下载暂停时,保存下载进度 
                            if (isPause) {
                                mTaskInfo.setStatus(Constants.State_PAUSE);
                                mDBManager.updateTaskInfo(mTaskInfo);
 
 
                                mDBManager.updateThread(
                                        mThreadInfo.getUrl(),
                                        mThreadInfo.getId(), mThreadInfo.getFinished()
                                ); 
                                return; 
                            } 
                        } 
 
 
                        //间隔200毫秒更新一次进度 
                        if (System.currentTimeMillis() - time > 200) {
                            time = System.currentTimeMillis();
                            DownloadObservable.getInstance().notifyDownloadProgressed(mTaskInfo);
                        } 
 
 
 
 
                    } 
 
 
                    //标识线程执行完毕 
                    isFinished = true;
 
 
 
 
                    //检测下载任务是否执行完毕 
                    checkAllThreadsFinished(); 
                } 
            } catch (Exception e) {
                e.printStackTrace();
            } finally { 
                try { 
 
 
                    if (is != null) {
                        is.close();
                    } 
 
 
                    if (raf != null) {
                        raf.close();
 
 
                    } 
 
 
                    if (conn != null) {
 
 
                        conn.disconnect();
                    } 
                } catch (Exception e) {
                    e.printStackTrace();
                } 
            } 
        } 
    } 
} 