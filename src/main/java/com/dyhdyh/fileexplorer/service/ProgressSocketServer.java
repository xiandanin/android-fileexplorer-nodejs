package com.dyhdyh.fileexplorer.service;

import com.dyhdyh.fileexplorer.encoder.GsonSocketServerEncoder;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * author  dengyuhan
 * created 2018/5/23 14:49
 */
@ServerEndpoint(value = "/progress", encoders = {GsonSocketServerEncoder.class})
@Component
public class ProgressSocketServer {


    @OnOpen
    public void open(Session session) {
        SocketManager.setSession(session);
        System.out.println("连接打开");
    }

    /**
     * 收到信息时触发
     *
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        System.out.println("收到消息--" + message);
    }

    /**
     * 连接关闭触发
     */
    @OnClose
    public void onClose() {
        System.out.println("连接关闭");
    }

    /**
     * 发生错误时触发
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("连接错误");
        error.printStackTrace();
    }
}
