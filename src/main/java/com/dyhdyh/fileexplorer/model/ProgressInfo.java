package com.dyhdyh.fileexplorer.model;

import java.io.Serializable;

/**
 * author  dengyuhan
 * created 2018/5/23 15:27
 */
public class ProgressInfo implements Serializable{
    private int percent;
    private String path;
    private String speed;
    private String consumeTime;
    private long sumSize;
    private String formatSumSize;
    private boolean completed;

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(String consumeTime) {
        this.consumeTime = consumeTime;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }


    public long getSumSize() {
        return sumSize;
    }

    public void setSumSize(long sumSize) {
        this.sumSize = sumSize;
    }

    public String getFormatSumSize() {
        return formatSumSize;
    }

    public void setFormatSumSize(String formatSumSize) {
        this.formatSumSize = formatSumSize;
    }
}
