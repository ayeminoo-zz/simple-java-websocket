package com.amo.websocket.server;

import com.amo.websocket.api.Message;

/**
 * Created by ayeminoo on 1/7/18.
 */
public class TextMessage implements Message<String> {

    private String data = "";

    public TextMessage(byte[]data){
        this.data = new String(data);
    }

    @Override
    public String getData() {
        return data;
    }
}
