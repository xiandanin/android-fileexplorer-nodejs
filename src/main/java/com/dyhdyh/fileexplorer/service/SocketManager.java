package com.dyhdyh.fileexplorer.service;

import org.springframework.util.StringUtils;

import java.io.IOException;

import javax.websocket.EncodeException;
import javax.websocket.Session;

/**
 * author  dengyuhan
 * created 2018/5/23 15:24
 */
public class SocketManager {
    private static Session session;

    public static Session getSession() {
        return session;
    }

    public static void setSession(Session session) {
        SocketManager.session = session;
    }


    public static void sendText(String text) {
        try {
            if (StringUtils.isEmpty(text)) {
                return;
            }
            session.getBasicRemote().sendText(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void sendObject(Object data) {
        try {
            if (data == null) {
                return;
            }
            session.getBasicRemote().sendObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncodeException e) {
            e.printStackTrace();
        }
    }
}
