package com.dyhdyh.fileexplorer.controller;

import com.dyhdyh.fileexplorer.adb.ADB;
import com.dyhdyh.fileexplorer.model.ProgressInfo;
import com.dyhdyh.fileexplorer.model.RemoteFileInfo;
import com.dyhdyh.fileexplorer.service.ADBService;
import com.dyhdyh.fileexplorer.service.ProgressSocketServer;
import com.dyhdyh.fileexplorer.service.SocketManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.List;

import se.vidstige.jadb.JadbDevice;

/**
 * author  dengyuhan
 * created 2018/5/22 13:54
 */
@Controller
public class MainController {

    @Autowired
    ADBService service;

    @Autowired
    ProgressSocketServer server;


    /**
     * ResponseBody样例
     *
     * @return
     */
    @RequestMapping("/")
    String getRemoteFileList(Model model, @RequestParam(required = false) String dir) {
        List<RemoteFileInfo> files;
        try {
            List<JadbDevice> devices = service.getDevices();
            service.selectDevice(devices.get(0));

            if (StringUtils.isEmpty(dir) || dir.equals("/")) {
                dir = ADBService.PATH_SDCARD;
                files = service.getSDCardList();
            } else {
                files = service.getSDCardList(dir);
            }
            model.addAttribute("files", files);
            model.addAttribute("currentPath", dir);
            model.addAttribute("parentPath", new File(dir).getParent());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "index";
    }

    @RequestMapping("api/pull")
    @ResponseBody
    boolean pullRemoteFile(Model model, @RequestParam String pullPath, @RequestParam String savePath) {
        try {
            //service.pull(pullPath, savePath);
            ADB.pull(pullPath, savePath, new ADB.OnReadLineListener<ProgressInfo>() {
                @Override
                public void onReadLine(ProgressInfo info) {
                    SocketManager.sendObject(info);
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
