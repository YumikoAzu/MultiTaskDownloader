package com.yumikoazu.multitaskdownloader;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.yumikoazu.multitaskdownloader.adapter.TaskAdapter;
import com.yumikoazu.multitaskdownloader.constants.Constants;
import com.yumikoazu.multitaskdownloader.download.DownloadManager;
import com.yumikoazu.multitaskdownloader.download.DownloadObservable;
import com.yumikoazu.multitaskdownloader.download.DownloadObserver;
import com.yumikoazu.multitaskdownloader.entity.TaskInfo;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements DownloadObserver {

    private ListView mListView;
    private List<TaskInfo> mTaskInfos;

    private TaskAdapter mTaskAdapter;

    private static Handler sHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.lv);
        mTaskInfos = new ArrayList<>();

        TaskInfo taskInfo = new TaskInfo(0, "http://www.imooc.com/mobile/imooc.apk", "imooc.apk", 0, Constants.State_NONE, 0);
        TaskInfo taskInfo1 = new TaskInfo(1, "http://toutiaoio.qiniudn.com/apk/toutiao.apk", "toutiao.apk", 0, Constants.State_NONE, 0);
        TaskInfo taskInfo2 = new TaskInfo(2, "http://api.dmzj.com/download/dmzj.apk", "dmzj.apk", 0, Constants.State_NONE, 0);
        TaskInfo taskInfo3 = new TaskInfo(3, "http://bb.vmall.com/Hispace.apk", "Hispace.apk", 0, Constants.State_NONE, 0);
        TaskInfo taskInfo4 = new TaskInfo(4, "http://mianshibang.qiniudn.com/mianshibang_1.0.2.apk", "mianshibang_1.0.2.apk", 0, Constants.State_NONE, 0);
        TaskInfo taskInfo5 = new TaskInfo(5, "http://117.27.241.57/d2.eoemarket.com/app0/346/346866/apk/1214099.apk?channel_id=426&wsiphost=local", "budingdonghua.apk", 0, Constants.State_NONE, 0);
        TaskInfo taskInfo6 = new TaskInfo(6, "http://gdown.baidu.com/data/wisegame/413aa7d4c11f09ec/QQyinle_240.apk", "QQyinle_240.apk", 0, Constants.State_NONE, 0);


        mTaskInfos.add(taskInfo);
        mTaskInfos.add(taskInfo1);
        mTaskInfos.add(taskInfo2);
        mTaskInfos.add(taskInfo3);
        mTaskInfos.add(taskInfo4);
        mTaskInfos.add(taskInfo5);
        mTaskInfos.add(taskInfo6);

        mTaskAdapter = new TaskAdapter(this, mTaskInfos);
        mListView.setAdapter(mTaskAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        DownloadManager.getInstance(this).addObserver();
        DownloadObservable.getInstance().registerObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadManager.getInstance(this).removeObserver();
        DownloadObservable.getInstance().unRegisterObserver(this);
    }

    @Override
    public void onDownloadStateChanged(TaskInfo taskInfo) {

    }

    @Override
    public void onDownloadProgressed(final TaskInfo taskInfo) {
        sHandler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        mTaskAdapter.updateProgress(taskInfo.getId(),taskInfo.getFinished(),taskInfo.getLength());
                    }
                }
        );
    }
}
