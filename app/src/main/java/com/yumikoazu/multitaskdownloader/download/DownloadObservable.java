package com.yumikoazu.multitaskdownloader.download;

import com.yumikoazu.multitaskdownloader.entity.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joker on 2015/7/25.
 */
public class DownloadObservable {


    private static DownloadObservable sObservable;


    private DownloadObservable() {

    }

    public static DownloadObservable getInstance() {
        if (null == sObservable) {
            synchronized (DownloadObservable.class) {
                if (null == sObservable) {
                    sObservable = new DownloadObservable();
                }
            }
        }

        return sObservable;
    }

    /**
     * 用于记录观察者，当信息发送了改变，需要通知他们
     */
    private List<DownloadObserver> mObservers = new ArrayList<>();

    /**
     * 注册观察者
     */
    public void registerObserver(DownloadObserver observer) {
        synchronized (mObservers) {
            if (!mObservers.contains(observer)) {
                mObservers.add(observer);
            }
        }
    }

    /**
     * 反注册观察者
     */
    public void unRegisterObserver(DownloadObserver observer) {
        synchronized (mObservers) {
            if (mObservers.contains(observer)) {
                mObservers.remove(observer);
            }
        }
    }

    /**
     * 当下载状态发生改变的时候回调
     */
    public void notifyDownloadStateChanged(TaskInfo info) {
        synchronized (mObservers) {
            for (DownloadObserver observer : mObservers) {
                observer.onDownloadStateChanged(info);
            }
        }
    }

    /**
     * 当下载进度发生改变的时候回调
     */
    public void notifyDownloadProgressed(TaskInfo info) {
        synchronized (mObservers) {
            for (DownloadObserver observer : mObservers) {
                observer.onDownloadProgressed(info);
            }
        }
    }
}
