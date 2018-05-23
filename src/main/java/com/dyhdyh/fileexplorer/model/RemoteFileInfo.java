package com.dyhdyh.fileexplorer.model;

/**
 * author  dengyuhan
 * created 2018/5/22 15:08
 */
public class RemoteFileInfo {
    private String path;
    private String name;
    private long size;
    private String formatSize;
    private long lastModified;
    private String formatLastModified;
    private boolean directory;

    public RemoteFileInfo() {
    }

    public RemoteFileInfo(String name, String path, boolean directory) {
        this.name = name;
        this.path = path;
        this.directory = directory;
    }

    public String getFormatLastModified() {
        return formatLastModified;
    }

    public void setFormatLastModified(String formatLastModified) {
        this.formatLastModified = formatLastModified;
    }

    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFormatSize() {
        return formatSize;
    }

    public void setFormatSize(String formatSize) {
        this.formatSize = formatSize;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }
}
