package com.yumikoazu.multitaskdownloader.entity;

import java.io.Serializable;

/**
 * Created by joker on 2015/7/18.
 */
public class TaskInfo implements Serializable {

    private int id;
    private String url;
    private String fileName;
    private int length;
    private int finished;
    private int status;


    public TaskInfo() {
    }

    public TaskInfo(int id, String url, String fileName, int length, int status, int finished) {
        this.id = id;
        this.status = status;
        this.finished = finished;
        this.length = length;
        this.fileName = fileName;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", length=" + length +
                ", finished=" + finished +
                ", status=" + status +
                '}';
    }
}
