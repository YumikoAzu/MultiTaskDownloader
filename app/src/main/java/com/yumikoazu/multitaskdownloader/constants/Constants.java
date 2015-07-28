package com.yumikoazu.multitaskdownloader.constants;

import android.os.Environment;

/**
 * Created by joker on 2015/7/18.
 */
public class Constants {

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_RESUME = "ACTION_RESUME";

    public static final int State_NONE = 999;
    public static final int State_START = 0;//刚开始下载状态
    public static final int State_DOWNLOADING = 1; //下载中状态
    public static final int State_PAUSE = 2; //暂停状态
    public static final int State_FINISHED = 3;//完成状态
    public static final int State_CANCEL = 4; //DownloadBean被取消
    public static final int State_RESUME = 5;

    public static final int State_FILEERROR = 6; //文件异常，文件版本不连续

    public final static int State_CANCELING = 7; //任务取消中

    public static final int NET_TIMEOUT_READ  = 5000; //读取超时
    public static final int NET_TIMEOUT_CONNECT = 20000; //连接超时

    public static final int TASK_COUNT = 4;

    /** URL不正确 */
    public static final String ERROR_URL = "101";

    /** 下载过程中网络断开或者超时 该异常发生时下载终端需要用户点击下载以继续下载 */
    public static final String ERROR_DOWNLOAD_INTERRUPT = "103";

    public static final String DOWN_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/dl/";
}
