package com.dyhdyh.fileexplorer.encoder;

import com.google.gson.Gson;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * author  dengyuhan
 * created 2018/5/23 16:01
 */
public class GsonSocketServerEncoder implements Encoder.Text<Object> {
    private Gson gson = new Gson();

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(EndpointConfig arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public String encode(Object object) throws EncodeException {
        return gson.toJson(object);
    }

}
