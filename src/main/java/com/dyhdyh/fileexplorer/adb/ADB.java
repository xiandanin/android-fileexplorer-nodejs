package com.dyhdyh.fileexplorer.adb;

import com.dyhdyh.fileexplorer.utils.FileUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * author  dengyuhan
 * created 2018/5/22 11:22
 */
public class ADB {

    public static List<String> getDevices() {
        List<String> devices = new ArrayList<String>();
        List<String> result = exec("adb devices");
        for (int i = 1; i < result.size() - 1; i++) {
            String line = result.get(i);
            //devices.add(line.split(" "));
        }
        return devices;
    }


    public static void pull(String remotePath, String localPath) {
        exec(String.format("adb pull %s %s", remotePath, localPath + File.separator + FileUtils.getFileName(remotePath)), new OnReadLineListener() {
            @Override
            public void onReadLine(String line) {

            }
        });
    }


    public static List<String> exec(String command) {
        return exec(command, null);
    }

    public static List<String> exec(String command, OnReadLineListener listener) {
        List<String> result = new ArrayList<String>();
        try {
            Process process = Runtime.getRuntime().exec(command);
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            //
            DataInputStream dataInputStream = new DataInputStream(process.getInputStream());
            Reader inputStreamReader = new InputStreamReader(dataInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while (true) {
                String readLine = bufferedReader.readLine();
                if (listener != null) {
                    listener.onReadLine(readLine);
                }
                if (readLine == null) {
                    break;
                }
                result.add(readLine);
            }
            dataOutputStream.close();
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public interface OnReadLineListener {
        void onReadLine(String line);
    }

}
