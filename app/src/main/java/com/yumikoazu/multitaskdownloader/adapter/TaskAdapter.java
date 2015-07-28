package com.yumikoazu.multitaskdownloader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yumikoazu.multitaskdownloader.R;
import com.yumikoazu.multitaskdownloader.download.DownloadManager;
import com.yumikoazu.multitaskdownloader.entity.TaskInfo;

import java.util.List;

/**
 * Created by joker on 2015/7/28.
 */
public class TaskAdapter extends BaseAdapter {

    private Context mContext;
    private List<TaskInfo> mTaskInfos;
    private LayoutInflater mInflater;

    public TaskAdapter(Context context, List<TaskInfo> taskInfos) {
        this.mContext = context;
        this.mTaskInfos = taskInfos;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mTaskInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mTaskInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TaskInfo taskInfo = mTaskInfos.get(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvFile = (TextView) convertView.findViewById(R.id.textView);
            viewHolder.tvPro = (TextView) convertView.findViewById(R.id.tvPro);
            viewHolder.btStart = (Button) convertView.findViewById(R.id.button2);
            viewHolder.btPause = (Button) convertView.findViewById(R.id.button);
            viewHolder.btResume = (Button) convertView.findViewById(R.id.button3);
            viewHolder.btCancel = (Button) convertView.findViewById(R.id.button4);
            viewHolder.pbFile = (ProgressBar) convertView.findViewById(R.id.progressBar);


            viewHolder.btStart.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownloadManager.getInstance(mContext).startTask(taskInfo);
                        }
                    }
            );

            viewHolder.btPause.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownloadManager.getInstance(mContext).pauseTask(taskInfo);
                        }
                    }
            );

            viewHolder.btResume.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownloadManager.getInstance(mContext).resumeTask(taskInfo);
                        }
                    }
            );

            viewHolder.btCancel.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownloadManager.getInstance(mContext).cancelTask(taskInfo);
                        }
                    }
            );

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvFile.setText(taskInfo.getFileName());
        viewHolder.pbFile.setMax(taskInfo.getLength());
        viewHolder.pbFile.setProgress(taskInfo.getFinished());


        if (taskInfo.getLength() == 0) {
            viewHolder.tvPro.setText(0 + "%");
        } else {
            viewHolder.tvPro.setText(taskInfo.getFinished() * 100 / taskInfo.getLength() + "%");
        }


        return convertView;
    }


    /**
     * 更新列表项中的进度条
     */
    public void updateProgress(Integer id, int progress, int length) {
        TaskInfo info = mTaskInfos.get(id);
        info.setFinished(progress);
        info.setLength(length);
        notifyDataSetChanged();//getView()重新调用
    }


    public static class ViewHolder {
        TextView tvFile;
        Button btStart, btPause, btResume, btCancel;
        ProgressBar pbFile;
        TextView tvPro;
    }
}
