package com.dyhdyh.fileexplorer.adb;

import com.dyhdyh.fileexplorer.model.ProgressInfo;
import com.dyhdyh.fileexplorer.utils.DateUtils;
import com.dyhdyh.fileexplorer.utils.FileUtils;

import org.springframework.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author  dengyuhan
 * created 2018/5/22 11:22
 */
public class ADB {
    private static SimpleDateFormat minuteFormat = new SimpleDateFormat("mm分ss秒");
    private static SimpleDateFormat hourFormat = new SimpleDateFormat("HH小时mm分ss秒");

    public static void pull(String remotePath, String localPath, OnReadLineListener<ProgressInfo> listener) {
        exec(String.format("adb pull %s %s", remotePath, localPath + File.separator + FileUtils.getFileName(remotePath)),
                new OnReadLineListener<String>() {
                    private int progress;

                    public void onReadLine(String line) {
                        if (listener != null) {
                            ProgressInfo info = transformerProgressInfo(line);
                            if (info != null) {
                                if (progress != info.getPercent()) {
                                    progress = info.getPercent();
                                    System.out.println(line);
                                    //listener.onReadLine(info);
                                }
                            }
                        }
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
            Thread t=new Thread(new InputStreamRunnable(process.getErrorStream(),"ErrorStream"));
            t.start();

            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            //
            DataInputStream dataInputStream = new DataInputStream(process.getInputStream());
            Reader inputStreamReader = new InputStreamReader(dataInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while (true) {
                String readLine = bufferedReader.readLine();
                if (StringUtils.isEmpty(readLine)) {
                    break;
                }
                if (listener != null) {
                    listener.onReadLine(readLine);
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

    private static ProgressInfo transformerProgressInfo(String line) {
        String percentRegex = "\\d+%";
        Matcher matcher = Pattern.compile(percentRegex).matcher(line);
        if (matcher.find()) {
            //进度百分比
            ProgressInfo info = new ProgressInfo();
            info.setCompleted(false);
            String path = line.substring(line.indexOf("/"));
            info.setPath(path);
            info.setPercent(Integer.parseInt(matcher.group().replace("%", "")));
            //info.setPercent(matcher.group());
            return info;
        } else {
            String numberRegex = "(\\d+(\\.\\d+)?)";
            Matcher numberMatcher = Pattern.compile(numberRegex).matcher(line);
            List<String> number = new ArrayList<>();
            while (numberMatcher.find()) {
                number.add(numberMatcher.group());
            }
            int numberCount = number.size();
            if (numberCount > 0) {
                ProgressInfo info = new ProgressInfo();
                String path = line.substring(0, line.indexOf(":"));
                info.setPath(path);
                info.setCompleted(true);
                for (int i = 0; i < numberCount; i++) {
                    long consumeTime = (long) (Float.parseFloat(number.get(numberCount - 1)) * 1000);
                    info.setConsumeTime(transformerConsumeTime(consumeTime));
                    String size = number.get(numberCount - 2);//.replace("bytes","").replace(" ","");
                    info.setFormatSumSize(FileUtils.formatSize(Long.parseLong(size)));
                    info.setSpeed(number.get(numberCount - 3) + "M/秒");
                }
                return info;
            }
        }
        return null;
    }


    private static String transformerConsumeTime(long time) {
        if (time <= DateUtils.SECOND_IN_MILLIS) {
            return time / DateUtils.SECOND_IN_MILLIS + "毫秒秒";
        } else if (time <= DateUtils.MINUTE_IN_MILLIS) {
            return time / DateUtils.MINUTE_IN_MILLIS + "秒";
        } else if (time <= DateUtils.HOUR_IN_MILLIS) {
            return minuteFormat.format(time);
        } else {
            return hourFormat.format(time);
        }
    }

    public interface OnReadLineListener<T> {
        void onReadLine(T line);
    }

    public static class InputStreamRunnable implements Runnable
    {
        BufferedReader bReader=null;
        String type=null;
        public InputStreamRunnable(InputStream is, String _type)
        {
            try
            {
                bReader=new BufferedReader(new InputStreamReader(new BufferedInputStream(is),"UTF-8"));
                type=_type;
            }
            catch(Exception ex)
            {
            }
        }
        public void run()
        {
            String line;
            int lineNum=0;

            try
            {
                while((line=bReader.readLine())!=null)
                {
                    lineNum++;
                    //Thread.sleep(200);
                }
                bReader.close();
            }
            catch(Exception ex)
            {
            }
        }
    }
}
