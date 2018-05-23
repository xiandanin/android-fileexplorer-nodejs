package com.dyhdyh.fileexplorer.service;

import com.dyhdyh.fileexplorer.model.RemoteFileInfo;
import com.dyhdyh.fileexplorer.utils.FileUtils;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;

/**
 * author  dengyuhan
 * created 2018/5/22 13:55
 */
@Service
public class ADBService {
    public static final String PATH_SDCARD = "/sdcard";

    private JadbConnection adb;
    private JadbDevice mCurrentDevice;

    public ADBService() {
        this.adb = new JadbConnection();
    }


    public List<JadbDevice> getDevices() throws IOException, JadbException {
        return this.adb.getDevices();
    }

    public void selectDevice(JadbDevice currentDevice) {
        this.mCurrentDevice = currentDevice;
    }

    public List<RemoteFileInfo> getSDCardList() throws IOException, JadbException {
        return getSDCardList("");
    }

    public List<RemoteFileInfo> getSDCardList(String remotePath) throws IOException, JadbException {
        return getRemoteFileList(StringUtils.isEmpty(remotePath) ? PATH_SDCARD : remotePath);
    }

    public List<RemoteFileInfo> getRemoteFileList(String remotePath) throws IOException, JadbException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:dd:ss");
        List<RemoteFile> list = mCurrentDevice.list(remotePath);
        List<RemoteFileInfo> infos = new ArrayList<>();
        //加一个上级目录
        //infos.add(new RemoteFileInfo("...", new File(remotePath).getParent(), true));
        //排除只有.的路径
        String regex = "^\\.+$";
        Pattern pattern = Pattern.compile(regex);
        for (RemoteFile file : list) {
            if (pattern.matcher(file.getPath()).find()) {
                continue;
            }
            RemoteFileInfo info = new RemoteFileInfo();
            info.setPath(remotePath + File.separator + file.getPath());
            info.setSize(file.getSize());
            info.setFormatSize(FileUtils.formatSize(info.getSize()));
            info.setLastModified(file.getLastModified() * 1000);
            info.setName(FileUtils.getFileName(info.getPath()));
            info.setDirectory(file.isDirectory());
            info.setFormatLastModified(format.format(info.getLastModified()));
            infos.add(info);
        }
        return infos;
    }


    public void pull(String remotePath, String savePath) throws IOException, JadbException {
        mCurrentDevice.pull(new RemoteFile(remotePath), new File(savePath, FileUtils.getFileName(remotePath)));
    }

    public static String transformerPath(String path) {
        return String.format("'%s'", path);
    }
}
